package com.demo.GeVi.service;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.demo.GeVi.model.Status;
import com.demo.GeVi.model.Ubication;
import com.demo.GeVi.model.Vehicle;
import com.demo.GeVi.model.VehicleReport;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class VehicleExcelService {

    /*
     * Genera un archivo Excel con vehículos registrados y vehículos indisponibles.
     */
    public static ByteArrayInputStream exportToExcel(List<Vehicle> vehicles, List<VehicleReport> reports)
            throws IOException {

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // HOJA 1: Vehículos registrados
            createRegisteredVehiclesSheet(workbook, vehicles);

            // HOJA 2 y 3: Vehículos en taller y en patio
            createUnavailableSheet(workbook, reports, "Taller", Ubication.TALLER);
            createUnavailableSheet(workbook, reports, "Patio", Ubication.PATIO);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private static void createRegisteredVehiclesSheet(Workbook workbook, List<Vehicle> vehicles) {
        Sheet sheet = workbook.createSheet("Vehículos");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle greenStyle = createFillStyle(workbook, IndexedColors.BRIGHT_GREEN);
        CellStyle yellowStyle = createFillStyle(workbook, IndexedColors.YELLOW);
        CellStyle redStyle = createFillStyle(workbook, IndexedColors.RED);
        CellStyle summaryStyle = createSummaryStyle(workbook);

        int tableStartRow = 7;
        int dataStartRow = tableStartRow + 1;

        // Título
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Resumen de vehículos registrados");
        titleCell.setCellStyle(headerStyle);

        // Encabezados
        String[] headers = {
                "Económico", "Placa", "Propiedad", "Kilometraje", "Marca",
                "Modelo", "Año", "Centro Trabajo", "Proceso", "Estado"
        };

        Row headerRow = sheet.createRow(tableStartRow);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Datos
        int rowIdx = dataStartRow;
        for (Vehicle v : vehicles) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(v.getEconomical());
            row.createCell(1).setCellValue(v.getBadge());
            row.createCell(2).setCellValue(v.getProperty().name());
            row.createCell(3).setCellValue(v.getMileage());
            row.createCell(4).setCellValue(v.getBrand());
            row.createCell(5).setCellValue(v.getModel());
            row.createCell(6).setCellValue(v.getYear().getValue());
            row.createCell(7).setCellValue(v.getWorkCenter().getName());
            row.createCell(8).setCellValue(v.getProcess().getName());

            Cell statusCell = row.createCell(9);
            statusCell.setCellValue(v.getStatus().name());

            switch (v.getStatus()) {
                case DISPONIBLE -> statusCell.setCellStyle(greenStyle);
                case OPERANDO_CON_FALLA -> statusCell.setCellStyle(yellowStyle);
                case INDISPONIBLE -> statusCell.setCellStyle(redStyle);
            }
        }

        // Resumen
        int dataEndRow = rowIdx;
        String statusCol = "J" + (dataStartRow + 1) + ":J" + dataEndRow;
        String[][] summary = {
                { "TOTAL:", "SUBTOTAL(103, " + statusCol + ")" },
                { "DISPONIBLES:", subtotalFormula(statusCol, "DISPONIBLE", dataStartRow) },
                { "OPERANDO CON FALLA:", subtotalFormula(statusCol, "OPERANDO_CON_FALLA", dataStartRow) },
                { "INDISPONIBLES:", subtotalFormula(statusCol, "INDISPONIBLE", dataStartRow) }
        };

        for (int i = 0; i < summary.length; i++) {
            Row row = sheet.createRow(1 + i);
            row.createCell(0).setCellValue(summary[i][0]);
            row.getCell(0).setCellStyle(summaryStyle);
            row.createCell(1).setCellFormula(summary[i][1]);
            row.getCell(1).setCellStyle(summaryStyle);
        }

        sheet.setAutoFilter(new CellRangeAddress(tableStartRow, dataEndRow - 1, 0, headers.length - 1));
        for (int i = 0; i < headers.length; i++)
            sheet.autoSizeColumn(i);
    }

    private static void createUnavailableSheet(Workbook workbook, List<VehicleReport> reports, String sheetName,
            Ubication ubication) {
        Sheet sheet = workbook.createSheet(sheetName);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle summaryStyle = createSummaryStyle(workbook);

        // Título
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Resumen de vehículos en " + sheetName.toLowerCase());
        titleCell.setCellStyle(headerStyle);

        // Contador
        Row totalRow = sheet.createRow(1);
        totalRow.createCell(0).setCellValue("TOTAL EN " + sheetName.toUpperCase() + ":");
        Cell totalCountCell = totalRow.createCell(1);
        totalRow.getCell(0).setCellStyle(summaryStyle);
        totalCountCell.setCellStyle(summaryStyle);

        // Encabezados
        String[] headers = { "Económico", "Placa", "Centro Trabajo", "Falla", "Fecha Reporte", "Tiempo Transcurrido" };
        Row headerRow = sheet.createRow(4);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Datos
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        int dataStartRow = 5;
        int rowIdx = dataStartRow;

        for (VehicleReport r : reports) {
            if (r.getNewStatus() == Status.INDISPONIBLE && r.getLocationUnavailable() == ubication) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(r.getVehicle().getEconomical());
                row.createCell(1).setCellValue(r.getVehicle().getBadge());
                row.createCell(2).setCellValue(r.getVehicle().getWorkCenter().getName());

                String fail = (r.getFailType() != null) ? r.getFailType().getName() : r.getPersonalizedFailure();
                row.createCell(3).setCellValue(fail != null ? fail : "N/A");
                row.createCell(4).setCellValue(r.getReportingDate().format(formatter));

                Duration d = Duration.between(r.getReportingDate(), LocalDateTime.now());
                String elapsed = (d.toDays() > 0)
                        ? String.format("%dd %02dh %02dm", d.toDays(), d.toHoursPart(), d.toMinutesPart())
                        : String.format("%02dh %02dm", d.toHoursPart(), d.toMinutesPart());
                row.createCell(5).setCellValue(elapsed);
            }
        }

        int firstDataRow = dataStartRow + 1;
        int lastDataRow = rowIdx;
        if (rowIdx > dataStartRow) {
            totalCountCell.setCellFormula("SUBTOTAL(103,A" + firstDataRow + ":A" + lastDataRow + ")");
        } else {
            totalCountCell.setCellValue(0);
        }

        sheet.setAutoFilter(new CellRangeAddress(4, rowIdx - 1, 0, headers.length - 1));
        for (int i = 0; i < headers.length; i++)
            sheet.autoSizeColumn(i);
    }

    // ======================== ESTILOS ========================

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

    private static CellStyle createFillStyle(Workbook wb, IndexedColors color) {
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

    private static String subtotalFormula(String colRef, String value, int dataStartRow) {
        return "SUMPRODUCT(SUBTOTAL(103,OFFSET(" + colRef + ",ROW(" + colRef + ")-ROW(" + colRef.split(":")[0]
                + "),0,1)),--(" + colRef + "=\"" + value + "\"))";
    }
}
