package com.automation.utilities;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * ExcelReader - Utility class for reading test data from Excel files
 * Supports .xlsx format using Apache POI
 * 
 * @author Vishal Lodhiya
 * @version 1.0
 */
public class ExcelReader {

    private static final String TEST_DATA_PATH = "src/test/resources/testdata/";
    
    /**
     * Read employee data from Excel file
     * Reads the first data row (row index 1) after headers
     * 
     * @param fileName Name of the Excel file (e.g., "Test Data.xlsx")
     * @param sheetName Name of the sheet to read from
     * @return Map containing column headers as keys and cell values as values
     */
    public static Map<String, String> getEmployeeData(String fileName, String sheetName) {
        return getRowData(fileName, sheetName, 1); // Row 1 is first data row (0 is header)
    }

    /**
     * Read employee data from default file and sheet
     * Uses "Test Data.xlsx" and first sheet
     * 
     * @return Map containing employee data
     */
    public static Map<String, String> getEmployeeData() {
        return getEmployeeData("Test Data.xlsx", null);
    }

    /**
     * Read a specific row from Excel file
     * 
     * @param fileName Name of the Excel file
     * @param sheetName Name of the sheet (null for first sheet)
     * @param rowIndex Row index to read (0-based, 0 is header)
     * @return Map containing column headers as keys and cell values as values
     */
    public static Map<String, String> getRowData(String fileName, String sheetName, int rowIndex) {
        Map<String, String> data = new HashMap<>();
        String filePath = TEST_DATA_PATH + fileName;
        
        System.out.println("[INFO] Reading Excel file: " + filePath);
        
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            // Get sheet - either by name or first sheet
            Sheet sheet;
            if (sheetName != null && !sheetName.isEmpty()) {
                sheet = workbook.getSheet(sheetName);
                if (sheet == null) {
                    throw new RuntimeException("[ERROR] Sheet not found: " + sheetName);
                }
            } else {
                sheet = workbook.getSheetAt(0);
            }
            
            System.out.println("[INFO] Reading from sheet: " + sheet.getSheetName());
            
            // Get header row (first row)
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("[ERROR] Header row is empty");
            }
            
            // Get data row
            Row dataRow = sheet.getRow(rowIndex);
            if (dataRow == null) {
                throw new RuntimeException("[ERROR] Data row " + rowIndex + " is empty");
            }
            
            // Read each cell and map header -> value
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell headerCell = headerRow.getCell(i);
                Cell dataCell = dataRow.getCell(i);
                
                if (headerCell != null) {
                    String header = getCellValueAsString(headerCell).trim();
                    String value = dataCell != null ? getCellValueAsString(dataCell).trim() : "";
                    
                    if (!header.isEmpty()) {
                        data.put(header, value);
                        System.out.println("   " + header + ": " + value);
                    }
                }
            }
            
            System.out.println("[INFO] Successfully read " + data.size() + " fields from Excel");
            
        } catch (IOException e) {
            System.out.println("[ERROR] Error reading Excel file: " + e.getMessage());
            throw new RuntimeException("Failed to read Excel file: " + filePath, e);
        }
        
        return data;
    }

    /**
     * Get cell value as String regardless of cell type
     * 
     * @param cell The cell to read
     * @return String representation of cell value
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
                
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Format date as dd-MM-yyyy
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
                    return sdf.format(cell.getDateCellValue());
                } else {
                    // Check if it's a whole number (like Employee ID)
                    double numValue = cell.getNumericCellValue();
                    if (numValue == Math.floor(numValue)) {
                        return String.valueOf((long) numValue);
                    }
                    return String.valueOf(numValue);
                }
                
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
                
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
                
            case BLANK:
                return "";
                
            default:
                return "";
        }
    }

    /**
     * Get specific field value from Excel
     * 
     * @param fileName Excel file name
     * @param fieldName Column header name
     * @return The value for that field
     */
    public static String getFieldValue(String fileName, String fieldName) {
        Map<String, String> data = getEmployeeData(fileName, null);
        return data.getOrDefault(fieldName, "");
    }

    /**
     * Get specific field value from default Excel file
     * 
     * @param fieldName Column header name
     * @return The value for that field
     */
    public static String getFieldValue(String fieldName) {
        return getFieldValue("Test Data.xlsx", fieldName);
    }

    /**
     * Get row count from Excel sheet (excluding header)
     * 
     * @param fileName Excel file name
     * @param sheetName Sheet name (null for first sheet)
     * @return Number of data rows
     */
    public static int getRowCount(String fileName, String sheetName) {
        String filePath = TEST_DATA_PATH + fileName;
        
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = sheetName != null ? workbook.getSheet(sheetName) : workbook.getSheetAt(0);
            int rowCount = sheet.getLastRowNum(); // Excludes header (0-indexed)
            System.out.println("[INFO] Row count in " + fileName + ": " + rowCount);
            return rowCount;
            
        } catch (IOException e) {
            System.out.println("[ERROR] Error getting row count: " + e.getMessage());
            return 0;
        }
    }
}

