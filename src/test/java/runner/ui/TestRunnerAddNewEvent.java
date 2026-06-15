package runner.ui;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/features/UI/Events/AddNewEvent.feature",
        glue = {"steps", "hooks"},
        monochrome = true,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/add-new-event.html",
                "json:target/cucumber-reports/add-new-event.json",
                "hooks.ReportHandler"
        }
)
public class TestRunnerAddNewEvent extends AbstractTestNGCucumberTests {
}
