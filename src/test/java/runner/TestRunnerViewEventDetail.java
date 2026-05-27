package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/features/Events/ViewEventDetail.feature",
        glue = {"steps", "hooks"},
        monochrome = true,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/view-event-detail.html",
                "json:target/cucumber-reports/view-event-detail.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
                "hooks.CucumberReportListener"
        }
)
public class TestRunnerViewEventDetail extends AbstractTestNGCucumberTests {
}
