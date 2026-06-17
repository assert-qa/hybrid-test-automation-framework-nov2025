package pages;

import factory.DriverFactory;
import keywords.WebUI;
import org.openqa.selenium.By;
import pages.dto.CredentialsDataObject;

import java.util.Properties;

import static helpers.PropertiesHelper.loadAllFiles;

public class LoginPage extends DriverFactory {
    Properties setUp = loadAllFiles();

    public LoginPage(){
    }

    String email = setUp.getProperty("EMAIL");
    String password = setUp.getProperty("PASSWORD");
    String sigInButton = setUp.getProperty("SIGN_IN_BUTTON");
    String successLoginLabel = setUp.getProperty("SUCCESS_LOGIN_LABEL");
    String logOutButton = setUp.getProperty("LOG_OUT_BUTTON");
    String errorToastMessage = setUp.getProperty("ERROR_TOAST_MESSAGE");

    public void loginAccount(CredentialsDataObject data){
        if (data.getUserEmail() != null)
            WebUI.setText(By.xpath(email), data.getUserEmail());
        if (data.getUserPassword() != null)
            WebUI.setText(By.xpath(password), data.getUserPassword());
    }

    public void clickSignInButton(){
        WebUI.clickElement(By.xpath(sigInButton));
    }

    public String getSuccessLogin(){
        return WebUI.getElementText(By.xpath(successLoginLabel));
    }

    public void clickLogOutButton(){
        WebUI.clickElement(By.xpath(logOutButton));
    }

    public String getErrorToastMessage(){
        // Wait for toast container to be present first
        By toastContainer = By.cssSelector("div[aria-live='polite']");
        WebUI.waitForElementPresent(toastContainer, 10);

        // Then wait for the actual message to be visible
        WebUI.waitForElementVisible(By.xpath(errorToastMessage), 10);
        return WebUI.getElementText(By.xpath(errorToastMessage));
    }
}