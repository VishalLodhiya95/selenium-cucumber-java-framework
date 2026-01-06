package com.automation.drivers;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import com.automation.utilities.ConfigReader;

import java.time.Duration;

/**
 * DriverFactory - Manages WebDriver instances using ThreadLocal for parallel execution
 */
public class DriverFactory {

    // ThreadLocal ensures each thread gets its own WebDriver instance
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static String browserName = "Unknown";

    /**
     * Initialize and return WebDriver based on configuration
     */
    public static WebDriver getDriver() {
        if (driver.get() == null) {
            initializeDriver();
        }
        return driver.get();
    }

    /**
     * Initialize the WebDriver based on browser configuration
     */
    private static void initializeDriver() {
        String browser = ConfigReader.getProperty("browser").toLowerCase();
        boolean headless = Boolean.parseBoolean(ConfigReader.getProperty("headlessMode"));

        System.out.println("Initializing browser: " + browser.toUpperCase());

        switch (browser) {
            case "chrome":
            case "headless":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless || browser.equals("headless")) {
                    chromeOptions.addArguments("--headless=new");
                    chromeOptions.addArguments("--window-size=1920,1080");
                }
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                driver.set(new ChromeDriver(chromeOptions));
                browserName = "Chrome";
                break;

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) {
                    firefoxOptions.addArguments("--headless");
                }
                driver.set(new FirefoxDriver(firefoxOptions));
                browserName = "Firefox";
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (headless) {
                    edgeOptions.addArguments("--headless=new");
                }
                driver.set(new EdgeDriver(edgeOptions));
                browserName = "Edge";
                break;

            default:
                throw new RuntimeException("Unsupported browser: " + browser);
        }

        // Configure timeouts
        int pageLoadTimeout = Integer.parseInt(ConfigReader.getProperty("pageLoadTimeout"));
        int implicitWait = Integer.parseInt(ConfigReader.getProperty("implicitWait"));

        driver.get().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
        driver.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        driver.get().manage().window().maximize();

        System.out.println("Browser launched: " + browserName + " | Thread ID: " + Thread.currentThread().getId());
    }

    /**
     * Get the browser name
     */
    public static String getBrowserName() {
        return browserName;
    }

    /**
     * Quit the WebDriver and clean up
     */
    public static void quitDriver() {
        if (driver.get() != null) {
            try {
                driver.get().quit();
                System.out.println("Browser closed successfully");
            } catch (Exception e) {
                System.out.println("Error closing browser: " + e.getMessage());
            } finally {
                driver.remove();
            }
        }
    }
}



