package runner.ui;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/java/features/UI/Login/LogoutUser.feature",
        glue = {"steps", "hooks"},
        monochrome = true,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/logout-user.html",
                "json:target/cucumber-reports/logout-user.json",
                "hooks.ReportHandler"
        }
)
public class TestRunnerLogoutUser extends AbstractTestNGCucumberTests {
}
