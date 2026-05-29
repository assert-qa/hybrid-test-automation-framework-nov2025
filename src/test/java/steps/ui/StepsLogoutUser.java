package steps.ui;

import constants.ConstantGlobal;
import hooks.TestContext;
import io.cucumber.java.en.Then;
import keywords.WebUI;
import pages.LoginPage;

public class StepsLogoutUser {
    private TestContext testContext;
    private LoginPage loginPage;

    public StepsLogoutUser(TestContext testContext) {
        this.testContext = testContext;
        this.loginPage = new LoginPage();
    }

    public StepsLogoutUser(){
        this(new TestContext());
    }

    @Then("I should be redirected to the login page")
    public void verifyUserRedirectedToLoginPage() {
        String actualUrl = ConstantGlobal.BASE_URL;
        String expectedUrl = WebUI.getCurrentUrl();

        WebUI.verifyEquals(actualUrl, expectedUrl,
                "User is NOT redirected to Login Page");
    }
}
