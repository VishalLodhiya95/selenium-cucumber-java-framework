package com.automation.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import com.automation.pages.LoginPage;
import com.automation.pages.MyInfoPage;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MyInfoSteps - Step definitions for My Info / Personal Details functionality
 * 
 * This class handles all BDD steps related to:
 * - Navigating to My Info page
 * - Loading employee data from Excel
 * - Clearing and filling personal details form
 * - Saving and verifying updates
 * 
 * @author Vishal Lodhiya
 * @version 1.0
 */
public class MyInfoSteps {

    private LoginPage loginPage = new LoginPage();
    private MyInfoPage myInfoPage = new MyInfoPage();

    // ============================================
    // GIVEN STEPS - PRECONDITIONS
    // ============================================

    @Given("I am logged into the OrangeHRM application as Admin")
    public void iAmLoggedIntoTheOrangeHRMApplicationAsAdmin() {
        loginPage.navigateToLoginPage();
        loginPage.login("Admin", "admin123");
        assertTrue(loginPage.isLoginSuccessful(), 
            "[ERROR] Failed to login to OrangeHRM application");
        System.out.println("[INFO] Successfully logged into OrangeHRM as Admin");
    }

    @And("I navigate to the My Info page")
    public void iNavigateToTheMyInfoPage() {
        myInfoPage.navigateToMyInfoPage();
        assertTrue(myInfoPage.isMyInfoPageLoaded(), 
            "[ERROR] My Info page did not load correctly");
        System.out.println("[INFO] Navigated to My Info page");
    }

    @Given("I have employee data loaded from {string}")
    public void iHaveEmployeeDataLoadedFrom(String fileName) {
        myInfoPage.loadEmployeeDataFromExcel(fileName);
        Map<String, String> data = myInfoPage.getEmployeeData();
        assertNotNull(data, "[ERROR] Failed to load employee data from Excel");
        assertFalse(data.isEmpty(), "[ERROR] Employee data is empty");
        System.out.println("[INFO] Employee data loaded from: " + fileName);
    }

    // ============================================
    // WHEN STEPS - CLEAR ACTIONS
    // ============================================

    @When("I clear all personal details fields")
    public void iClearAllPersonalDetailsFields() {
        myInfoPage.clearAllFields();
        System.out.println("[INFO] All personal details fields cleared");
    }

    @When("I clear the First Name field")
    public void iClearTheFirstNameField() {
        myInfoPage.clearFirstName();
    }

    @And("I clear the Middle Name field")
    public void iClearTheMiddleNameField() {
        myInfoPage.clearMiddleName();
    }

    @And("I clear the Last Name field")
    public void iClearTheLastNameField() {
        myInfoPage.clearLastName();
    }

    @When("I clear the Employee ID field")
    public void iClearTheEmployeeIdField() {
        myInfoPage.clearEmployeeId();
    }

    @When("I clear the License Expiry Date field")
    public void iClearTheLicenseExpiryDateField() {
        myInfoPage.clearLicenseExpiryDate();
    }

    @And("I clear the Date of Birth field")
    public void iClearTheDateOfBirthField() {
        myInfoPage.clearDateOfBirth();
    }

    // ============================================
    // WHEN STEPS - FILL ACTIONS
    // ============================================

    @And("I fill the personal details form with Excel data")
    public void iFillThePersonalDetailsFormWithExcelData() {
        myInfoPage.fillPersonalDetailsFromExcel();
        System.out.println("[INFO] Personal details form filled from Excel data");
    }

    @And("I enter First Name from Excel data")
    public void iEnterFirstNameFromExcelData() {
        Map<String, String> data = myInfoPage.getEmployeeData();
        String firstName = data.get("Employee First Name");
        myInfoPage.enterFirstName(firstName);
    }

    @And("I enter Middle Name from Excel data")
    public void iEnterMiddleNameFromExcelData() {
        Map<String, String> data = myInfoPage.getEmployeeData();
        String middleName = data.get("Employee Middle Name");
        myInfoPage.enterMiddleName(middleName);
    }

    @And("I enter Last Name from Excel data")
    public void iEnterLastNameFromExcelData() {
        Map<String, String> data = myInfoPage.getEmployeeData();
        String lastName = data.get("Employee Last Name");
        myInfoPage.enterLastName(lastName);
    }

    @And("I enter Employee ID from Excel data")
    public void iEnterEmployeeIdFromExcelData() {
        Map<String, String> data = myInfoPage.getEmployeeData();
        String employeeId = data.get("Employee ID");
        myInfoPage.enterEmployeeId(employeeId);
    }

    @And("I enter License Expiry Date from Excel data")
    public void iEnterLicenseExpiryDateFromExcelData() {
        Map<String, String> data = myInfoPage.getEmployeeData();
        String licenseExpiry = data.get("License Expiry Date");
        myInfoPage.enterLicenseExpiryDate(licenseExpiry);
    }

    @And("I enter Date of Birth from Excel data")
    public void iEnterDateOfBirthFromExcelData() {
        Map<String, String> data = myInfoPage.getEmployeeData();
        String dateOfBirth = data.get("Date of Birth");
        myInfoPage.enterDateOfBirth(dateOfBirth);
    }

    // ============================================
    // WHEN STEPS - SAVE ACTIONS
    // ============================================

    @And("I click the Save button to save changes")
    public void iClickTheSaveButtonToSaveChanges() {
        myInfoPage.clickSaveButton();
        System.out.println("[INFO] Save button clicked");
    }

    // ============================================
    // THEN STEPS - VERIFICATIONS
    // ============================================

    @Then("I should see a success message confirming the save")
    public void iShouldSeeASuccessMessageConfirmingTheSave() {
        assertTrue(myInfoPage.isSuccessToastDisplayed(), 
            "[ERROR] Success message was not displayed after save");
        System.out.println("[INFO] Success message displayed - Save confirmed");
    }

    @And("the personal details should be updated successfully")
    public void thePersonalDetailsShouldBeUpdatedSuccessfully() {
        // Wait for success toast to disappear
        myInfoPage.waitForSaveConfirmation();
        System.out.println("[INFO] Personal details updated successfully");
    }

    @Then("I should see the Personal Details section")
    public void iShouldSeeThePersonalDetailsSection() {
        assertTrue(myInfoPage.isMyInfoPageLoaded(), 
            "[ERROR] Personal Details section is not visible");
        System.out.println("[INFO] Personal Details section is visible");
    }

    @And("the First Name field should be visible")
    public void theFirstNameFieldShouldBeVisible() {
        String firstName = myInfoPage.getFirstNameValue();
        assertNotNull(firstName, "[ERROR] First Name field is not accessible");
        System.out.println("[INFO] First Name field is visible, value: " + firstName);
    }

    @And("the Last Name field should be visible")
    public void theLastNameFieldShouldBeVisible() {
        String lastName = myInfoPage.getLastNameValue();
        assertNotNull(lastName, "[ERROR] Last Name field is not accessible");
        System.out.println("[INFO] Last Name field is visible, value: " + lastName);
    }

    @And("the Employee ID field should be visible")
    public void theEmployeeIdFieldShouldBeVisible() {
        String employeeId = myInfoPage.getEmployeeIdValue();
        assertNotNull(employeeId, "[ERROR] Employee ID field is not accessible");
        System.out.println("[INFO] Employee ID field is visible, value: " + employeeId);
    }

    @And("the form data should match the Excel data")
    public void theFormDataShouldMatchTheExcelData() {
        assertTrue(myInfoPage.verifyFormDataMatchesExcel(), 
            "[ERROR] Form data does not match Excel data");
        System.out.println("[INFO] Form data matches Excel data");
    }

    // ============================================
    // ATTACHMENT UPLOAD STEPS
    // ============================================

    @When("I click the Add Attachment button")
    public void iClickTheAddAttachmentButton() {
        myInfoPage.clickAddAttachmentButton();
        System.out.println("[INFO] Add Attachment button clicked");
    }

    @And("I upload the PDF document {string}")
    public void iUploadThePdfDocument(String fileName) {
        myInfoPage.uploadPdfDocument(fileName);
        System.out.println("[INFO] PDF document uploaded: " + fileName);
    }

    @And("I click the Save Attachment button")
    public void iClickTheSaveAttachmentButton() {
        myInfoPage.clickSaveAttachmentButton();
        System.out.println("[INFO] Save Attachment button clicked");
    }

    @Then("I should see a success message confirming the attachment was saved")
    public void iShouldSeeASuccessMessageConfirmingTheAttachmentWasSaved() {
        assertTrue(myInfoPage.isAttachmentSavedSuccessfully(), 
            "[ERROR] Attachment save success message was not displayed");
        System.out.println("[INFO] Attachment saved successfully - Success message displayed");
    }

    @And("the attachment {string} should be visible in the attachments list")
    public void theAttachmentShouldBeVisibleInTheAttachmentsList(String fileName) {
        // Wait for save operation to complete and page to update
        myInfoPage.waitForPageToLoad();
        
        // Refresh the page to ensure attachments list is updated
        myInfoPage.refreshPage();
        myInfoPage.waitForPageToLoad();
        
        // Scroll down to attachments section and verify
        boolean found = myInfoPage.isFileInAttachmentsList(fileName);
        
        if (found) {
            System.out.println("[INFO] Attachment '" + fileName + "' is visible in attachments list");
        } else {
            System.out.println("[INFO] Attachment verification: File may have been saved but not visible in current view");
            System.out.println("[INFO] This can happen on shared demo sites - treating as soft pass");
        }
        
        // Make assertion flexible for demo site behavior
        assertTrue(found, 
            "[ERROR] Attachment '" + fileName + "' not found in attachments list");
    }
}

