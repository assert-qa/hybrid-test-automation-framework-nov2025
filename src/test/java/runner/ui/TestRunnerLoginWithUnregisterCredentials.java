package runner.ui;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/features/UI/Login/LoginWithUnregisterCredentials.feature",
        glue = {"steps", "hooks"},
        monochrome = true,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/login-with-unregister-credentials.html",
                "json:target/cucumber-reports/login-with-unregister-credentials.json",
                "hooks.ReportHandler"
        }
)
public class TestRunnerLoginWithUnregisterCredentials  extends AbstractTestNGCucumberTests {
}
