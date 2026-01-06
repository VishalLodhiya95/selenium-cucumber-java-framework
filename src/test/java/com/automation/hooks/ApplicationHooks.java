package com.automation.hooks;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import com.automation.drivers.DriverFactory;
import com.automation.reports.HtmlReportGenerator;
import com.automation.reports.PdfReportGenerator;
import com.automation.reports.TestResultCollector;
import com.automation.utilities.EmailHelper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * ApplicationHooks - Cucumber hooks for test lifecycle management
 */
public class ApplicationHooks {

    private LocalDateTime scenarioStartTime;

    /**
     * Before all tests - Initialize test run
     */
    @BeforeAll
    public static void beforeAll() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TEST RUN STARTED");
        System.out.println("=".repeat(60));
        TestResultCollector.setStartTime();
    }

    /**
     * Before each scenario - Initialize the browser
     */
    @Before
    public void beforeScenario(Scenario scenario) {
        scenarioStartTime = LocalDateTime.now();
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Starting Scenario: " + scenario.getName());
        System.out.println("   Tags: " + scenario.getSourceTagNames());
        System.out.println("=".repeat(60));
        
        // Initialize the driver
        DriverFactory.getDriver();
    }

    /**
     * After each scenario - Cleanup and take screenshot on failure
     */
    @After
    public void afterScenario(Scenario scenario) {
        LocalDateTime scenarioEndTime = LocalDateTime.now();
        Duration duration = Duration.between(scenarioStartTime, scenarioEndTime);
        String status = scenario.isFailed() ? "FAILED" : "PASSED";
        String errorMessage = "";
        String screenshotBase64 = null;
        
        try {
            // Take screenshot if scenario failed
            if (scenario.isFailed()) {
                System.out.println("Scenario FAILED: " + scenario.getName());
                
                // Capture screenshot with retry logic
                screenshotBase64 = captureScreenshotWithRetry(scenario);
            } else {
                System.out.println("Scenario PASSED: " + scenario.getName());
            }
        } finally {
            // Collect test result
            String featureName = scenario.getUri().toString();
            if (featureName.contains("/")) {
                featureName = featureName.substring(featureName.lastIndexOf("/") + 1);
            }
            // Handle Windows path separator
            if (featureName.contains("\\")) {
                featureName = featureName.substring(featureName.lastIndexOf("\\") + 1);
            }
            TestResultCollector.addResult(
                scenario.getName(),
                status,
                duration,
                scenarioStartTime,
                scenarioEndTime,
                errorMessage,
                featureName,
                screenshotBase64
            );
            
            // Always quit the driver
            DriverFactory.quitDriver();
        }
        
        System.out.println("=".repeat(60) + "\n");
    }

    /**
     * Capture screenshot with retry logic for reliability
     */
    private String captureScreenshotWithRetry(Scenario scenario) {
        String screenshotBase64 = null;
        int maxRetries = 3;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                // Small delay to ensure page is stable
                Thread.sleep(500);
                
                // Check if driver is still valid
                org.openqa.selenium.WebDriver driver = DriverFactory.getDriver();
                if (driver == null) {
                    System.out.println("Driver is null, cannot capture screenshot");
                    break;
                }
                
                // Capture screenshot
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                
                if (screenshot != null && screenshot.length > 0) {
                    // Attach to Cucumber report
                    scenario.attach(screenshot, "image/png", "Failed_Screenshot");
                    
                    // Convert to Base64 for HTML report
                    screenshotBase64 = Base64.getEncoder().encodeToString(screenshot);
                    System.out.println("Screenshot captured successfully (attempt " + attempt + ", size: " + screenshot.length + " bytes)");
                    break;
                } else {
                    System.out.println("Screenshot was empty (attempt " + attempt + ")");
                }
            } catch (Exception e) {
                System.out.println("Screenshot capture attempt " + attempt + " failed: " + e.getMessage());
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(1000); // Wait before retry
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        if (screenshotBase64 == null) {
            System.out.println("Failed to capture screenshot after " + maxRetries + " attempts");
        }
        
        return screenshotBase64;
    }

    /**
     * After all tests - Generate reports
     */
    @AfterAll
    public static void afterAll() {
        TestResultCollector.setEndTime();
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TEST RUN COMPLETED");
        System.out.println("=".repeat(60));
        
        // Print summary
        System.out.println("Total: " + TestResultCollector.getTotalCount());
        System.out.println("Passed: " + TestResultCollector.getPassedCount());
        System.out.println("Failed: " + TestResultCollector.getFailedCount());
        
        // Generate PDF report
        String pdfPath = PdfReportGenerator.generateReport(
            TestResultCollector.getAllResults(),
            DriverFactory.getBrowserName()
        );
        
        if (pdfPath != null) {
            System.out.println("PDF Report: " + pdfPath);
        }
        
        // Generate Custom HTML report
        String htmlPath = HtmlReportGenerator.generateReport(
            TestResultCollector.getAllResults(),
            DriverFactory.getBrowserName()
        );
        
        if (htmlPath != null) {
            System.out.println("HTML Report: " + htmlPath);
        }
        
        // Send email report
        EmailHelper.sendTestReport(pdfPath, htmlPath, DriverFactory.getBrowserName());
        
        System.out.println("=".repeat(60) + "\n");
    }
}



