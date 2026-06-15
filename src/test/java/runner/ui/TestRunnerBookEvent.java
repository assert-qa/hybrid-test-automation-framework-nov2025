package runner.ui;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/features/UI/MyBookings/BookEvent.feature",
        glue = {"steps", "hooks"},
        monochrome = true,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/book-event.html",
                "json:target/cucumber-reports/book-event.json",
                "hooks.ReportHandler"
        }
)
public class TestRunnerBookEvent extends AbstractTestNGCucumberTests {
}
