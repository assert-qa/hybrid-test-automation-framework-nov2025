package runner.ui;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/features/UI/Events/ViewEventDetail.feature",
        glue = {"steps", "hooks"},
        monochrome = true,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/view-event-detail.html",
                "json:target/cucumber-reports/view-event-detail.json",
                "hooks.ReportHandler"
        }
)
public class TestRunnerViewEventDetail extends AbstractTestNGCucumberTests {
}
