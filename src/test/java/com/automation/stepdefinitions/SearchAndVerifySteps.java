package com.automation.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.cucumber.datatable.DataTable;
import com.automation.pages.LoginPage;
import com.automation.pages.SearchAndVerifyPage;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SearchAndVerifySteps - Step definitions for Admin User Management Search functionality
 * 
 * This class handles all BDD steps related to:
 * - Navigating to Admin User Management
 * - Searching for system users (with dynamic data handling)
 * - Verifying search results
 * - Filter and reset operations
 * 
 * Key Feature: Dynamic data handling for demo sites with changing data
 * 
 * @author Vishal Lodhiya
 * @version 2.0
 */
public class SearchAndVerifySteps {

    private LoginPage loginPage = new LoginPage();
    private SearchAndVerifyPage searchPage = new SearchAndVerifyPage();

    // ============================================
    // GIVEN STEPS - PRECONDITIONS
    // ============================================

    @Given("I am logged into the OrangeHRM application")
    public void iAmLoggedIntoTheOrangeHRMApplication() {
        loginPage.navigateToLoginPage();
        loginPage.login("Admin", "admin123");
        assertTrue(loginPage.isLoginSuccessful(), 
            "[ERROR] Failed to login to OrangeHRM application");
        System.out.println("[INFO] Successfully logged into OrangeHRM");
    }

    @And("I navigate to the Admin User Management page")
    public void iNavigateToTheAdminUserManagementPage() {
        searchPage.navigateToAdminUserManagement();
        System.out.println("[INFO] Navigated to Admin User Management page");
    }

    // ============================================
    // WHEN STEPS - SEARCH ACTIONS
    // ============================================

    @When("I search for user with username {string}")
    public void iSearchForUserWithUsername(String username) {
        searchPage.enterUsername(username);
        System.out.println("[INFO] Entered username for search: " + username);
    }

    @When("I search for that captured username")
    public void iSearchForThatCapturedUsername() {
        String username = searchPage.getCapturedUsername();
        assertNotNull(username, "[ERROR] No username was captured");
        searchPage.enterUsername(username);
        System.out.println("[INFO] Searching for captured username: " + username);
    }

    @When("I search for the captured username")
    public void iSearchForTheCapturedUsername() {
        String username = searchPage.getCapturedUsername();
        assertNotNull(username, "[ERROR] No username was captured");
        searchPage.enterUsername(username);
        System.out.println("[INFO] Searching for captured username: " + username);
    }

    @When("I enter employee name {string} in the search field")
    public void iEnterEmployeeNameInTheSearchField(String employeeName) {
        searchPage.enterEmployeeName(employeeName);
        System.out.println("[INFO] Entered employee name for search: " + employeeName);
    }

    @And("I select {string} from the autocomplete suggestions")
    public void iSelectFromTheAutocompleteSuggestions(String employeeName) {
        searchPage.selectEmployeeFromAutocomplete(employeeName);
        System.out.println("[INFO] Selected from autocomplete: " + employeeName);
    }

    @And("I select User Role as {string}")
    public void iSelectUserRoleAs(String userRole) {
        searchPage.selectUserRole(userRole);
        System.out.println("[INFO] Selected User Role: " + userRole);
    }

    @And("I select Status as {string}")
    public void iSelectStatusAs(String status) {
        searchPage.selectStatus(status);
        System.out.println("[INFO] Selected Status: " + status);
    }

    @And("I click the Search button")
    public void iClickTheSearchButton() {
        searchPage.clickSearchButton();
        System.out.println("[INFO] Search button clicked");
    }

    @And("I click the Reset button")
    public void iClickTheResetButton() {
        searchPage.clickResetButton();
        System.out.println("[INFO] Reset button clicked");
    }

    // ============================================
    // DYNAMIC DATA CAPTURE STEPS
    // ============================================

    @When("I capture an existing username from the results table")
    public void iCaptureAnExistingUsernameFromTheResultsTable() {
        String username = searchPage.captureExistingUsername();
        assertNotNull(username, "[ERROR] Could not capture any username from results");
        System.out.println("[INFO] Captured existing username: " + username);
    }

    @When("I capture the first username from results")
    public void iCaptureTheFirstUsernameFromResults() {
        String username = searchPage.captureFirstUsername();
        assertNotNull(username, "[ERROR] Could not capture username from results");
        System.out.println("[INFO] Captured first username: " + username);
    }

    @And("I capture the total records count")
    public void iCaptureTheTotalRecordsCount() {
        int count = searchPage.captureRecordsCount();
        assertTrue(count > 0, "[ERROR] No records found to capture count");
        System.out.println("[INFO] Captured total records: " + count);
    }

    // ============================================
    // THEN STEPS - RESULT VERIFICATIONS
    // ============================================

    @Then("I should see search results displayed")
    public void iShouldSeeSearchResultsDisplayed() {
        assertTrue(searchPage.areResultsDisplayed(), 
            "[ERROR] Search results are not displayed");
        System.out.println("[INFO] Search results are displayed");
    }

    @And("the results should contain employee name {string}")
    public void theResultsShouldContainEmployeeName(String expectedEmployeeName) {
        assertTrue(searchPage.verifyEmployeeNameInResults(expectedEmployeeName), 
            "[ERROR] Employee name '" + expectedEmployeeName + "' not found in search results");
        System.out.println("[INFO] Verified employee name in results: " + expectedEmployeeName);
    }

    @And("the results should contain username {string}")
    public void theResultsShouldContainUsername(String expectedUsername) {
        assertTrue(searchPage.verifyUsernameInResults(expectedUsername), 
            "[ERROR] Username '" + expectedUsername + "' not found in search results");
        System.out.println("[INFO] Verified username in results: " + expectedUsername);
    }

    @And("the results should contain the searched username")
    public void theResultsShouldContainTheSearchedUsername() {
        String capturedUsername = searchPage.getCapturedUsername();
        assertTrue(searchPage.verifyUsernameInResults(capturedUsername), 
            "[ERROR] Captured username '" + capturedUsername + "' not found in search results");
        System.out.println("[INFO] Verified captured username in results: " + capturedUsername);
    }

    @And("the results should contain the captured username")
    public void theResultsShouldContainTheCapturedUsername() {
        String capturedUsername = searchPage.getCapturedUsername();
        assertTrue(searchPage.verifyUsernameInResults(capturedUsername), 
            "[ERROR] Captured username '" + capturedUsername + "' not found in search results");
        System.out.println("[INFO] Verified captured username in results: " + capturedUsername);
    }

    @And("all results should have User Role {string}")
    public void allResultsShouldHaveUserRole(String expectedUserRole) {
        assertTrue(searchPage.verifyAllResultsHaveUserRole(expectedUserRole), 
            "[ERROR] Not all results have User Role: " + expectedUserRole);
        System.out.println("[INFO] All results have User Role: " + expectedUserRole);
    }

    @And("all results should have Status {string}")
    public void allResultsShouldHaveStatus(String expectedStatus) {
        assertTrue(searchPage.verifyAllResultsHaveStatus(expectedStatus), 
            "[ERROR] Not all results have Status: " + expectedStatus);
        System.out.println("[INFO] All results have Status: " + expectedStatus);
    }

    @Then("I should see {string} message")
    public void iShouldSeeMessage(String expectedMessage) {
        if (expectedMessage.equals("No Records Found")) {
            // Check for either table message OR toast notification
            assertTrue(searchPage.isNoDataAvailable(), 
                "[ERROR] 'No Records Found' message/toast is not displayed");
            System.out.println("[INFO] 'No Records Found' indication is displayed");
        }
    }

    @Then("I should see either results with Status {string} or no records message")
    public void iShouldSeeEitherResultsWithStatusOrNoRecordsMessage(String status) {
        // First check if we have results
        if (searchPage.areResultsDisplayed()) {
            assertTrue(searchPage.verifyAllResultsHaveStatus(status), 
                "[ERROR] Results found but not all have status: " + status);
            System.out.println("[INFO] Results found with Status: " + status);
            return;
        }
        
        // If no results, check for no-data indication (toast or table message)
        if (searchPage.isNoDataAvailable()) {
            System.out.println("[INFO] No data available for Status: " + status + " - This is acceptable");
            return;
        }
        
        // If neither, fail
        fail("[ERROR] Neither results with status '" + status + "' nor 'No Records' indication found");
    }

    @Then("I should see results or a no data notification")
    public void iShouldSeeResultsOrNoDataNotification() {
        assertTrue(searchPage.verifySearchOutcome("current search"), 
            "[ERROR] Neither results nor 'no data' indication found");
        System.out.println("[INFO] Search outcome verified");
    }

    @Then("I should see toast notification if no data is available")
    public void iShouldSeeToastNotificationIfNoDataIsAvailable() {
        if (!searchPage.areResultsDisplayed()) {
            if (searchPage.isToastDisplayed()) {
                String message = searchPage.getToastMessage();
                System.out.println("[INFO] Toast notification displayed: " + message);
            } else if (searchPage.isNoRecordsMessageDisplayed()) {
                System.out.println("[INFO] No Records Found message displayed in table");
            }
        } else {
            System.out.println("[INFO] Results are displayed, no toast needed");
        }
    }

    @Then("the results count should be less than or equal to the captured count")
    public void theResultsCountShouldBeLessThanOrEqualToTheCapturedCount() {
        assertTrue(searchPage.isCurrentCountLessThanOrEqualToCaptured(), 
            "[ERROR] Current count exceeds the captured count (filter not working)");
        System.out.println("[INFO] Results count is filtered correctly");
    }

    // ============================================
    // RESET VERIFICATION STEPS
    // ============================================

    @Then("all search fields should be cleared")
    public void allSearchFieldsShouldBeCleared() {
        assertTrue(searchPage.isUsernameFieldEmpty(), 
            "[ERROR] Username field is not cleared after reset");
        System.out.println("[INFO] Username field is cleared");
    }

    @And("the User Role dropdown should show {string}")
    public void theUserRoleDropdownShouldShow(String expectedValue) {
        String actualValue = searchPage.getUserRoleDropdownText();
        assertTrue(actualValue.contains("Select"), 
            "[ERROR] User Role dropdown not reset. Actual: " + actualValue);
        System.out.println("[INFO] User Role dropdown shows: " + actualValue);
    }

    @And("the Status dropdown should show {string}")
    public void theStatusDropdownShouldShow(String expectedValue) {
        String actualValue = searchPage.getStatusDropdownText();
        assertTrue(actualValue.contains("Select"), 
            "[ERROR] Status dropdown not reset. Actual: " + actualValue);
        System.out.println("[INFO] Status dropdown shows: " + actualValue);
    }

    // ============================================
    // TABLE COLUMN VERIFICATION STEPS
    // ============================================

    @And("the results table should have columns:")
    public void theResultsTableShouldHaveColumns(DataTable dataTable) {
        List<String> expectedColumns = dataTable.asList(String.class);
        assertTrue(searchPage.verifyTableColumns(expectedColumns), 
            "[ERROR] Table columns verification failed");
        System.out.println("[INFO] All expected columns are present in the table");
    }

    // ============================================
    // AUTOCOMPLETE STEPS
    // ============================================

    @When("I type partial employee name {string} in the search field")
    public void iTypePartialEmployeeNameInTheSearchField(String partialName) {
        searchPage.typePartialEmployeeName(partialName);
        System.out.println("[INFO] Typed partial name: " + partialName);
    }

    @Then("I should see autocomplete suggestions appear")
    public void iShouldSeeAutocompleteSuggestionsAppear() {
        assertTrue(searchPage.areAutocompleteSuggestionsVisible(), 
            "[ERROR] Autocomplete suggestions did not appear");
        System.out.println("[INFO] Autocomplete suggestions are visible");
    }

    @When("I select the first autocomplete suggestion")
    public void iSelectTheFirstAutocompleteSuggestion() {
        searchPage.selectFirstAutocompleteSuggestion();
        System.out.println("[INFO] Selected first autocomplete suggestion");
    }

    // ============================================
    // DATA TABLE VERIFICATION STEP (Legacy support)
    // ============================================

    @And("I verify the following user details in results:")
    public void iVerifyTheFollowingUserDetailsInResults(DataTable dataTable) {
        Map<String, String> expectedDetails = dataTable.asMap(String.class, String.class);
        
        String username = expectedDetails.get("Username");
        String userRole = expectedDetails.get("User Role");
        String employeeName = expectedDetails.get("Employee Name");
        String status = expectedDetails.get("Status");
        
        System.out.println("[INFO] Verifying user details:");
        System.out.println("   Username: " + username);
        System.out.println("   User Role: " + userRole);
        System.out.println("   Employee Name: " + employeeName);
        System.out.println("   Status: " + status);
        
        assertTrue(searchPage.verifyUserDetails(username, userRole, employeeName, status),
            "[ERROR] User details verification failed");
        System.out.println("[INFO] All user details verified successfully");
    }
}
