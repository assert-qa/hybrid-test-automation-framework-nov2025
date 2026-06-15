package runner.ui;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/features/UI/Events/CombinationFilter.feature",
        glue = {"steps", "hooks"},
        monochrome = true,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/combination-filter.html",
                "json:target/cucumber-reports/combination-filter.json",
                "hooks.ReportHandler"
        }
)
public class TestRunnerFilterEventCombination extends AbstractTestNGCucumberTests {
}
