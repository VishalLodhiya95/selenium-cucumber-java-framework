package com.automation.reports;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.automation.utilities.ConfigReader;

/**
 * HtmlReportGenerator - Generates professional HTML test reports with screenshots
 * Compatible with Java 11+
 */
public class HtmlReportGenerator {

    private static final String REPORT_PATH = "target/reports/";
    
    // ========================================
    // EXECUTED BY INFORMATION
    // ========================================
    private static final String EXECUTED_BY_NAME = "Vishal Lodhiya";
    private static final String EXECUTED_BY_TITLE = "QA ISTQB Certified";

    /**
     * Generate HTML report from test results
     */
    public static String generateReport(List<TestResult> results, String browserName) {
        try {
            // Create reports directory if it doesn't exist
            Path path = Paths.get(REPORT_PATH);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            // Generate filename with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "TestReport_" + timestamp + ".html";
            String filePath = REPORT_PATH + fileName;

            // Calculate stats
            int total = results.size();
            int passed = (int) results.stream().filter(r -> "PASSED".equalsIgnoreCase(r.getStatus())).count();
            int failed = (int) results.stream().filter(r -> "FAILED".equalsIgnoreCase(r.getStatus())).count();
            int skipped = total - passed - failed;
            double passRate = total > 0 ? (double) passed / total * 100 : 0;

            // Build HTML content
            StringBuilder html = new StringBuilder();
            html.append(getHtmlHeader());
            html.append(getHtmlBody(results, browserName, total, passed, failed, skipped, passRate));
            html.append(getHtmlFooter());

            // Write to file
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(html.toString());
            }

            System.out.println("HTML Report generated: " + filePath);
            return filePath;

        } catch (IOException e) {
            System.out.println("Failed to generate HTML report: " + e.getMessage());
            return null;
        }
    }

    private static String getHtmlHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"en\">\n");
        sb.append("<head>\n");
        sb.append("    <meta charset=\"UTF-8\">\n");
        sb.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        sb.append("    <title>Test Automation Report</title>\n");
        sb.append("    <style>\n");
        
        // CSS Reset & Base
        sb.append("        * { margin: 0; padding: 0; box-sizing: border-box; }\n");
        sb.append("        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif; ");
        sb.append("background: #f0f2f5; min-height: 100vh; padding: 20px; color: #333; }\n");
        
        // Container
        sb.append("        .container { max-width: 1400px; margin: 0 auto; }\n");
        
        // Header Card
        sb.append("        .header-card { background: linear-gradient(135deg, #2c3e50 0%, #3498db 100%); ");
        sb.append("border-radius: 12px; padding: 30px; margin-bottom: 20px; color: white; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }\n");
        sb.append("        .header-card h1 { font-size: 2em; margin-bottom: 8px; }\n");
        sb.append("        .header-card .subtitle { opacity: 0.9; font-size: 1.1em; }\n");
        sb.append("        .executed-by-badge { display: inline-block; background: rgba(255,255,255,0.2); ");
        sb.append("padding: 8px 16px; border-radius: 20px; margin-top: 15px; font-size: 0.95em; }\n");
        sb.append("        .executed-by-badge strong { color: #f1c40f; }\n");
        
        // Cards Grid
        sb.append("        .cards-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); ");
        sb.append("gap: 20px; margin-bottom: 20px; }\n");
        
        // Info Card
        sb.append("        .card { background: white; border-radius: 12px; padding: 24px; ");
        sb.append("box-shadow: 0 2px 8px rgba(0,0,0,0.08); }\n");
        sb.append("        .card-title { font-size: 0.85em; color: #7f8c8d; text-transform: uppercase; ");
        sb.append("letter-spacing: 0.5px; margin-bottom: 8px; }\n");
        sb.append("        .card-value { font-size: 1.3em; font-weight: 600; color: #2c3e50; ");
        sb.append("word-break: break-all; overflow-wrap: break-word; }\n");
        
        // Summary Cards
        sb.append("        .summary-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 20px; }\n");
        sb.append("        @media (max-width: 768px) { .summary-grid { grid-template-columns: repeat(2, 1fr); } }\n");
        sb.append("        .summary-card { background: white; border-radius: 12px; padding: 24px; text-align: center; ");
        sb.append("box-shadow: 0 2px 8px rgba(0,0,0,0.08); border-top: 4px solid; }\n");
        sb.append("        .summary-card.total { border-top-color: #3498db; }\n");
        sb.append("        .summary-card.passed { border-top-color: #27ae60; }\n");
        sb.append("        .summary-card.failed { border-top-color: #e74c3c; }\n");
        sb.append("        .summary-card.rate { border-top-color: #9b59b6; }\n");
        sb.append("        .summary-number { font-size: 2.5em; font-weight: 700; display: block; margin-bottom: 5px; }\n");
        sb.append("        .summary-card.total .summary-number { color: #3498db; }\n");
        sb.append("        .summary-card.passed .summary-number { color: #27ae60; }\n");
        sb.append("        .summary-card.failed .summary-number { color: #e74c3c; }\n");
        sb.append("        .summary-card.rate .summary-number { color: #9b59b6; }\n");
        sb.append("        .summary-label { font-size: 0.9em; color: #7f8c8d; text-transform: uppercase; letter-spacing: 0.5px; }\n");
        
        // Results Section
        sb.append("        .results-card { background: white; border-radius: 12px; ");
        sb.append("box-shadow: 0 2px 8px rgba(0,0,0,0.08); overflow: hidden; }\n");
        sb.append("        .results-header { background: #2c3e50; color: white; padding: 20px 24px; }\n");
        sb.append("        .results-header h2 { font-size: 1.2em; font-weight: 600; }\n");
        
        // Table
        sb.append("        .results-table { width: 100%; border-collapse: collapse; }\n");
        sb.append("        .results-table th { background: #f8f9fa; padding: 14px 20px; text-align: left; ");
        sb.append("font-weight: 600; color: #2c3e50; border-bottom: 2px solid #e9ecef; font-size: 0.9em; }\n");
        sb.append("        .results-table td { padding: 16px 20px; border-bottom: 1px solid #f0f0f0; vertical-align: top; }\n");
        sb.append("        .results-table tr:hover { background: #f8f9fa; }\n");
        sb.append("        .results-table tr:last-child td { border-bottom: none; }\n");
        
        // Status Badge
        sb.append("        .status-badge { display: inline-flex; align-items: center; padding: 6px 14px; ");
        sb.append("border-radius: 6px; font-weight: 600; font-size: 0.85em; }\n");
        sb.append("        .status-badge.passed { background: #d5f5e3; color: #1e8449; }\n");
        sb.append("        .status-badge.failed { background: #fadbd8; color: #c0392b; }\n");
        sb.append("        .status-icon { margin-right: 6px; }\n");
        
        // Scenario Details (for failures)
        sb.append("        .scenario-name { font-weight: 500; color: #2c3e50; }\n");
        sb.append("        .error-section { margin-top: 12px; padding: 12px; background: #fef9e7; ");
        sb.append("border-radius: 6px; border-left: 3px solid #f39c12; }\n");
        sb.append("        .error-label { font-size: 0.75em; color: #7f8c8d; text-transform: uppercase; margin-bottom: 4px; }\n");
        sb.append("        .error-message { font-size: 0.9em; color: #7f8c8d; font-family: monospace; word-break: break-all; }\n");
        
        // Screenshot Section
        sb.append("        .screenshot-section { margin-top: 12px; }\n");
        sb.append("        .screenshot-toggle { background: #e74c3c; color: white; border: none; padding: 8px 16px; ");
        sb.append("border-radius: 6px; cursor: pointer; font-size: 0.85em; font-weight: 500; }\n");
        sb.append("        .screenshot-toggle:hover { background: #c0392b; }\n");
        sb.append("        .screenshot-container { margin-top: 12px; display: none; }\n");
        sb.append("        .screenshot-container.show { display: block; }\n");
        sb.append("        .screenshot-img { max-width: 100%; border-radius: 8px; border: 2px solid #e9ecef; ");
        sb.append("box-shadow: 0 4px 12px rgba(0,0,0,0.1); }\n");
        
        // Footer
        sb.append("        .footer { text-align: center; padding: 20px; color: #7f8c8d; font-size: 0.9em; margin-top: 20px; }\n");
        sb.append("        .footer strong { color: #2c3e50; }\n");
        
        sb.append("    </style>\n");
        sb.append("</head>\n");
        return sb.toString();
    }

    private static String getHtmlBody(List<TestResult> results, String browserName, 
            int total, int passed, int failed, int skipped, double passRate) {
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String baseUrl = ConfigReader.getBaseUrl();
        String environment = detectEnvironment(baseUrl);
        String totalDuration = getTotalExecutionDuration();
        
        StringBuilder body = new StringBuilder();
        body.append("<body>\n");
        body.append("<div class=\"container\">\n");
        
        // Header Card
        body.append("<div class=\"header-card\">\n");
        body.append("    <h1>Test Automation Report</h1>\n");
        body.append("    <p class=\"subtitle\">Selenium Cucumber Framework - Java</p>\n");
        body.append("    <div class=\"executed-by-badge\">Executed By: <strong>")
            .append(EXECUTED_BY_NAME).append("</strong> (").append(EXECUTED_BY_TITLE).append(")</div>\n");
        body.append("</div>\n");
        
        // Info Cards Grid
        body.append("<div class=\"cards-grid\">\n");
        body.append(createInfoCard("Browser", browserName));
        body.append(createInfoCard("Execution Date", timestamp));
        body.append(createInfoCard("Environment", environment));
        body.append(createInfoCard("Base URL", baseUrl));
        body.append(createInfoCard("Total Duration", totalDuration));
        body.append("</div>\n");
        
        // Summary Grid
        body.append("<div class=\"summary-grid\">\n");
        body.append(createSummaryCard("total", String.valueOf(total), "Total Tests"));
        body.append(createSummaryCard("passed", String.valueOf(passed), "Passed"));
        body.append(createSummaryCard("failed", String.valueOf(failed), "Failed"));
        body.append(createSummaryCard("rate", String.format("%.1f%%", passRate), "Pass Rate"));
        body.append("</div>\n");
        
        // Results Table
        body.append("<div class=\"results-card\">\n");
        body.append("<div class=\"results-header\"><h2>Test Results Details</h2></div>\n");
        body.append("<table class=\"results-table\">\n");
        body.append("<thead>\n");
        body.append("    <tr>\n");
        body.append("        <th style=\"width:50px\">#</th>\n");
        body.append("        <th>Scenario</th>\n");
        body.append("        <th style=\"width:120px\">Status</th>\n");
        body.append("        <th style=\"width:100px\">Duration</th>\n");
        body.append("        <th style=\"width:150px\">Feature</th>\n");
        body.append("    </tr>\n");
        body.append("</thead>\n");
        body.append("<tbody>\n");
        
        int index = 1;
        for (TestResult result : results) {
            boolean isFailed = "FAILED".equalsIgnoreCase(result.getStatus());
            String statusClass = isFailed ? "failed" : "passed";
            String statusIcon = isFailed ? "✖" : "✔";
            String screenshotId = "screenshot-" + index;
            
            body.append("<tr>\n");
            body.append("    <td>").append(index).append("</td>\n");
            
            // Scenario column with error/screenshot for failures
            body.append("    <td>\n");
            body.append("        <div class=\"scenario-name\">").append(result.getScenarioName()).append("</div>\n");
            
            // If failed, show error and screenshot
            if (isFailed) {
                // Error message if available
                if (result.getErrorMessage() != null && !result.getErrorMessage().isEmpty()) {
                    body.append("        <div class=\"error-section\">\n");
                    body.append("            <div class=\"error-label\">Error Message</div>\n");
                    body.append("            <div class=\"error-message\">").append(escapeHtml(result.getErrorMessage())).append("</div>\n");
                    body.append("        </div>\n");
                }
                
                // Screenshot if available
                if (result.hasScreenshot()) {
                    body.append("        <div class=\"screenshot-section\">\n");
                    body.append("            <button class=\"screenshot-toggle\" onclick=\"toggleScreenshot('")
                        .append(screenshotId).append("')\">View Screenshot</button>\n");
                    body.append("            <div id=\"").append(screenshotId).append("\" class=\"screenshot-container\">\n");
                    body.append("                <img class=\"screenshot-img\" src=\"data:image/png;base64,")
                        .append(result.getScreenshotBase64()).append("\" alt=\"Failed Screenshot\">\n");
                    body.append("            </div>\n");
                    body.append("        </div>\n");
                }
            }
            
            body.append("    </td>\n");
            
            // Status
            body.append("    <td><span class=\"status-badge ").append(statusClass).append("\">")
                .append("<span class=\"status-icon\">").append(statusIcon).append("</span>")
                .append(result.getStatus()).append("</span></td>\n");
            
            // Duration
            body.append("    <td>").append(result.getDurationFormatted()).append("</td>\n");
            
            // Feature
            body.append("    <td>").append(result.getFeatureName() != null ? result.getFeatureName() : "-").append("</td>\n");
            body.append("</tr>\n");
            
            index++;
        }
        
        body.append("</tbody>\n");
        body.append("</table>\n");
        body.append("</div>\n"); // results-card
        
        // Footer
        body.append("<div class=\"footer\">\n");
        body.append("    <p>Generated on ").append(timestamp).append(" | Selenium Cucumber Framework</p>\n");
        body.append("    <p>Executed By: <strong>").append(EXECUTED_BY_NAME).append("</strong> (")
            .append(EXECUTED_BY_TITLE).append(")</p>\n");
        body.append("</div>\n");
        
        body.append("</div>\n"); // container
        
        // JavaScript for screenshot toggle
        body.append("<script>\n");
        body.append("function toggleScreenshot(id) {\n");
        body.append("    var container = document.getElementById(id);\n");
        body.append("    if (container.classList.contains('show')) {\n");
        body.append("        container.classList.remove('show');\n");
        body.append("    } else {\n");
        body.append("        container.classList.add('show');\n");
        body.append("    }\n");
        body.append("}\n");
        body.append("</script>\n");
        
        body.append("</body>\n");
        
        return body.toString();
    }

    private static String createInfoCard(String label, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"card\">\n");
        sb.append("    <div class=\"card-title\">").append(label).append("</div>\n");
        sb.append("    <div class=\"card-value\">").append(value != null ? value : "-").append("</div>\n");
        sb.append("</div>\n");
        return sb.toString();
    }

    private static String createSummaryCard(String type, String number, String label) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"summary-card ").append(type).append("\">\n");
        sb.append("    <span class=\"summary-number\">").append(number).append("</span>\n");
        sb.append("    <span class=\"summary-label\">").append(label).append("</span>\n");
        sb.append("</div>\n");
        return sb.toString();
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }

    private static String getHtmlFooter() {
        return "</html>";
    }

    /**
     * Detect environment based on URL
     * - Contains "qa." → QA
     * - Contains "uat." → UAT
     * - Otherwise → Production
     */
    private static String detectEnvironment(String url) {
        if (url == null || url.isEmpty()) {
            return "Unknown";
        }
        String lowerUrl = url.toLowerCase();
        if (lowerUrl.contains("qa.")) {
            return "QA";
        } else if (lowerUrl.contains("uat.")) {
            return "UAT";
        } else {
            return "Production";
        }
    }

    /**
     * Calculate total execution duration
     */
    private static String getTotalExecutionDuration() {
        LocalDateTime startTime = TestResultCollector.getTestRunStartTime();
        LocalDateTime endTime = TestResultCollector.getTestRunEndTime();
        
        if (startTime == null || endTime == null) {
            return "N/A";
        }
        
        Duration duration = Duration.between(startTime, endTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        
        if (hours > 0) {
            return String.format("%d hr %d min %d sec", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%d min %d sec", minutes, seconds);
        } else {
            return String.format("%d sec", seconds);
        }
    }
}



