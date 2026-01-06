package com.automation.utilities;

import java.io.File;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.automation.reports.TestResult;
import com.automation.reports.TestResultCollector;

/**
 * EmailHelper - Sends test execution reports via email
 * Uses Mailtrap HTTP API to bypass firewall restrictions
 */
public class EmailHelper {

    private static final String EXECUTED_BY_NAME = "Vishal Lodhiya";
    private static final String EXECUTED_BY_TITLE = "QA ISTQB Certified";
    private static final String MAILTRAP_API_URL = "https://sandbox.api.mailtrap.io/api/send";

    /**
     * Send email report after test execution
     */
    public static void sendTestReport(String pdfReportPath, String htmlReportPath, String browserName) {
        // Check if email is enabled
        String emailEnabled = ConfigReader.getProperty("email.enabled", "false");
        if (!emailEnabled.equalsIgnoreCase("true")) {
            System.out.println("Email sending is disabled in config.properties");
            return;
        }

        System.out.println("\nStarting email send process (HTTP API)...");

        try {
            String apiToken = ConfigReader.getProperty("email.mailtrap.apiToken");
            String inboxId = ConfigReader.getProperty("email.mailtrap.inboxId");
            String senderEmail = ConfigReader.getProperty("email.sender", "test@automation.com");
            String senderName = ConfigReader.getProperty("email.senderName", "Test Automation");
            String recipients = ConfigReader.getProperty("email.recipients", "test@example.com");

            if (apiToken == null || apiToken.equals("YOUR_API_TOKEN_HERE") || 
                inboxId == null || inboxId.equals("YOUR_INBOX_ID_HERE")) {
                System.out.println("Mailtrap API not configured. Please set apiToken and inboxId in config.properties");
                System.out.println("Instructions:");
                System.out.println("   1. Go to https://mailtrap.io");
                System.out.println("   2. Click profile > Settings > API Tokens");
                System.out.println("   3. Create token and copy it");
                System.out.println("   4. Get Inbox ID from Email Testing > Inboxes URL");
                return;
            }

            // Get test results
            List<TestResult> results = TestResultCollector.getAllResults();
            int passed = (int) results.stream().filter(r -> "PASSED".equalsIgnoreCase(r.getStatus())).count();
            int failed = (int) results.stream().filter(r -> "FAILED".equalsIgnoreCase(r.getStatus())).count();
            int total = results.size();

            // Detect environment
            String baseUrl = ConfigReader.getBaseUrl();
            String environment = detectEnvironment(baseUrl);

            // Create subject
            String status = failed > 0 ? "FAILED" : "PASSED";
            String subject = String.format("[%s] Test Automation Report - %s | %d/%d Passed", 
                environment, status, passed, total);

            // Create HTML body
            String htmlBody = createEmailBody(results, browserName, environment);

            // Build JSON payload
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"from\":{\"email\":\"").append(senderEmail).append("\",\"name\":\"").append(senderName).append("\"},");
            json.append("\"to\":[{\"email\":\"").append(recipients.split(",")[0].trim()).append("\"}],");
            json.append("\"subject\":\"").append(escapeJson(subject)).append("\",");
            json.append("\"html\":\"").append(escapeJson(htmlBody)).append("\",");
            
            // Add attachments
            json.append("\"attachments\":[");
            boolean hasAttachment = false;
            
            if (pdfReportPath != null) {
                File pdfFile = new File(pdfReportPath);
                if (pdfFile.exists()) {
                    byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());
                    String pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes);
                    json.append("{\"content\":\"").append(pdfBase64).append("\",");
                    json.append("\"filename\":\"TestReport.pdf\",");
                    json.append("\"type\":\"application/pdf\"}");
                    hasAttachment = true;
                    System.out.println("Attached: TestReport.pdf");
                }
            }
            
            if (htmlReportPath != null) {
                File htmlFile = new File(htmlReportPath);
                if (htmlFile.exists()) {
                    if (hasAttachment) json.append(",");
                    byte[] htmlBytes = Files.readAllBytes(htmlFile.toPath());
                    String htmlBase64 = Base64.getEncoder().encodeToString(htmlBytes);
                    json.append("{\"content\":\"").append(htmlBase64).append("\",");
                    json.append("\"filename\":\"TestReport.html\",");
                    json.append("\"type\":\"text/html\"}");
                    System.out.println("Attached: TestReport.html");
                }
            }
            
            json.append("]");
            json.append("}");

            // Send API request
            String apiUrl = MAILTRAP_API_URL + "/" + inboxId;
            System.out.println("Sending to Mailtrap API...");
            
            // Bypass SSL certificate validation (for corporate networks)
            disableSSLValidation();
            
            URL url = new URL(apiUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Api-Token", apiToken);
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                System.out.println("Email sent successfully via Mailtrap API!");
                System.out.println("Check your inbox at: https://mailtrap.io/inboxes");
            } else {
                System.out.println("Failed to send email. Response code: " + responseCode);
                // Read error response
                java.io.InputStream errorStream = conn.getErrorStream();
                if (errorStream != null) {
                    String error = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                    System.out.println("Error: " + error);
                }
            }

            conn.disconnect();

        } catch (Exception e) {
            System.out.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Disable SSL certificate validation (for corporate networks with proxy)
     */
    private static void disableSSLValidation() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                }
            };
            
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            System.out.println("Could not disable SSL validation: " + e.getMessage());
        }
    }

    /**
     * Escape special characters for JSON
     */
    private static String escapeJson(String text) {
        if (text == null) return "";
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    /**
     * Create HTML email body
     */
    private static String createEmailBody(List<TestResult> results, String browserName, String environment) {
        int total = results.size();
        int passed = (int) results.stream().filter(r -> "PASSED".equalsIgnoreCase(r.getStatus())).count();
        int failed = (int) results.stream().filter(r -> "FAILED".equalsIgnoreCase(r.getStatus())).count();
        double passRate = total > 0 ? (double) passed / total * 100 : 0;
        String totalDuration = getTotalExecutionDuration();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><style>");
        html.append("body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background: #f5f5f5; }");
        html.append(".container { max-width: 600px; margin: 0 auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        html.append(".header { background: linear-gradient(135deg, #2c3e50, #3498db); color: white; padding: 25px; text-align: center; }");
        html.append(".header h1 { margin: 0 0 10px 0; font-size: 24px; }");
        html.append(".executed-by { background: #f39c12; color: white; padding: 10px; text-align: center; font-weight: bold; }");
        html.append(".content { padding: 25px; }");
        html.append(".summary { margin-bottom: 25px; }");
        html.append(".stat { display: inline-block; text-align: center; padding: 15px; border-radius: 8px; min-width: 80px; margin: 5px; }");
        html.append(".stat.total { background: #3498db; color: white; }");
        html.append(".stat.passed { background: #27ae60; color: white; }");
        html.append(".stat.failed { background: #e74c3c; color: white; }");
        html.append(".stat .number { font-size: 28px; font-weight: bold; display: block; }");
        html.append(".stat .label { font-size: 12px; text-transform: uppercase; }");
        html.append(".info-table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }");
        html.append(".info-table td { padding: 10px; border-bottom: 1px solid #eee; }");
        html.append(".info-table td:first-child { font-weight: bold; color: #666; width: 40%; }");
        html.append(".results-table { width: 100%; border-collapse: collapse; }");
        html.append(".results-table th { background: #2c3e50; color: white; padding: 12px; text-align: left; }");
        html.append(".results-table td { padding: 10px; border-bottom: 1px solid #eee; }");
        html.append(".status-pass { color: #27ae60; font-weight: bold; }");
        html.append(".status-fail { color: #e74c3c; font-weight: bold; }");
        html.append(".footer { background: #f8f9fa; padding: 15px; text-align: center; color: #666; font-size: 12px; }");
        html.append("</style></head><body>");
        
        html.append("<div class='container'>");
        
        // Header
        html.append("<div class='header'>");
        html.append("<h1>Test Automation Report</h1>");
        html.append("<p>Selenium Cucumber Framework - Java</p>");
        html.append("</div>");
        
        // Executed By
        html.append("<div class='executed-by'>");
        html.append("Executed By: ").append(EXECUTED_BY_NAME).append(" (").append(EXECUTED_BY_TITLE).append(")");
        html.append("</div>");
        
        html.append("<div class='content'>");
        
        // Summary Stats
        html.append("<div class='summary'>");
        html.append("<div class='stat total'><span class='number'>").append(total).append("</span><span class='label'>Total</span></div>");
        html.append("<div class='stat passed'><span class='number'>").append(passed).append("</span><span class='label'>Passed</span></div>");
        html.append("<div class='stat failed'><span class='number'>").append(failed).append("</span><span class='label'>Failed</span></div>");
        html.append("</div>");
        
        // Info Table
        html.append("<table class='info-table'>");
        html.append("<tr><td>Environment</td><td>").append(environment).append("</td></tr>");
        html.append("<tr><td>Browser</td><td>").append(browserName).append("</td></tr>");
        html.append("<tr><td>Execution Date</td><td>").append(timestamp).append("</td></tr>");
        html.append("<tr><td>Total Duration</td><td>").append(totalDuration).append("</td></tr>");
        html.append("<tr><td>Pass Rate</td><td>").append(String.format("%.1f%%", passRate)).append("</td></tr>");
        html.append("</table>");
        
        // Results Table
        html.append("<table class='results-table'>");
        html.append("<tr><th>#</th><th>Scenario</th><th>Status</th><th>Duration</th></tr>");
        
        int index = 1;
        for (TestResult result : results) {
            String statusClass = "PASSED".equalsIgnoreCase(result.getStatus()) ? "status-pass" : "status-fail";
            String statusText = "PASSED".equalsIgnoreCase(result.getStatus()) ? "PASSED" : "FAILED";
            
            html.append("<tr>");
            html.append("<td>").append(index++).append("</td>");
            html.append("<td>").append(result.getScenarioName()).append("</td>");
            html.append("<td class='").append(statusClass).append("'>").append(statusText).append("</td>");
            html.append("<td>").append(result.getDurationFormatted()).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        
        html.append("</div>"); // content
        
        // Footer
        html.append("<div class='footer'>");
        html.append("<p>This is an automated email from Test Automation Framework</p>");
        html.append("<p>Detailed reports attached</p>");
        html.append("</div>");
        
        html.append("</div>"); // container
        html.append("</body></html>");
        
        return html.toString();
    }

    /**
     * Detect environment from URL
     */
    private static String detectEnvironment(String url) {
        if (url == null || url.isEmpty()) return "Unknown";
        String lowerUrl = url.toLowerCase();
        if (lowerUrl.contains("qa.")) return "QA";
        if (lowerUrl.contains("uat.")) return "UAT";
        return "Production";
    }

    /**
     * Calculate total execution duration
     */
    private static String getTotalExecutionDuration() {
        LocalDateTime startTime = TestResultCollector.getTestRunStartTime();
        LocalDateTime endTime = TestResultCollector.getTestRunEndTime();
        
        if (startTime == null || endTime == null) return "N/A";
        
        Duration duration = Duration.between(startTime, endTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        
        if (hours > 0) return String.format("%d hr %d min %d sec", hours, minutes, seconds);
        if (minutes > 0) return String.format("%d min %d sec", minutes, seconds);
        return String.format("%d sec", seconds);
    }
}



