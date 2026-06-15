package runner.ui;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/features/UI/MyBookings/ViewBookedEventDetails.feature",
        glue = {"steps", "hooks"},
        monochrome = true,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/view-booked-event-details.html",
                "json:target/cucumber-reports/view-booked-event-details.json",
                "hooks.ReportHandler"
        }
)
public class TestRunnerViewBookedEventDetails extends AbstractTestNGCucumberTests {
}
