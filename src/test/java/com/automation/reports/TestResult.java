package com.automation.reports;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * TestResult - Holds individual test result data
 */
public class TestResult {
    private String scenarioName;
    private String status;
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String errorMessage;
    private String featureName;
    private String screenshotBase64;  // Base64 encoded screenshot for failed tests

    public TestResult(String scenarioName, String status, Duration duration, 
                      LocalDateTime startTime, LocalDateTime endTime, String errorMessage, String featureName) {
        this.scenarioName = scenarioName;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
        this.errorMessage = errorMessage;
        this.featureName = featureName;
        this.screenshotBase64 = null;
    }

    // Getters
    public String getScenarioName() { return scenarioName; }
    public String getStatus() { return status; }
    public Duration getDuration() { return duration; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public String getErrorMessage() { return errorMessage; }
    public String getFeatureName() { return featureName; }
    public String getScreenshotBase64() { return screenshotBase64; }

    // Setter for screenshot
    public void setScreenshotBase64(String screenshotBase64) {
        this.screenshotBase64 = screenshotBase64;
    }

    public boolean hasScreenshot() {
        return screenshotBase64 != null && !screenshotBase64.isEmpty();
    }

    public String getDurationFormatted() {
        long seconds = duration.getSeconds();
        long millis = duration.toMillis() % 1000;
        return String.format("%d.%03d s", seconds, millis);
    }
}



