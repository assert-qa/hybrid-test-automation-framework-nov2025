package runner.ui;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/features/UI/Login/LoginWithRegisteredCredentials.feature",
        glue = {"steps", "hooks"},
        monochrome = true,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/login-with-registered-credentials.html",
                "json:target/cucumber-reports/login-with-registered-credentials.json",
                "hooks.ReportHandler"
        }
)
public class TestRunnerLoginWithRegisteredCredentials extends AbstractTestNGCucumberTests {
}
