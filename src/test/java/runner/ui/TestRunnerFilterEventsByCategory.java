package runner.ui;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/features/UI/Events/FilterEventByCategory.feature",
        glue = {"steps", "hooks"},
        monochrome = true,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/filter-event-by-category.html",
                "json:target/cucumber-reports/filter-event-by-category.json",
                "hooks.ReportHandler"
        }
)
public class TestRunnerFilterEventsByCategory extends AbstractTestNGCucumberTests {
}
