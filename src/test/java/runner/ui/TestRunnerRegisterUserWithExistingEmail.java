package runner.ui;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/features/UI/Login/RegisterUserWithExistingEmail.feature",
        glue = {"steps", "hooks"},
        monochrome = true,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/register-user-with-existing-email.html",
                "json:target/cucumber-reports/register-user-with-existing-email.json",
                "hooks.ReportHandler"
        }
)
public class TestRunnerRegisterUserWithExistingEmail extends AbstractTestNGCucumberTests {
}
