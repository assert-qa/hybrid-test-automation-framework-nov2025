package runner.ui;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/features/UI/Events/SearchEvent.feature",
        glue = {"steps", "hooks"},
        monochrome = true,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/search-event.html",
                "json:target/cucumber-reports/search-event.json",
                "hooks.ReportHandler"
        }
)
public class TestRunnerSearchEvent extends AbstractTestNGCucumberTests {
}
