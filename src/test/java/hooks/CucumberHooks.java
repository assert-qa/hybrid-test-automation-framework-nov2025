package hooks;

import constants.ConstantGlobal;
import api.context.ApiTestContext;
import factory.DriverFactory;
import factory.DriverManager;
import managers.ConfigManager;
import helpers.PropertiesHelper;
import helpers.UserInfoHelper;
import io.cucumber.java.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import pages.MyBookingPage;
import reports.AllureManager;
import reports.ExtentTestManager;
import utils.LogUtils;

public class CucumberHooks {
    private static final String SEPARATOR = "=".repeat(40);
    private static final String API_TAG = "@api";

    @BeforeAll
    public static void beforeAll() {
        LogUtils.info(SEPARATOR);
        LogUtils.info("STARTING TEST SUITE");
        LogUtils.info(SEPARATOR);
        PropertiesHelper.loadAllFiles();

        // Log configuration info
        try {
            String environment = ConfigManager.getEnvironment();
            String baseUrl = ConfigManager.getBaseUrl();
            String email = ConfigManager.getValidLoginEmail();

            LogUtils.info("Configuration loaded successfully");
            LogUtils.info("Environment: " + environment);
            LogUtils.info("Base URL: " + baseUrl);
            LogUtils.info("Test User Email: " + email);
            LogUtils.info("Headless mode " + (ConfigManager.isHeadless() ? "On" : "Off"));
        } catch (Exception e) {
            LogUtils.warn("Failed to load configuration: " + e.getMessage());
        }

        // Log user info at suite level
        LogUtils.info(UserInfoHelper.getUserTestHeader());
    }

    @AfterAll
    public static void afterAll() {
        LogUtils.info(SEPARATOR);
        LogUtils.info("TEST SUITE COMPLETED");
        LogUtils.info(SEPARATOR);
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        LogUtils.info("SCENARIO START: " + scenario.getName());
        LogUtils.info("SCENARIO STATUS: Starting");

        boolean apiScenario = isApiScenario(scenario);
        if (!apiScenario && DriverManager.getDriver() == null) {
            new DriverFactory().createDriver();
            LogUtils.info("Driver initialized for scenario: " + scenario.getName());
        } else if (apiScenario) {
            LogUtils.info("API scenario detected. Skipping browser initialization.");
        }

        try {
            String testName = scenario.getName();
            if (ConfigManager.isExtentReportEnabled()) {
                ExtentTestManager.createTest(testName);
                LogUtils.info("ExtentTest initialized for: " + testName);
            }

            // Log scenario info to ExtentReport
            if (ConfigManager.isExtentReportEnabled() && ExtentTestManager.getTest() != null) {
                ExtentTestManager.getTest().info("Starting scenario: " + testName);

                // Log user info from ConfigManager
                String userEmail = ConfigManager.getValidLoginEmail() != null ?
                    ConfigManager.getValidLoginEmail() : ConstantGlobal.VALID_EMAIL;
                String environment = ConfigManager.getEnvironment() != null ?
                    ConfigManager.getEnvironment() : ConstantGlobal.ENV;
                String accountType = UserInfoHelper.getUserAccountType();

                ExtentTestManager.getTest().info("Test User: " + userEmail);
                ExtentTestManager.getTest().info("Environment: " + environment);
                ExtentTestManager.getTest().info("Account Type: " + accountType);
            }

            // Attach user info to Allure Report
            if (ConfigManager.isAllureReportEnabled()) {
                AllureManager.attachEnvironmentInfo();
                AllureManager.attachUserAccountInfo();
            }

        } catch (Exception e) {
            LogUtils.info("Failed to initialize scenario report data: " + e.getMessage());
        }
    }

    @After
    public void afterScenario(Scenario scenario) {
        String status = "PASSED";
        try {
            Object statusObj = scenario.getClass().getMethod("getStatus").invoke(scenario);
            if (statusObj != null) {
                status = statusObj.toString();
            } else {
                status = scenario.isFailed() ? "FAILED" : "PASSED";
            }
        } catch (NoSuchMethodException nsme) {
            status = scenario.isFailed() ? "FAILED" : "PASSED";
        } catch (Exception e) {
            status = scenario.isFailed() ? "FAILED" : "PASSED";
        }

        try {
            if ("FAILED".equalsIgnoreCase(status) && DriverManager.getDriver() != null) {
                byte[] screenshot = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Failed Screenshot");

                if (ConfigManager.isAllureReportEnabled()) {
                    AllureManager.saveScreenshotPNG();
                }

                LogUtils.error("Screenshot captured for failed scenario: " + scenario.getName());
            } else if ("FAILED".equalsIgnoreCase(status)) {
                LogUtils.info("No WebDriver active. Skipping failed scenario screenshot.");
            }

            if (ConfigManager.isExtentReportEnabled() && ExtentTestManager.getTest() != null) {
                if ("FAILED".equalsIgnoreCase(status)) {
                    ExtentTestManager.getTest().fail("Scenario Failed: " + scenario.getName());

                    // Attach summary on failure
                    if (ConfigManager.isAllureReportEnabled()) {
                        AllureManager.attachTestResultSummary(scenario.getName(), "FAILED");
                    }
                } else if ("SKIPPED".equalsIgnoreCase(status) || "UNKNOWN".equalsIgnoreCase(status)) {
                    ExtentTestManager.getTest().skip("Scenario Skipped: " + scenario.getName());
                } else {
                    ExtentTestManager.getTest().pass("Scenario Passed: " + scenario.getName());
                    if (ConfigManager.isAllureReportEnabled()) {
                        AllureManager.attachTestResultSummary(scenario.getName(), "PASSED");
                    }
                }
            } else if (ConfigManager.isAllureReportEnabled()) {
                String resultStatus = "FAILED".equalsIgnoreCase(status) ? "FAILED" : "PASSED";
                AllureManager.attachTestResultSummary(scenario.getName(), resultStatus);
            }
        } catch (Exception e) {
            LogUtils.error("Failed to log scenario status to report: " + e.getMessage());
        }

        LogUtils.info("SCENARIO FINISHED: " + scenario.getName());
        LogUtils.info("SCENARIO STATUS: " + status);

        clearTestBookingsIfCreated();

        DriverManager.quit();
        LogUtils.info(SEPARATOR + "\n");

        TestContext.reset();
        ApiTestContext.reset();
    }

    private boolean isApiScenario(Scenario scenario) {
        return scenario.getSourceTagNames().contains(API_TAG);
    }

    private void clearTestBookingsIfCreated() {
        if (DriverManager.getDriver() == null) {
            return;
        }

        if (new TestContext().getBookingData() == null) {
            return;
        }

        try {
            MyBookingPage myBookingPage = new MyBookingPage();
            myBookingPage.goToMyBookingPage();

            if (myBookingPage.isClearAllBookingsTextButtonDisplayed()) {
                myBookingPage.clickClearAllBookingsTextButton();
                myBookingPage.confirmBookingDeletion();
                LogUtils.info("Test bookings cleaned up successfully.");
            } else {
                LogUtils.info("No clear all bookings button displayed. Skipping booking cleanup.");
            }
        } catch (Exception e) {
            LogUtils.warn("Failed to clean up test bookings: " + e.getMessage());
        }
    }
}
