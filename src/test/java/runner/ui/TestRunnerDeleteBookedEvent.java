package runner.ui;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/features/UI/MyBookings/DeleteBookedEvent.feature",
        glue = {"steps", "hooks"},
        monochrome = true,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/delete-booked-event.html",
                "json:target/cucumber-reports/delete-booked-event.json",
                "hooks.ReportHandler"
        }
)
public class TestRunnerDeleteBookedEvent extends AbstractTestNGCucumberTests {
}
