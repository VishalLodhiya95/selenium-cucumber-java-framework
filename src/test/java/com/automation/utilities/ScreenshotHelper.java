package com.automation.utilities;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ScreenshotHelper - Utility for capturing screenshots
 */
public class ScreenshotHelper {

    private static final String SCREENSHOT_PATH = "target/screenshots/";

    /**
     * Capture screenshot and save to file
     */
    public static String captureScreenshot(WebDriver driver, String screenshotName) {
        try {
            // Create screenshots directory if it doesn't exist
            Path path = Paths.get(SCREENSHOT_PATH);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            // Generate filename with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = screenshotName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp + ".png";
            String filePath = SCREENSHOT_PATH + fileName;

            // Capture screenshot
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File(filePath);
            Files.copy(srcFile.toPath(), destFile.toPath());

            System.out.println("Screenshot saved: " + filePath);
            return filePath;

        } catch (IOException e) {
            System.out.println("Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get screenshot as bytes (for embedding in reports)
     */
    public static byte[] captureScreenshotAsBytes(WebDriver driver) {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            System.out.println("Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }
}



