package managers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigManager - Manages environment-specific configuration
 * Loads properties from src/main/resources/env/{environment}.properties
 * Supports CI/CD override via system property: -Denv=staging
 */
public class ConfigManager {
    private static Properties config;
    private static Properties baseConfig;

    private static String environment;
    private static final String DEFAULT_ENV = "dev";
    private static final String DEFAULT_TEST_SUITE = "Regression Suite";
    private static final String BASE_CONFIG_PATH = "src/main/resources/config.properties";
    private static final String ENV_CONFIG_PATH = "src/main/resources/env/";

    static {
        loadConfiguration();
    }

    private ConfigManager() {
        // Private constructor to prevent instantiation
    }

    private static void loadConfiguration() {
        try {
            // Get environment from system property or use default
            environment = System.getProperty("env", DEFAULT_ENV).toLowerCase();
            System.out.println("Loading configuration for environment: " + environment);

            // Load base configuration
            baseConfig = new Properties();
            try (InputStream baseInput = new FileInputStream(BASE_CONFIG_PATH)) {
                baseConfig.load(baseInput);
                System.out.println("Base configuration loaded from: " + BASE_CONFIG_PATH);
            }

            // Load environment-specific configuration
            config = new Properties();
            String envConfigFile = ENV_CONFIG_PATH + environment + ".properties";
            try (InputStream envInput = new FileInputStream(envConfigFile)) {
                config.load(envInput);
                System.out.println("Environment configuration loaded from: " + envConfigFile);
            }

            // Merge base config into environment config (env config takes priority)
            for (String key : baseConfig.stringPropertyNames()) {
                if (!config.containsKey(key)) {
                    config.setProperty(key, baseConfig.getProperty(key));
                }
            }

            System.out.println("Configuration loaded successfully for environment: " + environment);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration for environment: " + environment, e);
        }
    }

    // Get current environment name
    public static String getEnvironment() {
        return environment;
    }

    // Get base URL for the application
    public static String getBaseUrl() {
        return getProperty("BASE_URL");
    }

    // Get browser name
    public static String getBrowser() {
        return getProperty("BROWSER", "chrome");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("HEADLESS", "false"));
    }

    // Get explicit wait timeout in seconds
    public static int getExplicitWaitTimeout() {
        return Integer.parseInt(getProperty("EXPLICIT_WAIT_TIMEOUT", "10"));
    }

    // Get hard wait timeout in seconds
    public static int getHardWaitTimeout() {
        return Integer.parseInt(getProperty("HARD_WAIT_TIMEOUT", "2"));
    }

    // Get step time in seconds
    public static int getStepTime() {
        return Integer.parseInt(getProperty("STEP_TIME", "0"));
    }

    // Get page load timeout in seconds
    public static int getPageLoadTimeout() {
        return Integer.parseInt(getProperty("PAGE_LOAD_TIMEOUT", "60"));
    }

    // Get screenshot path
    public static String getScreenshotPath() {
        return getProperty("SCREENSHOT_PATH", "./exports/screenshots/");
    }

    // Get video recording path
    public static String getRecordVideoPath() {
        return getProperty("RECORD_VIDEO_PATH", "./exports/videos/");
    }

    public static boolean isRecordVideoEnabled() {
        return "yes".equalsIgnoreCase(getProperty("RECORD_VIDEO", "no"));
    }

    public static boolean isScreenshotStepAllEnabled() {
        return "yes".equalsIgnoreCase(getProperty("SCREENSHOT_STEP_ALL", "no"));
    }

    public static String getExtentReportPath() {
        return getProperty("EXTENT_REPORT_PATH", "exports/ExtentReport/ExtentReport.html");
    }

    public static String getExtentReportZipPath() {
        return getProperty("EXTENT_REPORT_ZIP_PATH", "exports/ExtentReport.zip");
    }

    public static String getAllureReportUrl() {
        String configuredUrl = getProperty("ALLURE_REPORT_URL", "");
        if (isNotBlank(configuredUrl)) {
            return configuredUrl.trim();
        }

        String buildUrl = System.getenv("BUILD_URL");
        if (isNotBlank(buildUrl)) {
            return appendPath(buildUrl, "allure");
        }

        return "";
    }

    public static boolean isAllureReportEnabled() {
        return getBooleanProperty("REPORT.ALLURE.ENABLED", true);
    }

    public static boolean isExtentReportEnabled() {
        return getBooleanProperty("REPORT.EXTENT.ENABLED", false);
    }

    public static boolean isEmailReportEnabled() {
        return getBooleanProperty("REPORT.EMAIL.ENABLED", getBooleanProperty("SEND_EMAIL_TO_USERS", false));
    }

    public static String getEmailSmtpHost() {
        return getProperty("EMAIL.SMTP.HOST", "smtp.gmail.com");
    }

    public static String getEmailSmtpPort() {
        return getProperty("EMAIL.SMTP.PORT", "587");
    }

    public static String getEmailFrom() {
        return getProperty("EMAIL.FROM", "");
    }

    public static String getEmailPassword() {
        return getProperty("EMAIL.PASSWORD", "");
    }

    public static String[] getEmailTo() {
        String to = getProperty("EMAIL.TO", "");
        if (!isNotBlank(to)) {
            return new String[0];
        }
        return java.util.Arrays.stream(to.split(","))
                .map(String::trim)
                .filter(ConfigManager::isNotBlank)
                .toArray(String[]::new);
    }

    public static String getReportTitle() {
        String title = getProperty("REPORT_TITLE", "Automation Test Execution Report - {suite}");
        String suiteName = getTestSuiteName();
        return title.replace("{suite}", suiteName).replace("${suite}", suiteName);
    }

    public static String getTestSuiteName() {
        String suiteName = getProperty("TEST_SUITE", DEFAULT_TEST_SUITE);
        return isNotBlank(suiteName) ? suiteName.trim() : DEFAULT_TEST_SUITE;
    }

    public static String getTestSuiteTag() {
        String configuredTag = getProperty("TEST_SUITE_TAG", "");
        if (isNotBlank(configuredTag)) {
            return normalizeCucumberTag(configuredTag);
        }

        String suiteKey = normalizeSuiteKey(getTestSuiteName());
        String mappedTag = getProperty("TEST_SUITE_TAG." + suiteKey, "");
        if (isNotBlank(mappedTag)) {
            return normalizeCucumberTag(mappedTag);
        }

        return switch (suiteKey) {
            case "SIT" -> "@sit";
            case "STAGING" -> "@staging";
            case "SANITY" -> "@sanity";
            case "UAT" -> "@uat";
            case "PRODUCTION" -> "@production";
            default -> "@regression";
        };
    }

    public static void configureCucumberTagsForRunner(String runnerTag) {
        String normalizedRunnerTag = normalizeCucumberTag(runnerTag);
        String tagExpression = normalizedRunnerTag + " and " + getTestSuiteTag();
        System.setProperty("cucumber.filter.tags", tagExpression);
        System.out.println("Cucumber tag filter applied: " + tagExpression);
    }

    public static String getAuthor() {
        return getProperty("AUTHOR", "Injas Mahendra Berutu");
    }

    public static String getLocale() {
        return getProperty("LOCALE", getProperty("LOCATE", "en"));
    }

    public static String getValidLoginEmail() {
        return getProperty("VALID_LOGIN_EMAIL");
    }

    public static String getValidLoginPassword() {
        return getProperty("VALID_LOGIN_PASSWORD");
    }

    public static String getProperty(String key) {
        // Priority: System Property > Config Property
        String systemValue = System.getProperty(key);
        if (systemValue != null) {
            return systemValue;
        }
        String envValue = System.getenv(key);
        if (envValue == null) {
            envValue = System.getenv(key.replace('.', '_'));
        }
        if (envValue != null) {
            return envValue;
        }
        return config.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    private static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value.trim()) : defaultValue;
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static String normalizeCucumberTag(String tag) {
        String normalizedTag = tag.trim();
        return normalizedTag.startsWith("@") ? normalizedTag : "@" + normalizedTag;
    }

    private static String normalizeSuiteKey(String suiteName) {
        String normalizedSuiteName = suiteName.trim().toUpperCase();
        if (normalizedSuiteName.endsWith(" SUITE")) {
            normalizedSuiteName = normalizedSuiteName.substring(0, normalizedSuiteName.length() - " SUITE".length());
        }
        return normalizedSuiteName.replaceAll("[^A-Z0-9]+", "_");
    }

    private static String appendPath(String baseUrl, String path) {
        if (baseUrl.endsWith("/")) {
            return baseUrl + path;
        }
        return baseUrl + "/" + path;
    }

    public static void reload() {
        loadConfiguration();
    }

    public static void printConfiguration() {
        System.out.println("========== Configuration Properties ==========");
        System.out.println("Environment: " + environment);
        config.forEach((key, value) -> System.out.println(key + " = " + value));
        System.out.println("=============================================");
    }
}
