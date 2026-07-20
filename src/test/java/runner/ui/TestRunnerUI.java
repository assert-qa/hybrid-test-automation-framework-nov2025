package runner.ui;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/features/UI",
        glue = {"steps", "hooks"},
        tags = "@ui",
        monochrome = true,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/ui-report.html",
                "json:target/cucumber-reports/ui-report.json",
                "hooks.ReportHandler"
        }
)
public class TestRunnerUI extends AbstractTestNGCucumberTests {
}
