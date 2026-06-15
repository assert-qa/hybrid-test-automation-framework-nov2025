package runner.ui;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/features/UI/MyBookings/CancelBooking.feature",
        glue = {"steps", "hooks"},
        monochrome = true,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cancel-booking.html",
                "json:target/cucumber-reports/cancel-booking.json",
                "hooks.ReportHandler"
        }
)

public class TestRunnerCancelBookedEvent extends AbstractTestNGCucumberTests {
}
