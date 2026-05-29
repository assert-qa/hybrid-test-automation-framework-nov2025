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

    public static String getAuthor() {
        return getProperty("AUTHOR", "Test Automation Team");
    }

    public static String getLocale() {
        return getProperty("LOCATE", "en");
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
        return config.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
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
