package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/features/Events/SearchNotFound.feature",
        glue = {"steps", "hooks"},
        monochrome = true,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/search-not-found.html",
                "json:target/cucumber-reports/search-not-found.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
                "hooks.CucumberReportListener"
        }
)
public class TestRunnerSearchEventNotFound extends AbstractTestNGCucumberTests {
}
