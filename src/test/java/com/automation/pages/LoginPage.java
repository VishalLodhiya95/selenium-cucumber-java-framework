package com.automation.pages;

import org.openqa.selenium.By;
import com.automation.helpers.ElementHelper;
import com.automation.utilities.ConfigReader;

/**
 * LoginPage - Page Object for the OrangeHRM login page
 * Uses https://opensource-demo.orangehrmlive.com as the test site
 */
public class LoginPage extends BasePage {

    // Locators for OrangeHRM
    private By usernameField = By.name("username");
    private By passwordField = By.name("password");
    private By loginButton = By.cssSelector("button[type='submit']");
    private By dashboardHeader = By.cssSelector("h6.oxd-topbar-header-breadcrumb-module");
    private By errorMessage = By.cssSelector("p.oxd-alert-content-text");
    private By userDropdown = By.cssSelector("span.oxd-userdropdown-tab");
    private By logoutLink = By.xpath("//a[text()='Logout']");

    /**
     * Navigate to login page
     */
    public void navigateToLoginPage() {
        String baseUrl = ConfigReader.getBaseUrl();
        navigateTo(baseUrl + "/web/index.php/auth/login");
        // Wait for page to load
        ElementHelper.waitForElementToBeVisible(driver, usernameField, 15);
        System.out.println("Navigated to Login Page");
    }

    /**
     * Enter username
     */
    public void enterUsername(String username) {
        ElementHelper.safeSendKeys(driver, usernameField, username);
        System.out.println("Entered username: " + username);
    }

    /**
     * Enter password
     */
    public void enterPassword(String password) {
        ElementHelper.safeSendKeys(driver, passwordField, password);
        System.out.println("Entered password: ****");
    }

    /**
     * Click login button
     */
    public void clickLoginButton() {
        ElementHelper.safeClickElement(driver, loginButton);
        System.out.println("Clicked Login button");
        // Wait for page to respond
        ElementHelper.sleep(2000);
    }

    /**
     * Perform complete login
     */
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    /**
     * Check if login was successful (Dashboard is visible)
     */
    public boolean isLoginSuccessful() {
        return ElementHelper.isElementDisplayed(driver, dashboardHeader, 10);
    }

    /**
     * Check if error message is displayed
     */
    public boolean isErrorMessageDisplayed() {
        return ElementHelper.isElementDisplayed(driver, errorMessage, 5);
    }

    /**
     * Get error message text
     */
    public String getErrorMessageText() {
        return ElementHelper.getTextFromElement(driver, errorMessage, 5);
    }

    /**
     * Get success message text (Dashboard header text)
     */
    public String getSuccessMessageText() {
        return ElementHelper.getTextFromElement(driver, dashboardHeader, 5);
    }

    /**
     * Click logout
     */
    public void clickLogout() {
        // First click on user dropdown
        ElementHelper.safeClickElement(driver, userDropdown);
        ElementHelper.sleep(500);
        // Then click Logout
        ElementHelper.safeClickElement(driver, logoutLink);
        System.out.println("Clicked Logout");
    }

    /**
     * Verify user is logged in (user dropdown visible)
     */
    public boolean isLoggedIn() {
        return ElementHelper.isElementDisplayed(driver, userDropdown, 5);
    }
}



