package runner.ui;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/features/UI/MyBookings/EmptyBookingState.feature",
        glue = {"steps", "hooks"},
        monochrome = true,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/empty-booking-state.html",
                "json:target/cucumber-reports/empty-booking-state.json",
                "hooks.ReportHandler"
        }
)
public class TestRunnerEmptyBookingState extends AbstractTestNGCucumberTests {
}
