package com.automation.reports;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
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
 * PdfReportGenerator - Generates PDF test reports
 */
public class PdfReportGenerator {

    private static final String REPORT_PATH = "target/reports/";
    private static final DeviceRgb GREEN = new DeviceRgb(40, 167, 69);
    private static final DeviceRgb RED = new DeviceRgb(220, 53, 69);
    private static final DeviceRgb BLUE = new DeviceRgb(0, 123, 255);
    private static final DeviceRgb GRAY = new DeviceRgb(108, 117, 125);
    private static final DeviceRgb GOLD = new DeviceRgb(218, 165, 32);
    
    // ========================================
    // EXECUTED BY INFORMATION
    // ========================================
    private static final String EXECUTED_BY_NAME = "Vishal Lodhiya";
    private static final String EXECUTED_BY_TITLE = "QA ISTQB Certified";

    /**
     * Generate PDF report from test results
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
            String fileName = "TestReport_" + timestamp + ".pdf";
            String filePath = REPORT_PATH + fileName;

            // Create PDF
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add title
            Paragraph title = new Paragraph("Test Automation Report")
                    .setFontSize(24)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(BLUE);
            document.add(title);

            // Add subtitle
            Paragraph subtitle = new Paragraph("Selenium Cucumber Framework - Java")
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(GRAY);
            document.add(subtitle);

            document.add(new Paragraph("\n"));

            // Add execution info
            addExecutionInfo(document, browserName);

            document.add(new Paragraph("\n"));

            // Add summary
            addSummary(document, results);

            document.add(new Paragraph("\n"));

            // Add results table
            addResultsTable(document, results);

            // Close document
            document.close();

            System.out.println("PDF Report generated: " + filePath);
            return filePath;

        } catch (IOException e) {
            System.out.println("Failed to generate PDF report: " + e.getMessage());
            return null;
        }
    }

    private static void addExecutionInfo(Document document, String browserName) {
        Paragraph header = new Paragraph("Execution Information")
                .setFontSize(16)
                .setBold()
                .setFontColor(BLUE);
        document.add(header);

        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                .setWidth(UnitValue.createPercentValue(100));

        // Add Executed By with special styling
        infoTable.addCell(new Cell()
                .add(new Paragraph("Executed By").setBold())
                .setBackgroundColor(new DeviceRgb(240, 240, 240)));
        infoTable.addCell(new Cell()
                .add(new Paragraph(EXECUTED_BY_NAME + " (" + EXECUTED_BY_TITLE + ")")
                        .setBold()
                        .setFontColor(GOLD)));
        
        addInfoRow(infoTable, "Browser", browserName);
        addInfoRow(infoTable, "Execution Date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        // Auto-detect environment from URL
        String baseUrl = ConfigReader.getBaseUrl();
        String environment = detectEnvironment(baseUrl);
        addInfoRow(infoTable, "Environment", environment);
        addInfoRow(infoTable, "Base URL", baseUrl);
        
        // Add Total Duration
        String totalDuration = getTotalExecutionDuration();
        addInfoRow(infoTable, "Total Duration", totalDuration);

        document.add(infoTable);
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

    private static void addInfoRow(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label).setBold()).setBackgroundColor(new DeviceRgb(240, 240, 240)));
        table.addCell(new Cell().add(new Paragraph(value)));
    }

    private static void addSummary(Document document, List<TestResult> results) {
        Paragraph header = new Paragraph("Test Summary")
                .setFontSize(16)
                .setBold()
                .setFontColor(BLUE);
        document.add(header);

        int total = results.size();
        int passed = (int) results.stream().filter(r -> "PASSED".equalsIgnoreCase(r.getStatus())).count();
        int failed = (int) results.stream().filter(r -> "FAILED".equalsIgnoreCase(r.getStatus())).count();
        int skipped = total - passed - failed;
        double passRate = total > 0 ? (double) passed / total * 100 : 0;

        Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25}))
                .setWidth(UnitValue.createPercentValue(100));

        // Headers
        summaryTable.addHeaderCell(createHeaderCell("Total"));
        summaryTable.addHeaderCell(createHeaderCell("Passed"));
        summaryTable.addHeaderCell(createHeaderCell("Failed"));
        summaryTable.addHeaderCell(createHeaderCell("Pass Rate"));

        // Values
        summaryTable.addCell(createValueCell(String.valueOf(total), BLUE));
        summaryTable.addCell(createValueCell(String.valueOf(passed), GREEN));
        summaryTable.addCell(createValueCell(String.valueOf(failed), RED));
        summaryTable.addCell(createValueCell(String.format("%.1f%%", passRate), passRate >= 80 ? GREEN : RED));

        document.add(summaryTable);
    }

    private static Cell createHeaderCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setBold())
                .setBackgroundColor(BLUE)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private static Cell createValueCell(String text, DeviceRgb color) {
        return new Cell()
                .add(new Paragraph(text).setBold().setFontSize(14))
                .setFontColor(color)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private static void addResultsTable(Document document, List<TestResult> results) {
        Paragraph header = new Paragraph("Test Results Details")
                .setFontSize(16)
                .setBold()
                .setFontColor(BLUE);
        document.add(header);

        Table table = new Table(UnitValue.createPercentArray(new float[]{5, 45, 15, 15, 20}))
                .setWidth(UnitValue.createPercentValue(100));

        // Headers
        table.addHeaderCell(createHeaderCell("#"));
        table.addHeaderCell(createHeaderCell("Scenario Name"));
        table.addHeaderCell(createHeaderCell("Status"));
        table.addHeaderCell(createHeaderCell("Duration"));
        table.addHeaderCell(createHeaderCell("Feature"));

        // Data rows
        int index = 1;
        for (TestResult result : results) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(index++))).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(result.getScenarioName())));
            
            // Status with color
            DeviceRgb statusColor = "PASSED".equalsIgnoreCase(result.getStatus()) ? GREEN : RED;
            String statusIcon = "PASSED".equalsIgnoreCase(result.getStatus()) ? "✓ " : "✗ ";
            table.addCell(new Cell()
                    .add(new Paragraph(statusIcon + result.getStatus()).setBold())
                    .setFontColor(statusColor)
                    .setTextAlignment(TextAlignment.CENTER));
            
            table.addCell(new Cell().add(new Paragraph(result.getDurationFormatted())).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(result.getFeatureName() != null ? result.getFeatureName() : "-")));
        }

        document.add(table);
    }
}



