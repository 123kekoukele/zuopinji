package com.hadluo.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Excel 导入通用工具：单元格读取、表头列定位。
 */
public final class ExcelImportUtil {

    private static final DataFormatter DATA_FORMATTER = new DataFormatter();

    private ExcelImportUtil() {
    }

    public static String getCellString(Cell cell) {
        if (cell == null) {
            return "";
        }
        try {
            String value = DATA_FORMATTER.formatCellValue(cell);
            return value == null ? "" : value.trim();
        } catch (Exception ignore) {
        }
        CellType type = cell.getCellType();
        if (type == CellType.STRING) {
            return StringUtils.trimToEmpty(cell.getStringCellValue());
        }
        if (type == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                try {
                    return cell.getDateCellValue() != null ? cell.getDateCellValue().toString() : "";
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            }
            double n = cell.getNumericCellValue();
            return (long) n == n ? String.valueOf((long) n) : String.valueOf(n);
        }
        if (type == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        }
        if (type == CellType.FORMULA) {
            try {
                return StringUtils.trimToEmpty(cell.getStringCellValue());
            } catch (Exception ignore) {
            }
            try {
                return String.valueOf(cell.getNumericCellValue());
            } catch (Exception e) {
                return StringUtils.trimToEmpty(cell.getCellFormula());
            }
        }
        return "";
    }

    public static String getCellString(Sheet sheet, Row row, int columnIndex) {
        if (row == null || columnIndex < 0) {
            return "";
        }
        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == CellType.FORMULA && sheet != null && sheet.getWorkbook() != null) {
            try {
                FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
                String value = DATA_FORMATTER.formatCellValue(cell, evaluator);
                return value == null ? "" : value.trim();
            } catch (Exception ignore) {
            }
        }
        return getCellString(cell);
    }

    public static String normalizeHeader(String title) {
        if (title == null) {
            return "";
        }
        return title.replace('\u00A0', ' ').replace('\uFEFF', ' ').trim();
    }

    /**
     * 在前若干行中扫描指定表头，返回 [headerRowIndex, columnIndex]；未找到返回 null。
     */
    public static int[] locateColumn(Sheet sheet, String headerTitle, int maxScanRows) {
        if (sheet == null || StringUtils.isBlank(headerTitle)) {
            return null;
        }
        String target = headerTitle.trim();
        int scanLimit = Math.max(1, maxScanRows);
        int lastRow = Math.min(sheet.getLastRowNum(), scanLimit - 1);
        for (int rowIndex = 0; rowIndex <= lastRow; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            int lastCellNum = row.getLastCellNum();
            for (int colIndex = 0; colIndex < lastCellNum; colIndex++) {
                String title = normalizeHeader(getCellString(row.getCell(colIndex)));
                if (target.equals(title)) {
                    return new int[] { rowIndex, colIndex };
                }
            }
        }
        return null;
    }
}
