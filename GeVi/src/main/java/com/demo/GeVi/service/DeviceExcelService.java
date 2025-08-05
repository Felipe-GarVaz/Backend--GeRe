package com.demo.GeVi.service;

import com.demo.GeVi.model.Device;
import com.demo.GeVi.model.Device.DeviceStatus;
import com.demo.GeVi.model.DeviceReport;
import com.demo.GeVi.model.DeviceType;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DeviceExcelService {

    public static ByteArrayInputStream exportToExcel(List<Device> devices, List<DeviceReport> reports)
            throws IOException {

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // ======================== HOJA 1: DISPOSITIVOS ========================
            Sheet sheet = workbook.createSheet("Dispositivos");

            // Estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle greenStyle = createSolidStyle(workbook, IndexedColors.BRIGHT_GREEN);
            CellStyle redStyle = createSolidStyle(workbook, IndexedColors.RED);
            CellStyle summaryStyle = createSummaryStyle(workbook);

            int tableStartRow = 7;
            int dataStartRow = tableStartRow + 1;

            // Título resumen
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Resumen de dispositivos registrados");
            titleCell.setCellStyle(headerStyle);

            // Encabezado tabla
            String[] headers = { "Centro de Trabajo", "Número de Serie", "Tipo de Dispositivo", "Estado" };
            Row headerRow = sheet.createRow(tableStartRow);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            int rowIdx = dataStartRow;
            for (Device d : devices) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(d.getWorkCenter().getName());
                row.createCell(1).setCellValue(d.getSerialNumber());
                row.createCell(2).setCellValue(d.getDeviceType().name());

                Cell statusCell = row.createCell(3);
                statusCell.setCellValue(d.getStatus().name());
                statusCell.setCellStyle(d.getStatus() == DeviceStatus.DEFECTUOSO ? redStyle : greenStyle);
            }

            int dataEndRow = rowIdx;

            // Fórmulas resumen
            String colRef = "C" + (dataStartRow + 1) + ":C" + dataEndRow;
            String[][] summary = {
                    { "TOTAL:", "SUBTOTAL(103, C" + (dataStartRow + 1) + ":C" + dataEndRow + ")" },
                    { "TP NEWLAND:", "SUMPRODUCT(SUBTOTAL(103,OFFSET(" + colRef + ",ROW(" + colRef + ")-ROW(C"
                            + (dataStartRow + 1) + "),0,1)),--(" + colRef + "=\"TP_NEWLAND\"))" },
                    { "LECTOR NEWLAND:", "SUMPRODUCT(SUBTOTAL(103,OFFSET(" + colRef + ",ROW(" + colRef + ")-ROW(C"
                            + (dataStartRow + 1) + "),0,1)),--(" + colRef + "=\"LECTOR_NEWLAND\"))" },
                    { "TP DOLPHIN 9900:", "SUMPRODUCT(SUBTOTAL(103,OFFSET(" + colRef + ",ROW(" + colRef + ")-ROW(C"
                            + (dataStartRow + 1) + "),0,1)),--(" + colRef + "=\"TP_DOLPHIN_9900\"))" },
                    { "LECTOR DOLPHIN 9900:", "SUMPRODUCT(SUBTOTAL(103,OFFSET(" + colRef + ",ROW(" + colRef
                            + ")-ROW(C" + (dataStartRow + 1) + "),0,1)),--(" + colRef + "=\"LECTOR_DOLPHIN_9900\"))" }
            };

            for (int i = 0; i < summary.length; i++) {
                Row row = sheet.createRow(1 + i);
                Cell label = row.createCell(0);
                label.setCellValue(summary[i][0]);
                label.setCellStyle(summaryStyle);

                Cell formula = row.createCell(1);
                formula.setCellFormula(summary[i][1]);
                formula.setCellStyle(summaryStyle);
            }

            // Filtros y tamaño automático
            sheet.setAutoFilter(new CellRangeAddress(tableStartRow, dataEndRow - 1, 0, headers.length - 1));
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // ======================== HOJA 2: DEFECTUOSOS ========================
            createDefectSheet(workbook, reports, devices);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private static void createDefectSheet(Workbook workbook, List<DeviceReport> reports, List<Device> devices) {
        Sheet sheet = workbook.createSheet("Defectuosos");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle summaryStyle = createSummaryStyle(workbook);

        // Titulo resumen
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Resumen de dispositivos defectuosos");
        titleCell.setCellStyle(headerStyle);

        // Encabezados
        int tableStartRow = 7;
        int dataStartRow = tableStartRow + 1;

        String[] headers = {
                "Centro de Trabajo", "Número de Serie", "Tipo de Dispositivo",
                "Falla", "Fecha de Reporte", "Tiempo Transcurrido"
        };
        Row headerRow = sheet.createRow(tableStartRow);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Datos
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        int rowIdx = dataStartRow;
        for (DeviceReport r : reports) {
            Row row = sheet.createRow(rowIdx++);

            row.createCell(0).setCellValue(r.getWorkCenter().getName());

            String serial = findSerialByTypeAndCenter(devices, r.getDevice(), r.getWorkCenter().getId());
            row.createCell(1).setCellValue(serial != null ? serial : "N/A");

            row.createCell(2).setCellValue(r.getDevice().name());

            String fail = (r.getFailTypeDevice() != null) ? r.getFailTypeDevice().getName()
                    : r.getPersonalizedFailure();
            row.createCell(3).setCellValue(fail != null ? fail : "N/A");

            row.createCell(4).setCellValue(r.getReportingDate().format(formatter));
            row.createCell(5).setCellValue(formatElapsed(Duration.between(r.getReportingDate(), LocalDateTime.now())));
        }

        int dataEndRow = rowIdx;
        String colRef = "C" + (dataStartRow + 1) + ":C" + dataEndRow;

        // Resumen
        String[][] summary = {
                { "TOTAL EN DEFECTUOSOS:", "SUBTOTAL(103, C" + (dataStartRow + 1) + ":C" + dataEndRow + ")" },
                { "TP NEWLAND:", "SUMPRODUCT(SUBTOTAL(103,OFFSET(" + colRef + ",ROW(" + colRef + ")-ROW(C"
                        + (dataStartRow + 1) + "),0,1)),--(" + colRef + "=\"TP_NEWLAND\"))" },
                { "LECTOR NEWLAND:", "SUMPRODUCT(SUBTOTAL(103,OFFSET(" + colRef + ",ROW(" + colRef + ")-ROW(C"
                        + (dataStartRow + 1) + "),0,1)),--(" + colRef + "=\"LECTOR_NEWLAND\"))" },
                { "TP DOLPHIN 9900:", "SUMPRODUCT(SUBTOTAL(103,OFFSET(" + colRef + ",ROW(" + colRef + ")-ROW(C"
                        + (dataStartRow + 1) + "),0,1)),--(" + colRef + "=\"TP_DOLPHIN_9900\"))" },
                { "LECTOR DOLPHIN 9900:", "SUMPRODUCT(SUBTOTAL(103,OFFSET(" + colRef + ",ROW(" + colRef
                        + ")-ROW(C" + (dataStartRow + 1) + "),0,1)),--(" + colRef + "=\"LECTOR_DOLPHIN_9900\"))" }
        };

        for (int i = 0; i < summary.length; i++) {
            Row row = sheet.createRow(1 + i);
            Cell label = row.createCell(0);
            label.setCellValue(summary[i][0]);
            label.setCellStyle(summaryStyle);

            Cell formula = row.createCell(1);
            formula.setCellFormula(summary[i][1]);
            formula.setCellStyle(summaryStyle);
        }

        // Filtros
        sheet.setAutoFilter(new CellRangeAddress(tableStartRow, dataEndRow - 1, 0, headers.length - 1));
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static String formatElapsed(Duration d) {
        long days = d.toDays();
        long hrs = d.toHoursPart();
        long mins = d.toMinutesPart();
        return (days > 0) ? String.format("%dd %02dh %02dm", days, hrs, mins) : String.format("%02dh %02dm", hrs, mins);
    }

    private static String findSerialByTypeAndCenter(List<Device> devices, DeviceType type, Integer centerId) {
        return devices.stream()
                .filter(d -> d.getDeviceType().equals(type) && d.getWorkCenter().getId().equals(centerId)
                        && d.getStatus() == DeviceStatus.DEFECTUOSO)
                .map(Device::getSerialNumber)
                .findFirst()
                .orElse(null);
    }

    // Estilos
    private static CellStyle createHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private static CellStyle createSolidStyle(Workbook wb, IndexedColors color) {
        CellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(color.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private static CellStyle createSummaryStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}
