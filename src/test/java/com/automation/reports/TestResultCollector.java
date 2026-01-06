package com.automation.reports;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TestResultCollector - Collects test results during test execution
 */
public class TestResultCollector {

    private static List<TestResult> results = new ArrayList<>();
    private static LocalDateTime testRunStartTime;
    private static LocalDateTime testRunEndTime;

    /**
     * Set test run start time
     */
    public static void setStartTime() {
        testRunStartTime = LocalDateTime.now();
        results.clear(); // Clear previous results
        System.out.println("Test run started at: " + testRunStartTime);
    }

    /**
     * Set test run end time
     */
    public static void setEndTime() {
        testRunEndTime = LocalDateTime.now();
        System.out.println("Test run ended at: " + testRunEndTime);
    }

    /**
     * Add a test result
     */
    public static void addResult(String scenarioName, String status, Duration duration,
                                  LocalDateTime startTime, LocalDateTime endTime, 
                                  String errorMessage, String featureName) {
        addResult(scenarioName, status, duration, startTime, endTime, errorMessage, featureName, null);
    }

    /**
     * Add a test result with screenshot (for failed tests)
     */
    public static void addResult(String scenarioName, String status, Duration duration,
                                  LocalDateTime startTime, LocalDateTime endTime, 
                                  String errorMessage, String featureName, String screenshotBase64) {
        TestResult result = new TestResult(scenarioName, status, duration, startTime, endTime, errorMessage, featureName);
        if (screenshotBase64 != null && !screenshotBase64.isEmpty()) {
            result.setScreenshotBase64(screenshotBase64);
        }
        results.add(result);
        System.out.println("Result added: " + scenarioName + " - " + status);
    }

    /**
     * Get all results
     */
    public static List<TestResult> getAllResults() {
        return new ArrayList<>(results);
    }

    /**
     * Get passed count
     */
    public static int getPassedCount() {
        return (int) results.stream().filter(r -> "PASSED".equalsIgnoreCase(r.getStatus())).count();
    }

    /**
     * Get failed count
     */
    public static int getFailedCount() {
        return (int) results.stream().filter(r -> "FAILED".equalsIgnoreCase(r.getStatus())).count();
    }

    /**
     * Get skipped count
     */
    public static int getSkippedCount() {
        return (int) results.stream().filter(r -> "SKIPPED".equalsIgnoreCase(r.getStatus())).count();
    }

    /**
     * Get total count
     */
    public static int getTotalCount() {
        return results.size();
    }

    /**
     * Get test run start time
     */
    public static LocalDateTime getTestRunStartTime() {
        return testRunStartTime;
    }

    /**
     * Get test run end time
     */
    public static LocalDateTime getTestRunEndTime() {
        return testRunEndTime;
    }

    /**
     * Clear all results
     */
    public static void clear() {
        results.clear();
    }
}



