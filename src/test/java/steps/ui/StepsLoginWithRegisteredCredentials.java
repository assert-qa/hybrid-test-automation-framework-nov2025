package steps.ui;

import managers.ConfigManager;
import helpers.UserInfoHelper;
import hooks.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import keywords.WebUI;
import pages.LoginPage;
import pages.models.CredentialsDataObject;
import reports.AllureManager;
import utils.LogUtils;

public class StepsLoginWithRegisteredCredentials {
    private TestContext testContext;
    private LoginPage loginPage;

    public StepsLoginWithRegisteredCredentials(TestContext testContext) {
        this.testContext = testContext;
        this.loginPage = new LoginPage();
    }

    public StepsLoginWithRegisteredCredentials(){
        this(new TestContext());
    }

    @When("I enter registered email address and password")
    public void iEnterCorrectEmailAddressAndPassword() {
        // Get credentials from ConfigManager
        String userEmail = ConfigManager.getValidLoginEmail();
        String userPassword = ConfigManager.getValidLoginPassword();

        LogUtils.info("Logging in with account: " + userEmail);
        LogUtils.info("Account Type: " + UserInfoHelper.getUserAccountType());
        LogUtils.info("Environment: " + ConfigManager.getEnvironment());

        CredentialsDataObject credentialsData = CredentialsDataObject.builder()
                .userEmail(userEmail)
                .userPassword(userPassword)
                .build();

        loginPage.loginAccount(credentialsData);

        // Attach user action to Allure
        if (ConfigManager.isAllureReportEnabled()) {
            AllureManager.attachUserAccountInfo();
        }
    }

    @Then("I verify that {string} is visible")
    public void iVerifyThatIsVisible(String expectedText) {
        // Get email from ConfigManager
        String userEmail = ConfigManager.getValidLoginEmail();
        String actualText = loginPage.getSuccessLogin();

        if ("Logged in as user email".equalsIgnoreCase(expectedText.trim())) {
            WebUI.verifyEquals(actualText, userEmail,
                    "Success login label is not showing the expected user email.");
            return;
        }

        WebUI.verifyEquals(actualText, expectedText,
                "Success login label is different from expected text.");
    }
}
