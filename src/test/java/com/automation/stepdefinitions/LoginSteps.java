package com.automation.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import com.automation.pages.LoginPage;
import static org.junit.jupiter.api.Assertions.*;

/**
 * LoginSteps - Step definitions for Login feature
 */
public class LoginSteps {

    private LoginPage loginPage = new LoginPage();

    @Given("I navigate to the login page")
    public void iNavigateToTheLoginPage() {
        loginPage.navigateToLoginPage();
    }

    @When("I enter username {string}")
    public void iEnterUsername(String username) {
        loginPage.enterUsername(username);
    }

    @And("I enter password {string}")
    public void iEnterPassword(String password) {
        loginPage.enterPassword(password);
    }

    @And("I click the login button")
    public void iClickTheLoginButton() {
        loginPage.clickLoginButton();
    }

    @Then("I should be logged in successfully")
    public void iShouldBeLoggedInSuccessfully() {
        assertTrue(loginPage.isLoginSuccessful(), "Login was not successful!");
        System.out.println("Login verification PASSED");
    }

    @And("I should see the logout button")
    public void iShouldSeeTheLogoutButton() {
        assertTrue(loginPage.isLoggedIn(), "Logout button not visible!");
        System.out.println("Logout button is visible");
    }

    @Then("I should see an error message")
    public void iShouldSeeAnErrorMessage() {
        assertTrue(loginPage.isErrorMessageDisplayed(), "Error message not displayed!");
        System.out.println("Error message is displayed");
    }

    @And("the error message should contain {string}")
    public void theErrorMessageShouldContain(String expectedText) {
        String actualMessage = loginPage.getErrorMessageText();
        assertTrue(actualMessage.contains(expectedText), 
            "Error message does not contain expected text. Actual: " + actualMessage);
        System.out.println("Error message contains: " + expectedText);
    }

    @When("I click the logout button")
    public void iClickTheLogoutButton() {
        loginPage.clickLogout();
    }

    @Then("I should be logged out successfully")
    public void iShouldBeLoggedOutSuccessfully() {
        // After logout, we should be able to see the login form again
        assertTrue(!loginPage.isLoggedIn(), "User is still logged in!");
        System.out.println("User logged out successfully");
    }
}



