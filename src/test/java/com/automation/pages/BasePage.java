package com.automation.pages;

import org.openqa.selenium.WebDriver;
import com.automation.drivers.DriverFactory;

/**
 * BasePage - Base class for all page objects
 * Contains common elements and methods shared across pages
 */
public class BasePage {

    protected WebDriver driver;

    public BasePage() {
        this.driver = DriverFactory.getDriver();
    }

    /**
     * Navigate to a URL
     */
    public void navigateTo(String url) {
        driver.get(url);
        System.out.println("Navigated to: " + url);
    }

    /**
     * Get current page title
     */
    public String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Get current URL
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Refresh the page
     */
    public void refreshPage() {
        driver.navigate().refresh();
        System.out.println("Page refreshed");
    }

    /**
     * Navigate back
     */
    public void navigateBack() {
        driver.navigate().back();
        System.out.println("Navigated back");
    }
}



