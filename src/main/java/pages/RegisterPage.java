package pages;

import factory.DriverFactory;
import keywords.WebUI;
import org.openqa.selenium.By;
import pages.dto.RegisterFormDataObject;

import java.util.Properties;

import static helpers.PropertiesHelper.loadAllFiles;

public class RegisterPage extends DriverFactory {
    Properties setUp = loadAllFiles();

    public RegisterPage(){

    }

    String registerEmail = setUp.getProperty("REGISTER_EMAIL");
    String registerPassword = setUp.getProperty("REGISTER_PASSWORD");
    String registerConfirmPassword = setUp.getProperty("REGISTER_CONF_PASSWORD");
    String createAccountButton = setUp.getProperty("CREATE_ACCOUNT_BUTTON");
    String registerPage = setUp.getProperty("NAVIGATE_TO_REGISTER_PAGE");
    String successLoginLabel = setUp.getProperty("SUCCESS_LOGIN_LABEL");
    String errorToastMessage = setUp.getProperty("ERROR_TOAST_MESSAGE");

    public void goToRegisterPage(){
        WebUI.clickElement(By.xpath(registerPage));
    }

    public void setRegisterAccount(RegisterFormDataObject data){
        if (data.getEmail() != null)
            WebUI.setText(By.xpath(registerEmail), data.getEmail());
        if (data.getPassword() != null)
            WebUI.setText(By.xpath(registerPassword), data.getPassword());
        if (data.getConfirmPassword() != null)
            WebUI.setText(By.xpath(registerConfirmPassword), data.getConfirmPassword());
    }

    public void createAccountButton(){
        WebUI.clickElement(By.xpath(createAccountButton));
    }

    public String getSuccessRegistered(){
        return WebUI.getElementText(By.xpath(successLoginLabel));
    }

    public String getErrorToastMessage(){
        return WebUI.getElementText(By.xpath(errorToastMessage));
    }
}