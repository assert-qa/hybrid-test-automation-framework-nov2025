package utils;

import factory.DriverManager;
import managers.ConfigManager;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;

public class UploadUtils {
    private static final long EXPLICIT_TIMEOUT = ConfigManager.getExplicitWaitTimeout();

    public static void uploadFile(By by, String filePath) {
        WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(EXPLICIT_TIMEOUT), Duration.ofMillis(500));

        File file = new File(filePath);
        if (!file.exists()) {
            LogUtils.error("File not found: " + filePath);
            throw new RuntimeException("File not found: " + filePath + ". Please ensure the file exists in the correct location.");
        }

        LogUtils.info("File exists, proceeding with upload: " + filePath);
        wait.until(ExpectedConditions.presenceOfElementLocated(by)).sendKeys(filePath);
        LogUtils.info("Upload file: " + filePath + " on element " + by);
    }
}
