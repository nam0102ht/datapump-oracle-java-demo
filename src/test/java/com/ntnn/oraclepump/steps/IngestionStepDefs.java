package com.ntnn.oraclepump.steps;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class IngestionStepDefs {

    @LocalServerPort
    private int port;

    private final JdbcTemplate jdbcTemplate;

    private Response lastResponse;
    private File testFile;

    // ── Given ─────────────────────────────────────────────────────────────────

    @Given("the ingestion API is running")
    public void apiIsRunning() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Given("a valid DTFT file with {int} records")
    public void validDtftFile(int count) throws Exception {
        testFile = buildValidFile(count);
    }

    @Given("a DTFT file with {int} valid records and {int} invalid records")
    public void mixedDtftFile(int valid, int invalid) throws Exception {
        testFile = buildMixedFile(valid, invalid);
    }

    @Given("a DTFT file containing MachineID {int} and MachineID {int}")
    public void fileWithSpecificMachineIds(int id1, int id2) throws Exception {
        var content = """
            MachineID|Timestamp|Temperature|Pressure|Status|Operator
            %d|2026-07-05T10:00:00|85.5|101.3|RUNNING|Op1
            %d|2026-07-05T10:01:00|86.0|102.0|RUNNING|Op2
            """.formatted(id1, id2);
        testFile = writeTempFile(content);
    }

    @Given("an empty DTFT file")
    public void emptyFile() throws Exception {
        testFile = writeTempFile("");
    }

    // ── When ──────────────────────────────────────────────────────────────────

    @When("I upload the file to the ingest endpoint")
    public void uploadFile() {
        lastResponse = RestAssured.given()
            .multiPart("file", testFile, "text/plain")
            .when()
            .post("/api/v1/ingest");
    }

    // ── Then ──────────────────────────────────────────────────────────────────

    @Then("the response status is {int}")
    public void responseStatus(int expected) {
        assertThat(lastResponse.statusCode()).isEqualTo(expected);
    }

    @Then("the job status is {string}")
    public void jobStatus(String expected) {
        assertThat(lastResponse.jsonPath().getString("status")).isEqualTo(expected);
    }

    @Then("rows_inserted is {long}")
    public void rowsInserted(long expected) {
        assertThat(lastResponse.jsonPath().getLong("rowsInserted")).isEqualTo(expected);
    }

    @Then("rows_failed is {long}")
    public void rowsFailed(long expected) {
        assertThat(lastResponse.jsonPath().getLong("rowsFailed")).isEqualTo(expected);
    }

    @Then("MachineID {int} is stored in shard table {string}")
    public void machineIdInShard(int machineId, String tableName) {
        var count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM " + tableName + " WHERE machine_id = ?",
            Integer.class, (long) machineId);
        assertThat(count).isGreaterThan(0);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private File buildValidFile(int count) throws Exception {
        var sb = new StringBuilder("MachineID|Timestamp|Temperature|Pressure|Status|Operator\n");
        for (int i = 0; i < count; i++) {
            sb.append("%d|2026-07-05T%02d:%02d:00|%.2f|%.3f|RUNNING|Op%d\n"
                .formatted(10000L + i, (i / 60) % 24, i % 60, 80.0 + (i % 20), 100.0 + i * 0.01, i % 10));
        }
        return writeTempFile(sb.toString());
    }

    private File buildMixedFile(int valid, int invalid) throws Exception {
        var sb = new StringBuilder("MachineID|Timestamp|Temperature|Pressure|Status|Operator\n");
        for (int i = 0; i < valid; i++) {
            sb.append("%d|2026-07-05T10:%02d:00|85.5|101.3|RUNNING|Op%d\n"
                .formatted(10000L + i, i % 60, i));
        }
        for (int i = 0; i < invalid; i++) {
            sb.append("%d|2026-07-05T10:%02d:00|INVALID|101.3|RUNNING|Op%d\n"
                .formatted(20000L + i, i % 60, i));
        }
        return writeTempFile(sb.toString());
    }

    private File writeTempFile(String content) throws Exception {
        var tmp = Files.createTempFile("dtft-test-", ".dtft");
        Files.writeString(tmp, content);
        tmp.toFile().deleteOnExit();
        return tmp.toFile();
    }
}
