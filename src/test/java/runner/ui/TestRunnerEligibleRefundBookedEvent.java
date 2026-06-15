package runner.ui;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/features/UI/MyBookings/EligibleRefundBookedEvent.feature",
        glue = {"steps", "hooks"},
        monochrome = true,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/eligible-refund-booked-event.html",
                "json:target/cucumber-reports/eligible-refund-booked-event.json",
                "hooks.ReportHandler"
        }
)
public class TestRunnerEligibleRefundBookedEvent extends AbstractTestNGCucumberTests {
}
