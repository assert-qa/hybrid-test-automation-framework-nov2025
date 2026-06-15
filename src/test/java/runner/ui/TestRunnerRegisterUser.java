package runner.ui;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/features/UI/Login/RegisterUser.feature",
        glue = {"steps", "hooks"},
        monochrome = true,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/register-user.html",
                "json:target/cucumber-reports/register-user.json",
                "hooks.ReportHandler"
        }
)
public class TestRunnerRegisterUser extends AbstractTestNGCucumberTests {
}
