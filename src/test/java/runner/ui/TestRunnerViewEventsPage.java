package runner.ui;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/features/UI/Events/ViewEventsPage.feature",
        glue = {"steps", "hooks"},
        monochrome = true,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/view-event-page.html",
                "json:target/cucumber-reports/view-event-page.json",
                "hooks.ReportHandler"
        }
)
public class TestRunnerViewEventsPage extends AbstractTestNGCucumberTests {
}
