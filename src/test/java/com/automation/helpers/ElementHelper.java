package com.automation.helpers;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ElementHelper - Common helper methods for interacting with web elements
 * Similar to the C# ElementHelper class
 */
public class ElementHelper {

    /**
     * Wait for element to be clickable and click it
     */
    public static void safeClickElement(WebDriver driver, By locator, int maxRetries, int timeoutInSeconds) {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                element.click();
                System.out.println("Successfully clicked element: " + locator + " (attempt " + attempt + ")");
                return;
            } catch (StaleElementReferenceException e) {
                System.out.println("Stale element, retrying... (attempt " + attempt + "/" + maxRetries + ")");
                if (attempt == maxRetries) {
                    throw new RuntimeException("Failed to click element after " + maxRetries + " attempts: " + locator, e);
                }
            } catch (ElementClickInterceptedException e) {
                System.out.println("Element click intercepted, trying JavaScript click... (attempt " + attempt + ")");
                try {
                    WebElement element = driver.findElement(locator);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                    System.out.println("Successfully clicked element using JavaScript: " + locator);
                    return;
                } catch (Exception jsEx) {
                    if (attempt == maxRetries) {
                        throw new RuntimeException("Failed to click element: " + locator, jsEx);
                    }
                }
            } catch (TimeoutException e) {
                System.out.println("Timeout waiting for element... (attempt " + attempt + "/" + maxRetries + ")");
                if (attempt == maxRetries) {
                    throw new RuntimeException("Element not clickable after " + timeoutInSeconds + "s: " + locator, e);
                }
            }
            sleep(500);
        }
    }

    /**
     * Overload with default values
     */
    public static void safeClickElement(WebDriver driver, By locator) {
        safeClickElement(driver, locator, 3, 10);
    }

    /**
     * Wait for element and send keys
     */
    public static void safeSendKeys(WebDriver driver, By locator, String text, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.clear();
            element.sendKeys(text);
            System.out.println("Successfully entered text: '" + text + "'");
        } catch (Exception e) {
            throw new RuntimeException("Failed to send keys to element: " + locator, e);
        }
    }

    /**
     * Overload with default timeout
     */
    public static void safeSendKeys(WebDriver driver, By locator, String text) {
        safeSendKeys(driver, locator, text, 10);
    }

    /**
     * Wait for element to be visible
     */
    public static WebElement waitForElementToBeVisible(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            throw new RuntimeException("Element not visible after " + timeoutInSeconds + "s: " + locator, e);
        }
    }

    /**
     * Wait for element to be interactable (clickable)
     */
    public static WebElement waitForElementToBeInteractable(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            throw new RuntimeException("Element not interactable after " + timeoutInSeconds + "s: " + locator, e);
        }
    }

    /**
     * Check if element is displayed
     */
    public static boolean isElementDisplayed(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Get text from element
     */
    public static String getTextFromElement(WebDriver driver, By locator, int timeoutInSeconds) {
        WebElement element = waitForElementToBeVisible(driver, locator, timeoutInSeconds);
        String text = element.getText().trim();
        System.out.println("Got text from element: '" + text + "'");
        return text;
    }

    /**
     * Scroll element into view
     */
    public static void scrollIntoView(WebDriver driver, By locator) {
        try {
            WebElement element = driver.findElement(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            sleep(500);
            System.out.println("Scrolled to element: " + locator);
        } catch (Exception e) {
            System.out.println("Could not scroll to element: " + e.getMessage());
        }
    }

    /**
     * Wait for page to load completely
     */
    public static void waitForPageLoad(WebDriver driver, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                    .executeScript("return document.readyState").equals("complete"));
            System.out.println("Page loaded completely");
        } catch (Exception e) {
            System.out.println("Page load timeout: " + e.getMessage());
        }
    }

    /**
     * Thread sleep helper
     */
    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ============================================
    // DROPDOWN METHODS
    // ============================================

    /**
     * Select dropdown option by visible text
     */
    public static void selectByVisibleText(WebDriver driver, By locator, String text, int timeoutInSeconds) {
        try {
            WebElement element = waitForElementToBeInteractable(driver, locator, timeoutInSeconds);
            Select dropdown = new Select(element);
            dropdown.selectByVisibleText(text);
            System.out.println("Selected dropdown option by text: '" + text + "'");
        } catch (Exception e) {
            throw new RuntimeException("Failed to select dropdown option by text: " + text, e);
        }
    }

    /**
     * Select dropdown option by visible text (default timeout)
     */
    public static void selectByVisibleText(WebDriver driver, By locator, String text) {
        selectByVisibleText(driver, locator, text, 10);
    }

    /**
     * Select dropdown option by index (0-based)
     */
    public static void selectByIndex(WebDriver driver, By locator, int index, int timeoutInSeconds) {
        try {
            WebElement element = waitForElementToBeInteractable(driver, locator, timeoutInSeconds);
            Select dropdown = new Select(element);
            dropdown.selectByIndex(index);
            System.out.println("Selected dropdown option by index: " + index);
        } catch (Exception e) {
            throw new RuntimeException("Failed to select dropdown option by index: " + index, e);
        }
    }

    /**
     * Select dropdown option by index (default timeout)
     */
    public static void selectByIndex(WebDriver driver, By locator, int index) {
        selectByIndex(driver, locator, index, 10);
    }

    /**
     * Select dropdown option by value attribute
     */
    public static void selectByValue(WebDriver driver, By locator, String value, int timeoutInSeconds) {
        try {
            WebElement element = waitForElementToBeInteractable(driver, locator, timeoutInSeconds);
            Select dropdown = new Select(element);
            dropdown.selectByValue(value);
            System.out.println("Selected dropdown option by value: '" + value + "'");
        } catch (Exception e) {
            throw new RuntimeException("Failed to select dropdown option by value: " + value, e);
        }
    }

    /**
     * Select dropdown option by value (default timeout)
     */
    public static void selectByValue(WebDriver driver, By locator, String value) {
        selectByValue(driver, locator, value, 10);
    }

    /**
     * Select first option from dropdown
     */
    public static void selectFirstOption(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            WebElement element = waitForElementToBeInteractable(driver, locator, timeoutInSeconds);
            Select dropdown = new Select(element);
            List<WebElement> options = dropdown.getOptions();
            if (options.size() > 0) {
                dropdown.selectByIndex(0);
                System.out.println("Selected first dropdown option: '" + options.get(0).getText() + "'");
            } else {
                throw new RuntimeException("Dropdown has no options");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to select first dropdown option: " + locator, e);
        }
    }

    /**
     * Select first option from dropdown (default timeout)
     */
    public static void selectFirstOption(WebDriver driver, By locator) {
        selectFirstOption(driver, locator, 10);
    }

    /**
     * Select first non-empty option from dropdown (skips placeholder options)
     */
    public static void selectFirstNonEmptyOption(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            WebElement element = waitForElementToBeInteractable(driver, locator, timeoutInSeconds);
            Select dropdown = new Select(element);
            List<WebElement> options = dropdown.getOptions();
            for (int i = 0; i < options.size(); i++) {
                String optionText = options.get(i).getText().trim();
                if (!optionText.isEmpty() && !optionText.equals("--Select--") && !optionText.equals("Select")) {
                    dropdown.selectByIndex(i);
                    System.out.println("Selected first non-empty option: '" + optionText + "'");
                    return;
                }
            }
            throw new RuntimeException("No non-empty options found in dropdown");
        } catch (Exception e) {
            throw new RuntimeException("Failed to select first non-empty option: " + locator, e);
        }
    }

    /**
     * Get selected option text from dropdown
     */
    public static String getSelectedOptionText(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            WebElement element = waitForElementToBeVisible(driver, locator, timeoutInSeconds);
            Select dropdown = new Select(element);
            String selectedText = dropdown.getFirstSelectedOption().getText().trim();
            System.out.println("Selected option: '" + selectedText + "'");
            return selectedText;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get selected option text: " + locator, e);
        }
    }

    /**
     * Get all dropdown options as list of strings
     */
    public static List<String> getAllDropdownOptions(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            WebElement element = waitForElementToBeVisible(driver, locator, timeoutInSeconds);
            Select dropdown = new Select(element);
            List<String> options = dropdown.getOptions().stream()
                    .map(opt -> opt.getText().trim())
                    .collect(Collectors.toList());
            System.out.println("Found " + options.size() + " dropdown options");
            return options;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get dropdown options: " + locator, e);
        }
    }

    // ============================================
    // TEXT & ATTRIBUTE METHODS
    // ============================================

    /**
     * Get text from element (with default timeout)
     */
    public static String getTextFromElement(WebDriver driver, By locator) {
        return getTextFromElement(driver, locator, 10);
    }

    /**
     * Get attribute value from element
     */
    public static String getAttributeFromElement(WebDriver driver, By locator, String attributeName, int timeoutInSeconds) {
        try {
            WebElement element = waitForElementToBeVisible(driver, locator, timeoutInSeconds);
            String value = element.getAttribute(attributeName);
            System.out.println("Got attribute '" + attributeName + "': '" + value + "'");
            return value;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get attribute: " + attributeName, e);
        }
    }

    /**
     * Get 'value' attribute from input element
     */
    public static String getInputValue(WebDriver driver, By locator, int timeoutInSeconds) {
        return getAttributeFromElement(driver, locator, "value", timeoutInSeconds);
    }

    /**
     * Get 'value' attribute from input element (default timeout)
     */
    public static String getInputValue(WebDriver driver, By locator) {
        return getInputValue(driver, locator, 10);
    }

    /**
     * Get inner HTML of element
     */
    public static String getInnerHtml(WebDriver driver, By locator, int timeoutInSeconds) {
        return getAttributeFromElement(driver, locator, "innerHTML", timeoutInSeconds);
    }

    /**
     * Get text from multiple elements
     */
    public static List<String> getTextFromElements(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            List<WebElement> elements = driver.findElements(locator);
            List<String> texts = elements.stream()
                    .map(e -> e.getText().trim())
                    .collect(Collectors.toList());
            System.out.println("Got text from " + texts.size() + " elements");
            return texts;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get text from elements: " + locator, e);
        }
    }

    // ============================================
    // CHECKBOX & RADIO BUTTON METHODS
    // ============================================

    /**
     * Check a checkbox (if not already checked)
     */
    public static void checkCheckbox(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            WebElement checkbox = waitForElementToBeInteractable(driver, locator, timeoutInSeconds);
            if (!checkbox.isSelected()) {
                checkbox.click();
                System.out.println("Checkbox checked: " + locator);
            } else {
                System.out.println("Checkbox already checked: " + locator);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to check checkbox: " + locator, e);
        }
    }

    /**
     * Uncheck a checkbox (if checked)
     */
    public static void uncheckCheckbox(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            WebElement checkbox = waitForElementToBeInteractable(driver, locator, timeoutInSeconds);
            if (checkbox.isSelected()) {
                checkbox.click();
                System.out.println("Checkbox unchecked: " + locator);
            } else {
                System.out.println("Checkbox already unchecked: " + locator);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to uncheck checkbox: " + locator, e);
        }
    }

    /**
     * Check if checkbox is selected
     */
    public static boolean isCheckboxSelected(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            WebElement checkbox = waitForElementToBeVisible(driver, locator, timeoutInSeconds);
            return checkbox.isSelected();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Select a radio button
     */
    public static void selectRadioButton(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            WebElement radio = waitForElementToBeInteractable(driver, locator, timeoutInSeconds);
            if (!radio.isSelected()) {
                radio.click();
                System.out.println("Radio button selected: " + locator);
            } else {
                System.out.println("Radio button already selected: " + locator);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to select radio button: " + locator, e);
        }
    }

    // ============================================
    // CLEAR & INPUT METHODS
    // ============================================

    /**
     * Clear input field
     */
    public static void clearField(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            WebElement element = waitForElementToBeInteractable(driver, locator, timeoutInSeconds);
            element.clear();
            System.out.println("Field cleared: " + locator);
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear field: " + locator, e);
        }
    }

    /**
     * Clear field using keyboard (Ctrl+A, Delete)
     */
    public static void clearFieldWithKeyboard(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            WebElement element = waitForElementToBeInteractable(driver, locator, timeoutInSeconds);
            element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            element.sendKeys(Keys.DELETE);
            System.out.println("Field cleared with keyboard: " + locator);
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear field with keyboard: " + locator, e);
        }
    }

    /**
     * Send keys without clearing first
     */
    public static void appendText(WebDriver driver, By locator, String text, int timeoutInSeconds) {
        try {
            WebElement element = waitForElementToBeInteractable(driver, locator, timeoutInSeconds);
            element.sendKeys(text);
            System.out.println("Appended text: '" + text + "'");
        } catch (Exception e) {
            throw new RuntimeException("Failed to append text: " + locator, e);
        }
    }

    /**
     * Press Enter key on element
     */
    public static void pressEnter(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            WebElement element = waitForElementToBeInteractable(driver, locator, timeoutInSeconds);
            element.sendKeys(Keys.ENTER);
            System.out.println("Pressed Enter key");
        } catch (Exception e) {
            throw new RuntimeException("Failed to press Enter: " + locator, e);
        }
    }

    /**
     * Press Tab key on element
     */
    public static void pressTab(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            WebElement element = waitForElementToBeInteractable(driver, locator, timeoutInSeconds);
            element.sendKeys(Keys.TAB);
            System.out.println("Pressed Tab key");
        } catch (Exception e) {
            throw new RuntimeException("Failed to press Tab: " + locator, e);
        }
    }

    // ============================================
    // WAIT METHODS
    // ============================================

    /**
     * Wait for element to disappear
     */
    public static void waitForElementToDisappear(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
            System.out.println("Element disappeared: " + locator);
        } catch (TimeoutException e) {
            System.out.println("Element still visible after " + timeoutInSeconds + "s: " + locator);
        }
    }

    /**
     * Wait for loader to disappear
     */
    public static void waitForLoaderToDisappear(WebDriver driver, By loaderLocator, int timeoutInSeconds) {
        try {
            // First check if loader is present
            List<WebElement> loaders = driver.findElements(loaderLocator);
            if (loaders.size() > 0 && loaders.get(0).isDisplayed()) {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
                wait.until(ExpectedConditions.invisibilityOfElementLocated(loaderLocator));
                System.out.println("Loader disappeared");
            }
        } catch (Exception e) {
            System.out.println("Loader wait issue: " + e.getMessage());
        }
    }

    /**
     * Wait for text to be present in element
     */
    public static void waitForTextInElement(WebDriver driver, By locator, String text, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
            System.out.println("Text '" + text + "' found in element");
        } catch (TimeoutException e) {
            throw new RuntimeException("Text '" + text + "' not found in element after " + timeoutInSeconds + "s", e);
        }
    }

    /**
     * Wait for element count
     */
    public static void waitForElementCount(WebDriver driver, By locator, int expectedCount, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            wait.until(ExpectedConditions.numberOfElementsToBe(locator, expectedCount));
            System.out.println("Found " + expectedCount + " element(s)");
        } catch (TimeoutException e) {
            throw new RuntimeException("Expected " + expectedCount + " elements not found", e);
        }
    }

    // ============================================
    // ELEMENT COUNT & EXISTENCE METHODS
    // ============================================

    /**
     * Get count of elements matching locator
     */
    public static int getElementCount(WebDriver driver, By locator) {
        List<WebElement> elements = driver.findElements(locator);
        System.out.println("Found " + elements.size() + " element(s)");
        return elements.size();
    }

    /**
     * Check if element exists (without waiting)
     */
    public static boolean elementExists(WebDriver driver, By locator) {
        return driver.findElements(locator).size() > 0;
    }

    /**
     * Check if element is enabled
     */
    public static boolean isElementEnabled(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            WebElement element = waitForElementToBeVisible(driver, locator, timeoutInSeconds);
            return element.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    // ============================================
    // HOVER & ACTIONS METHODS
    // ============================================

    /**
     * Hover over element
     */
    public static void hoverOverElement(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            WebElement element = waitForElementToBeVisible(driver, locator, timeoutInSeconds);
            org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
            actions.moveToElement(element).perform();
            System.out.println("Hovered over element: " + locator);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hover over element: " + locator, e);
        }
    }

    /**
     * Double click on element
     */
    public static void doubleClick(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            WebElement element = waitForElementToBeInteractable(driver, locator, timeoutInSeconds);
            org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
            actions.doubleClick(element).perform();
            System.out.println("Double clicked element: " + locator);
        } catch (Exception e) {
            throw new RuntimeException("Failed to double click element: " + locator, e);
        }
    }

    /**
     * Right click on element
     */
    public static void rightClick(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            WebElement element = waitForElementToBeInteractable(driver, locator, timeoutInSeconds);
            org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
            actions.contextClick(element).perform();
            System.out.println("Right clicked element: " + locator);
        } catch (Exception e) {
            throw new RuntimeException("Failed to right click element: " + locator, e);
        }
    }

    // ============================================
    // FRAME & WINDOW METHODS
    // ============================================

    /**
     * Switch to frame by locator
     */
    public static void switchToFrame(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            WebElement frame = waitForElementToBeVisible(driver, locator, timeoutInSeconds);
            driver.switchTo().frame(frame);
            System.out.println("Switched to frame: " + locator);
        } catch (Exception e) {
            throw new RuntimeException("Failed to switch to frame: " + locator, e);
        }
    }

    /**
     * Switch to default content (exit frame)
     */
    public static void switchToDefaultContent(WebDriver driver) {
        driver.switchTo().defaultContent();
        System.out.println("Switched to default content");
    }

    /**
     * Accept alert
     */
    public static void acceptAlert(WebDriver driver, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
            System.out.println("Alert accepted");
        } catch (Exception e) {
            System.out.println("No alert present or could not accept: " + e.getMessage());
        }
    }

    /**
     * Dismiss alert
     */
    public static void dismissAlert(WebDriver driver, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().dismiss();
            System.out.println("Alert dismissed");
        } catch (Exception e) {
            System.out.println("No alert present or could not dismiss: " + e.getMessage());
        }
    }

    /**
     * Get alert text
     */
    public static String getAlertText(WebDriver driver, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            wait.until(ExpectedConditions.alertIsPresent());
            String alertText = driver.switchTo().alert().getText();
            System.out.println("Alert text: " + alertText);
            return alertText;
        } catch (Exception e) {
            throw new RuntimeException("No alert present: " + e.getMessage());
        }
    }
}



