package runner.ui;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/features/UI/Events/SearchNotFound.feature",
        glue = {"steps", "hooks"},
        monochrome = true,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/search-not-found.html",
                "json:target/cucumber-reports/search-not-found.json",
                "hooks.ReportHandler"
        }
)
public class TestRunnerSearchEventNotFound extends AbstractTestNGCucumberTests {
}
