package runner.api;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/features/API",
        glue = {"steps", "hooks"},
        tags = "@api",
        monochrome = true,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/api-report.html",
                "json:target/cucumber-reports/api-report.json",
                "hooks.ReportHandler"
        }
)
public class TestRunnerAPI extends AbstractTestNGCucumberTests {
}
