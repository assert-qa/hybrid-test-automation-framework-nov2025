package reports;

import constants.ConstantGlobal;
import factory.DriverManager;
import managers.ConfigManager;
import helpers.SystemHelper;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AllureManager {
    // Text attachment
    @Attachment(value = "{0}", type = "text/plain")
    public static String saveTextLog(String message) {
        if (!ConfigManager.isAllureReportEnabled()) {
            return null;
        }
        return message;
    }

    // HTML attachment
    @Attachment(value = "{0}", type = "text/html")
    public static String attachHtml(String html) {
        if (!ConfigManager.isAllureReportEnabled()) {
            return null;
        }
        return html;
    }

    //Text attachments for Allure
    @Attachment(value = "Page screenshot", type = "image/png")
    public static byte[] saveScreenshotPNG() {
        if (!ConfigManager.isAllureReportEnabled()) {
            return new byte[0];
        }
        return ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
    }

    // ...existing code...

    // ========== ENHANCEMENT: Environment Information ==========
    @Attachment(value = "Test Environment Information", type = "text/plain")
    public static String attachEnvironmentInfo() {
        if (!ConfigManager.isAllureReportEnabled()) {
            return null;
        }
        StringBuilder info = new StringBuilder();
        info.append("=== TEST ENVIRONMENT INFORMATION ===\n\n");

        // Use environment from ConfigManager
        String environment = ConfigManager.getEnvironment() != null ?
            ConfigManager.getEnvironment() : (ConstantGlobal.ENV != null ? ConstantGlobal.ENV : "N/A");

        info.append("Environment: ").append(environment).append("\n");
        info.append("Base URL: ").append(ConfigManager.getBaseUrl() != null ? ConfigManager.getBaseUrl() : "N/A").append("\n");
        info.append("Browser: ").append(ConfigManager.getBrowser() != null ? ConfigManager.getBrowser() : "N/A").append("\n");
        info.append("Headless Mode: ").append(ConfigManager.isHeadless() ? "On" : "Off").append("\n");
        info.append("Execution Time: ").append(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))).append("\n");
        return info.toString();
    }

    // ========== ENHANCEMENT: User Account Information ==========
    @Attachment(value = "Test User Account Information", type = "text/plain")
    public static String attachUserAccountInfo() {
        if (!ConfigManager.isAllureReportEnabled()) {
            return null;
        }
        StringBuilder userInfo = new StringBuilder();
        userInfo.append("=== TEST USER ACCOUNT INFORMATION ===\n\n");

        // Use email and environment from ConfigManager
        String email = ConfigManager.getValidLoginEmail() != null ?
            ConfigManager.getValidLoginEmail() : (ConstantGlobal.VALID_EMAIL != null ? ConstantGlobal.VALID_EMAIL : "N/A");
        String environment = ConfigManager.getEnvironment() != null ?
            ConfigManager.getEnvironment() : (ConstantGlobal.ENV != null ? ConstantGlobal.ENV : "N/A");

        userInfo.append("Email: ").append(email).append("\n");
        userInfo.append("Account Type: ").append(getAccountType()).append("\n");
        userInfo.append("Environment: ").append(environment).append("\n");
        userInfo.append("Test Data Source: Environment Properties\n");
        userInfo.append("Credentials Loaded From: env/").append(environment).append(".properties\n");
        return userInfo.toString();
    }

    // ========== ENHANCEMENT: System Configuration ==========
    @Attachment(value = "System Configuration", type = "text/plain")
    public static String attachSystemConfig() {
        if (!ConfigManager.isAllureReportEnabled()) {
            return null;
        }
        StringBuilder config = new StringBuilder();
        config.append("=== SYSTEM CONFIGURATION ===\n\n");
        config.append("Operating System: ").append(SystemHelper.getOSName()).append("\n");
        config.append("Java Version: ").append(System.getProperty("java.version")).append("\n");
        config.append("Browser: ").append(ConstantGlobal.BROWSER).append("\n");
        config.append("Explicit Wait Timeout: ").append(ConstantGlobal.EXPLICIT_TIMEOUT).append(" seconds\n");
        config.append("Page Load Timeout: ").append(ConstantGlobal.PAGE_LOAD_TIMEOUT).append(" seconds\n");
        config.append("Hard Wait Timeout: ").append(ConstantGlobal.HARD_WAIT_TIMEOUT).append(" seconds\n");
        return config.toString();
    }

    // ========== ENHANCEMENT: Complete Test Context ==========
    @Attachment(value = "Complete Test Context Information", type = "text/plain")
    public static String attachCompleteTestContext() {
        if (!ConfigManager.isAllureReportEnabled()) {
            return null;
        }
        StringBuilder context = new StringBuilder();
        context.append("=== COMPLETE TEST CONTEXT ===\n\n");
        context.append(attachEnvironmentInfo()).append("\n");
        context.append(attachUserAccountInfo()).append("\n");
        context.append(attachSystemConfig());
        return context.toString();
    }

    // ========== ENHANCEMENT: Test Result Summary ==========
    @Attachment(value = "Test Result Summary", type = "text/plain")
    public static String attachTestResultSummary(String testName, String status) {
        if (!ConfigManager.isAllureReportEnabled()) {
            return null;
        }
        StringBuilder summary = new StringBuilder();
        summary.append("=== TEST RESULT SUMMARY ===\n\n");
        summary.append("Test Name: ").append(testName).append("\n");
        summary.append("Status: ").append(status).append("\n");

        // Use email and environment from ConfigManager
        String email = ConfigManager.getValidLoginEmail() != null ?
            ConfigManager.getValidLoginEmail() : (ConstantGlobal.VALID_EMAIL != null ? ConstantGlobal.VALID_EMAIL : "N/A");
        String environment = ConfigManager.getEnvironment() != null ?
            ConfigManager.getEnvironment() : (ConstantGlobal.ENV != null ? ConstantGlobal.ENV : "N/A");

        summary.append("Executed By: ").append(email).append("\n");
        summary.append("Environment: ").append(environment).append("\n");
        summary.append("Execution Date: ").append(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))).append("\n");
        summary.append("Browser: ").append(ConfigManager.getBrowser() != null ? ConfigManager.getBrowser() : "N/A").append("\n");
        return summary.toString();
    }

    // ========== HELPER METHOD ==========
    private static String getAccountType() {
        // ========== FIXED: Add null check ==========
        if (ConstantGlobal.ENV == null) {
            System.err.println("WARNING: ConstantGlobal.ENV is NULL!");
            return "Unknown Account";
        }

        String env = ConstantGlobal.ENV.toLowerCase();
        if (env.contains("prod")) {
            return "Production Account";
        } else if (env.contains("staging")) {
            return "Staging Account";
        } else if (env.contains("dev")) {
            return "Development Account";
        }
        return "Test Account (" + env + ")";
    }
}
