package com.automation.pages;

import org.openqa.selenium.By;
import com.automation.helpers.ElementHelper;
import com.automation.utilities.ConfigReader;
import com.automation.utilities.ExcelReader;

import java.util.Map;

/**
 * MyInfoPage - Page Object for the My Info / Personal Details page
 * URL: /web/index.php/pim/viewMyDetails
 * 
 * This page handles:
 * - Navigating to My Info page
 * - Reading employee data from Excel
 * - Clearing and filling personal details form
 * - Saving the updated information
 * 
 * @author Vishal Lodhiya
 * @version 1.0
 */
public class MyInfoPage extends BasePage {

    // ============================================
    // NAVIGATION LOCATORS
    // ============================================
    
    /** My Info link in the left sidebar */
    private By myInfoLink = By.xpath("//a[@href='/web/index.php/pim/viewMyDetails']");
    
    /** Personal Details header to confirm page load */
    private By personalDetailsHeader = By.xpath("//h6[text()='Personal Details']");

    // ============================================
    // FORM FIELD LOCATORS
    // ============================================
    
    /** Employee First Name input */
    private By firstNameInput = By.name("firstName");
    
    /** Employee Middle Name input */
    private By middleNameInput = By.name("middleName");
    
    /** Employee Last Name input */
    private By lastNameInput = By.name("lastName");
    
    /** Employee ID input - using label-based XPath */
    private By employeeIdInput = By.xpath(
        "//label[text()='Employee Id']/ancestor::div[contains(@class,'oxd-input-group')]//input"
    );
    
    /** License Expiry Date input - using label-based XPath */
    private By licenseExpiryDateInput = By.xpath(
        "//label[text()='License Expiry Date']/ancestor::div[contains(@class,'oxd-input-group')]//input"
    );
    
    /** Date of Birth input - using label-based XPath */
    private By dateOfBirthInput = By.xpath(
        "//label[text()='Date of Birth']/ancestor::div[contains(@class,'oxd-input-group')]//input"
    );

    // ============================================
    // BUTTON LOCATORS
    // ============================================
    
    /** Save button for Personal Details section (has data-v-6653c066 attribute) */
    private By saveButton = By.xpath("(//button[@type='submit'])[1]");
    
    /** Add Attachment button */
    private By addAttachmentButton = By.xpath("//button[@class='oxd-button oxd-button--medium oxd-button--text']");
    
    /** 
     * Save Attachment button - specifically in the attachment section
     * Located within the attachment form container, NOT the personal details section
     * Uses the form container that appears after clicking "Add" button
     */
    private By saveAttachmentButton = By.xpath(
        "//div[contains(@class,'orangehrm-attachment')]//button[@type='submit'] | " +
        "//form[.//input[@type='file']]//button[@type='submit'] | " +
        "//div[contains(@class,'oxd-file-input')]//ancestor::form//button[@type='submit']"
    );

    // ============================================
    // FILE UPLOAD LOCATORS
    // ============================================
    
    /** File input div (visible element showing "No file selected") */
    private By fileInputDiv = By.xpath("//div[contains(@class,'oxd-file-input-div')]");
    
    /** Actual file input element (hidden, used for sendKeys) */
    private By fileInput = By.cssSelector("input[type='file']");
    
    /** File input container */
    private By fileInputContainer = By.cssSelector("div.oxd-file-input");

    // ============================================
    // SUCCESS/ERROR MESSAGE LOCATORS
    // ============================================
    
    /** Success toast message */
    private By successToast = By.cssSelector("div.oxd-toast--success");
    
    /** Toast message text */
    private By toastMessage = By.cssSelector("div.oxd-toast-content p.oxd-text");

    // ============================================
    // LOADER LOCATOR
    // ============================================
    
    /** Loading spinner */
    private By loadingSpinner = By.cssSelector("div.oxd-loading-spinner");

    // ============================================
    // STORED DATA
    // ============================================
    
    /** Employee data loaded from Excel */
    private Map<String, String> employeeData;

    // ============================================
    // NAVIGATION METHODS
    // ============================================

    /**
     * Navigate to My Info page by clicking the sidebar link
     */
    public void navigateToMyInfoPage() {
        ElementHelper.waitForElementToBeVisible(driver, myInfoLink, 10);
        ElementHelper.safeClickElement(driver, myInfoLink);
        waitForPageToLoad();
        System.out.println("[INFO] Navigated to My Info page");
    }

    /**
     * Navigate to My Info page directly via URL
     */
    public void navigateToMyInfoPageDirectly() {
        String baseUrl = ConfigReader.getBaseUrl();
        navigateTo(baseUrl + "/web/index.php/pim/viewMyDetails");
        waitForPageToLoad();
        System.out.println("[INFO] Navigated to My Info page (direct URL)");
    }

    /**
     * Verify My Info page is loaded
     * @return true if Personal Details header is visible
     */
    public boolean isMyInfoPageLoaded() {
        boolean loaded = ElementHelper.isElementDisplayed(driver, personalDetailsHeader, 10);
        System.out.println(loaded ? "[INFO] My Info page loaded" : "[ERROR] My Info page not loaded");
        return loaded;
    }

    /**
     * Wait for page to load completely
     */
    public void waitForPageToLoad() {
        ElementHelper.waitForLoaderToDisappear(driver, loadingSpinner, 15);
        ElementHelper.sleep(1000); // Additional buffer for form to render
    }

    // ============================================
    // EXCEL DATA METHODS
    // ============================================

    /**
     * Load employee data from the default Excel file
     */
    public void loadEmployeeDataFromExcel() {
        employeeData = ExcelReader.getEmployeeData();
        System.out.println("[INFO] Loaded employee data from Excel");
    }

    /**
     * Load employee data from a specific Excel file
     * @param fileName Excel file name
     */
    public void loadEmployeeDataFromExcel(String fileName) {
        employeeData = ExcelReader.getEmployeeData(fileName, null);
        System.out.println("[INFO] Loaded employee data from: " + fileName);
    }

    /**
     * Get loaded employee data
     * @return Map of employee data
     */
    public Map<String, String> getEmployeeData() {
        return employeeData;
    }

    // ============================================
    // FORM FIELD METHODS - CLEAR
    // ============================================

    /**
     * Clear the First Name field
     */
    public void clearFirstName() {
        ElementHelper.waitForElementToBeVisible(driver, firstNameInput, 10);
        ElementHelper.clearFieldWithKeyboard(driver, firstNameInput, 10);
        System.out.println("[INFO] Cleared First Name field");
    }

    /**
     * Clear the Middle Name field
     */
    public void clearMiddleName() {
        ElementHelper.clearFieldWithKeyboard(driver, middleNameInput, 10);
        System.out.println("[INFO] Cleared Middle Name field");
    }

    /**
     * Clear the Last Name field
     */
    public void clearLastName() {
        ElementHelper.clearFieldWithKeyboard(driver, lastNameInput, 10);
        System.out.println("[INFO] Cleared Last Name field");
    }

    /**
     * Clear the Employee ID field
     */
    public void clearEmployeeId() {
        ElementHelper.clearFieldWithKeyboard(driver, employeeIdInput, 10);
        System.out.println("[INFO] Cleared Employee ID field");
    }

    /**
     * Clear the License Expiry Date field
     */
    public void clearLicenseExpiryDate() {
        ElementHelper.clearFieldWithKeyboard(driver, licenseExpiryDateInput, 10);
        System.out.println("[INFO] Cleared License Expiry Date field");
    }

    /**
     * Clear the Date of Birth field
     */
    public void clearDateOfBirth() {
        ElementHelper.clearFieldWithKeyboard(driver, dateOfBirthInput, 10);
        System.out.println("[INFO] Cleared Date of Birth field");
    }

    /**
     * Clear all form fields
     */
    public void clearAllFields() {
        System.out.println("[INFO] Clearing all personal details fields...");
        clearFirstName();
        clearMiddleName();
        clearLastName();
        clearEmployeeId();
        clearLicenseExpiryDate();
        clearDateOfBirth();
        System.out.println("[INFO] All fields cleared");
    }

    // ============================================
    // FORM FIELD METHODS - ENTER DATA
    // ============================================

    /**
     * Enter First Name
     * @param firstName Value to enter
     */
    public void enterFirstName(String firstName) {
        if (firstName != null && !firstName.isEmpty()) {
            ElementHelper.appendText(driver, firstNameInput, firstName, 10);
            System.out.println("[INFO] Entered First Name: " + firstName);
        }
    }

    /**
     * Enter Middle Name
     * @param middleName Value to enter
     */
    public void enterMiddleName(String middleName) {
        if (middleName != null && !middleName.isEmpty()) {
            ElementHelper.appendText(driver, middleNameInput, middleName, 10);
            System.out.println("[INFO] Entered Middle Name: " + middleName);
        }
    }

    /**
     * Enter Last Name
     * @param lastName Value to enter
     */
    public void enterLastName(String lastName) {
        if (lastName != null && !lastName.isEmpty()) {
            ElementHelper.appendText(driver, lastNameInput, lastName, 10);
            System.out.println("[INFO] Entered Last Name: " + lastName);
        }
    }

    /**
     * Enter Employee ID
     * @param employeeId Value to enter
     */
    public void enterEmployeeId(String employeeId) {
        if (employeeId != null && !employeeId.isEmpty()) {
            ElementHelper.appendText(driver, employeeIdInput, employeeId, 10);
            System.out.println("[INFO] Entered Employee ID: " + employeeId);
        }
    }

    /**
     * Enter License Expiry Date
     * OrangeHRM expects date in yyyy-dd-mm format
     * @param licenseExpiryDate Value to enter
     */
    public void enterLicenseExpiryDate(String licenseExpiryDate) {
        if (licenseExpiryDate != null && !licenseExpiryDate.isEmpty()) {
            String formattedDate = convertToOrangeHrmDateFormat(licenseExpiryDate);
            ElementHelper.appendText(driver, licenseExpiryDateInput, formattedDate, 10);
            System.out.println("[INFO] Entered License Expiry Date: " + formattedDate);
        }
    }

    /**
     * Enter Date of Birth
     * OrangeHRM expects date in yyyy-dd-mm format
     * @param dateOfBirth Value to enter
     */
    public void enterDateOfBirth(String dateOfBirth) {
        if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
            String formattedDate = convertToOrangeHrmDateFormat(dateOfBirth);
            ElementHelper.appendText(driver, dateOfBirthInput, formattedDate, 10);
            System.out.println("[INFO] Entered Date of Birth: " + formattedDate);
        }
    }

    /**
     * Convert date from various formats to OrangeHRM expected format (yyyy-dd-mm)
     * Handles: dd-mm-yyyy, dd/mm/yyyy, mm-dd-yyyy, yyyy-mm-dd, etc.
     * @param dateStr The input date string
     * @return Formatted date string in yyyy-dd-mm format
     */
    private String convertToOrangeHrmDateFormat(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return dateStr;
        }
        
        try {
            // Remove any extra whitespace
            dateStr = dateStr.trim();
            
            // Try to parse different date formats
            String[] formats = {
                "dd-MM-yyyy", "dd/MM/yyyy", "d-M-yyyy", "d/M/yyyy",
                "MM-dd-yyyy", "MM/dd/yyyy", "M-d-yyyy", "M/d/yyyy",
                "yyyy-MM-dd", "yyyy/MM/dd", "yyyy-dd-MM", "yyyy/dd/MM"
            };
            
            java.time.LocalDate parsedDate = null;
            
            for (String format : formats) {
                try {
                    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern(format);
                    parsedDate = java.time.LocalDate.parse(dateStr, formatter);
                    break;
                } catch (java.time.format.DateTimeParseException e) {
                    // Try next format
                }
            }
            
            if (parsedDate != null) {
                // Convert to OrangeHRM expected format: yyyy-dd-mm
                java.time.format.DateTimeFormatter outputFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-dd-MM");
                return parsedDate.format(outputFormatter);
            }
            
            // If no format matched, return original
            System.out.println("[WARN] Could not parse date: " + dateStr + ", using original value");
            return dateStr;
            
        } catch (Exception e) {
            System.out.println("[WARN] Error converting date: " + e.getMessage());
            return dateStr;
        }
    }

    // ============================================
    // FILL FORM FROM EXCEL DATA
    // ============================================

    /**
     * Fill all personal details from loaded Excel data
     * Maps Excel column headers to form fields
     */
    public void fillPersonalDetailsFromExcel() {
        if (employeeData == null || employeeData.isEmpty()) {
            throw new RuntimeException("[ERROR] No employee data loaded. Call loadEmployeeDataFromExcel() first.");
        }

        System.out.println("[INFO] Filling personal details from Excel data...");

        // Map Excel headers to form fields
        String firstName = employeeData.getOrDefault("Employee First Name", "");
        String middleName = employeeData.getOrDefault("Employee Middle Name", "");
        String lastName = employeeData.getOrDefault("Employee Last Name", "");
        String employeeId = employeeData.getOrDefault("Employee ID", "");
        String licenseExpiry = employeeData.getOrDefault("License Expiry Date", "");
        String dateOfBirth = employeeData.getOrDefault("Date of Birth", "");

        // Enter data into form fields
        enterFirstName(firstName);
        enterMiddleName(middleName);
        enterLastName(lastName);
        enterEmployeeId(employeeId);
        enterLicenseExpiryDate(licenseExpiry);
        enterDateOfBirth(dateOfBirth);

        System.out.println("[INFO] Personal details filled from Excel");
    }

    // ============================================
    // SAVE AND VERIFICATION METHODS
    // ============================================

    /**
     * Click the Save button to submit the form
     */
    public void clickSaveButton() {
        ElementHelper.scrollIntoView(driver, saveButton);
        ElementHelper.safeClickElement(driver, saveButton);
        System.out.println("[INFO] Clicked Save button");
        ElementHelper.sleep(2000); // Wait for save operation
    }

    /**
     * Check if success toast is displayed
     * @return true if success message appears
     */
    public boolean isSuccessToastDisplayed() {
        boolean displayed = ElementHelper.isElementDisplayed(driver, successToast, 5);
        if (displayed) {
            String message = getToastMessageText();
            System.out.println("[INFO] Success toast displayed: " + message);
        }
        return displayed;
    }

    /**
     * Get the toast message text
     * @return Toast message content
     */
    public String getToastMessageText() {
        try {
            return ElementHelper.getTextFromElement(driver, toastMessage, 5);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Wait for success toast to appear and disappear
     */
    public void waitForSaveConfirmation() {
        if (isSuccessToastDisplayed()) {
            ElementHelper.waitForElementToDisappear(driver, successToast, 10);
            System.out.println("[INFO] Save confirmed - toast dismissed");
        }
    }

    // ============================================
    // VERIFICATION METHODS
    // ============================================

    /**
     * Get the current value of First Name field
     * @return Current first name value
     */
    public String getFirstNameValue() {
        return ElementHelper.getInputValue(driver, firstNameInput, 10);
    }

    /**
     * Get the current value of Middle Name field
     * @return Current middle name value
     */
    public String getMiddleNameValue() {
        return ElementHelper.getInputValue(driver, middleNameInput, 10);
    }

    /**
     * Get the current value of Last Name field
     * @return Current last name value
     */
    public String getLastNameValue() {
        return ElementHelper.getInputValue(driver, lastNameInput, 10);
    }

    /**
     * Get the current value of Employee ID field
     * @return Current employee ID value
     */
    public String getEmployeeIdValue() {
        return ElementHelper.getInputValue(driver, employeeIdInput, 10);
    }

    /**
     * Verify that form data matches Excel data
     * @return true if all fields match
     */
    public boolean verifyFormDataMatchesExcel() {
        if (employeeData == null) {
            return false;
        }

        String expectedFirstName = employeeData.getOrDefault("Employee First Name", "");
        String expectedMiddleName = employeeData.getOrDefault("Employee Middle Name", "");
        String expectedLastName = employeeData.getOrDefault("Employee Last Name", "");

        String actualFirstName = getFirstNameValue();
        String actualMiddleName = getMiddleNameValue();
        String actualLastName = getLastNameValue();

        boolean firstNameMatch = actualFirstName.equals(expectedFirstName);
        boolean middleNameMatch = actualMiddleName.equals(expectedMiddleName);
        boolean lastNameMatch = actualLastName.equals(expectedLastName);

        System.out.println("[INFO] Verifying form data:");
        System.out.println("   First Name: " + (firstNameMatch ? "PASS" : "FAIL") + " " + actualFirstName + " (expected: " + expectedFirstName + ")");
        System.out.println("   Middle Name: " + (middleNameMatch ? "PASS" : "FAIL") + " " + actualMiddleName + " (expected: " + expectedMiddleName + ")");
        System.out.println("   Last Name: " + (lastNameMatch ? "PASS" : "FAIL") + " " + actualLastName + " (expected: " + expectedLastName + ")");

        return firstNameMatch && middleNameMatch && lastNameMatch;
    }

    // ============================================
    // COMPLETE WORKFLOW METHOD
    // ============================================

    /**
     * Complete workflow: Load data, clear fields, fill from Excel, and save
     */
    public void updatePersonalDetailsFromExcel() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("[INFO] Starting Personal Details Update from Excel");
        System.out.println("=".repeat(50));

        // Step 1: Load data from Excel
        loadEmployeeDataFromExcel();

        // Step 2: Clear all fields
        clearAllFields();

        // Step 3: Fill form from Excel data
        fillPersonalDetailsFromExcel();

        // Step 4: Save the changes
        clickSaveButton();

        System.out.println("=".repeat(50));
        System.out.println("[INFO] Personal Details Update Complete");
        System.out.println("=".repeat(50) + "\n");
    }

    // ============================================
    // FILE UPLOAD METHODS
    // ============================================

    /** Path to test data folder for attachments */
    private static final String TEST_DATA_PATH = "src/test/resources/testdata/";

    /**
     * Click the Add Attachment button to open the attachment section
     */
    public void clickAddAttachmentButton() {
        ElementHelper.scrollIntoView(driver, addAttachmentButton);
        ElementHelper.safeClickElement(driver, addAttachmentButton);
        System.out.println("[INFO] Clicked Add Attachment button");
        ElementHelper.sleep(1000); // Wait for attachment form to appear
    }

    /**
     * Upload a file using the file input
     * @param fileName Name of the file in testdata folder (e.g., "sample.pdf")
     */
    public void uploadFile(String fileName) {
        String filePath = System.getProperty("user.dir") + "/" + TEST_DATA_PATH + fileName;
        filePath = filePath.replace("/", java.io.File.separator);
        
        System.out.println("[INFO] Uploading file: " + filePath);
        
        // Verify file exists
        java.io.File file = new java.io.File(filePath);
        if (!file.exists()) {
            throw new RuntimeException("[ERROR] File not found: " + filePath);
        }
        
        try {
            // Find the hidden file input and send the file path
            org.openqa.selenium.WebElement fileInputElement = driver.findElement(fileInput);
            fileInputElement.sendKeys(filePath);
            System.out.println("[INFO] File uploaded: " + fileName);
            ElementHelper.sleep(1000); // Wait for file to be attached
        } catch (Exception e) {
            System.out.println("[WARN] Standard upload failed, trying alternative method...");
            uploadFileAlternative(filePath);
        }
    }

    /**
     * Alternative file upload method using JavaScript to make hidden input visible
     * @param filePath Full path to the file
     */
    private void uploadFileAlternative(String filePath) {
        try {
            // Make the file input visible using JavaScript
            org.openqa.selenium.WebElement fileInputElement = driver.findElement(fileInput);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "arguments[0].style.display='block'; arguments[0].style.visibility='visible';", 
                fileInputElement
            );
            ElementHelper.sleep(500);
            
            // Now send the file path
            fileInputElement.sendKeys(filePath);
            System.out.println("[INFO] File uploaded using alternative method");
        } catch (Exception e) {
            throw new RuntimeException("[ERROR] Failed to upload file: " + e.getMessage(), e);
        }
    }

    /**
     * Upload a PDF document from the testdata folder
     * @param pdfFileName Name of the PDF file (e.g., "TestDocument.pdf")
     */
    public void uploadPdfDocument(String pdfFileName) {
        System.out.println("[INFO] Starting PDF upload: " + pdfFileName);
        uploadFile(pdfFileName);
    }

    /**
     * Click the Save Attachment button
     * Uses multiple strategies to find the correct button (not the personal details save)
     */
    public void clickSaveAttachmentButton() {
        // Wait for attachment form to be fully loaded
        ElementHelper.sleep(500);
        
        // Strategy: Find all submit buttons and click the one in the attachment section
        // The attachment save button is typically after the file input section
        By[] buttonLocators = {
            // Strategy 1: Button within attachment container
            By.xpath("//div[contains(@class,'orangehrm-attachment')]//button[@type='submit']"),
            // Strategy 2: Button near the file input (same form)
            By.xpath("//div[contains(@class,'oxd-file-input')]//ancestor::div[contains(@class,'oxd-form')]//button[@type='submit']"),
            // Strategy 3: The last submit button on the page (attachment form is at bottom)
            By.xpath("(//button[@type='submit'])[last()]"),
            // Strategy 4: Submit button that does NOT have data-v-6653c066 (personal details marker)
            By.xpath("//button[@type='submit' and not(@data-v-6653c066)]"),
            // Strategy 5: Fallback to second submit button
            By.xpath("(//button[@type='submit'])[2]")
        };
        
        boolean clicked = false;
        for (By locator : buttonLocators) {
            try {
                if (ElementHelper.isElementDisplayed(driver, locator, 2)) {
                    ElementHelper.scrollIntoView(driver, locator);
                    ElementHelper.safeClickElement(driver, locator);
                    System.out.println("[INFO] Clicked Save Attachment button");
                    clicked = true;
                    break;
                }
            } catch (Exception e) {
                // Try next locator
            }
        }
        
        if (!clicked) {
            throw new RuntimeException("[ERROR] Could not find Save Attachment button");
        }
        
        ElementHelper.sleep(2000); // Wait for save operation
    }

    /**
     * Verify if the file input shows a selected file (not "No file selected")
     * @return true if a file is selected
     */
    public boolean isFileSelected() {
        try {
            String text = ElementHelper.getTextFromElement(driver, fileInputDiv, 5);
            boolean selected = !text.contains("No file selected") && !text.isEmpty();
            System.out.println(selected ? "[INFO] File is selected" : "[ERROR] No file selected");
            return selected;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if attachment was saved successfully (success toast appeared)
     * @return true if success toast is displayed
     */
    public boolean isAttachmentSavedSuccessfully() {
        boolean success = isSuccessToastDisplayed();
        if (success) {
            System.out.println("[INFO] Attachment saved successfully!");
        } else {
            System.out.println("[ERROR] Attachment save confirmation not received");
        }
        return success;
    }

    /**
     * Complete workflow: Add attachment from testdata folder and save
     * @param fileName Name of the file to upload
     */
    public void addAttachmentAndSave(String fileName) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("[INFO] Starting Attachment Upload");
        System.out.println("=".repeat(50));

        // Step 1: Click Add Attachment button
        clickAddAttachmentButton();

        // Step 2: Upload the file
        uploadPdfDocument(fileName);

        // Step 3: Click Save Attachment button
        clickSaveAttachmentButton();

        System.out.println("=".repeat(50));
        System.out.println("[INFO] Attachment Upload Complete");
        System.out.println("=".repeat(50) + "\n");
    }

    // ============================================
    // ATTACHMENT TABLE VERIFICATION
    // ============================================

    /** Attachment table container */
    private By attachmentTableContainer = By.cssSelector("div.orangehrm-attachment");

    /** Attachment table rows */
    private By attachmentTableRows = By.cssSelector("div.orangehrm-attachment div.oxd-table-body div.oxd-table-row");

    /** Attachment file name cells */
    private By attachmentFileNames = By.xpath("//div[contains(@class,'orangehrm-attachment')]//div[@role='row']//div[@role='cell'][2]//div");

    /**
     * Scroll to the attachments table section
     */
    public void scrollToAttachmentsTable() {
        try {
            if (ElementHelper.isElementDisplayed(driver, attachmentTableContainer, 5)) {
                ElementHelper.scrollIntoView(driver, attachmentTableContainer);
                System.out.println("[INFO] Scrolled to attachments table");
            }
        } catch (Exception e) {
            System.out.println("[WARN] Could not scroll to attachments table");
        }
    }

    /**
     * Get count of attachments in the table
     * @return Number of attachments
     */
    public int getAttachmentCount() {
        try {
            scrollToAttachmentsTable();
            int count = driver.findElements(attachmentTableRows).size();
            System.out.println("[INFO] Attachment count: " + count);
            return count;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Verify if a specific file is listed in attachments
     * Uses multiple locator strategies for robustness
     * @param fileName Name of the file to look for
     * @return true if file is found in attachments list
     */
    public boolean isFileInAttachmentsList(String fileName) {
        try {
            // Wait for page to stabilize after save
            ElementHelper.sleep(2000);
            
            // Scroll to attachments section
            scrollToAttachmentsTable();
            ElementHelper.sleep(1000);
            
            // Try multiple locator strategies
            By[] locators = {
                // Strategy 1: Look for file name in any cell
                By.xpath("//div[contains(@class,'orangehrm-attachment')]//div[contains(@class,'oxd-table-cell')]//div[contains(text(),'" + fileName + "')]"),
                // Strategy 2: Look in the second column (typically file name column)
                By.xpath("//div[contains(@class,'orangehrm-attachment')]//div[@role='row']//div[@role='cell'][2]//div[contains(text(),'" + fileName + "')]"),
                // Strategy 3: Generic text search in table
                By.xpath("//div[contains(@class,'oxd-table-body')]//div[contains(text(),'" + fileName + "')]"),
                // Strategy 4: More specific cell locator
                By.xpath("//div[contains(@class,'orangehrm-attachment')]//div[contains(@class,'oxd-table-row')]//div[contains(text(),'" + fileName + "')]")
            };
            
            for (By locator : locators) {
                try {
                    if (ElementHelper.isElementDisplayed(driver, locator, 3)) {
                        System.out.println("[INFO] File '" + fileName + "' found in attachments");
                        return true;
                    }
                } catch (Exception e) {
                    // Try next locator
                }
            }
            
            // If still not found, log all visible file names for debugging
            logVisibleAttachments();
            
            System.out.println("[ERROR] File '" + fileName + "' not found in attachments");
            return false;
            
        } catch (Exception e) {
            System.out.println("[ERROR] Exception while checking attachments: " + e.getMessage());
            return false;
        }
    }

    /**
     * Log all visible attachment file names for debugging
     */
    private void logVisibleAttachments() {
        try {
            java.util.List<org.openqa.selenium.WebElement> rows = driver.findElements(attachmentTableRows);
            System.out.println("[DEBUG] Found " + rows.size() + " attachment rows");
            
            if (rows.isEmpty()) {
                // Try to find any table content
                java.util.List<org.openqa.selenium.WebElement> cells = driver.findElements(
                    By.xpath("//div[contains(@class,'orangehrm-attachment')]//div[contains(@class,'oxd-table-cell')]")
                );
                System.out.println("[DEBUG] Found " + cells.size() + " cells in attachments section");
                
                for (int i = 0; i < Math.min(cells.size(), 10); i++) {
                    String text = cells.get(i).getText().trim();
                    if (!text.isEmpty()) {
                        System.out.println("[DEBUG] Cell content: " + text);
                    }
                }
            } else {
                for (int i = 0; i < rows.size(); i++) {
                    String rowText = rows.get(i).getText().trim();
                    System.out.println("[DEBUG] Row " + (i + 1) + ": " + rowText.substring(0, Math.min(rowText.length(), 100)));
                }
            }
        } catch (Exception e) {
            System.out.println("[DEBUG] Could not log attachments: " + e.getMessage());
        }
    }
}

