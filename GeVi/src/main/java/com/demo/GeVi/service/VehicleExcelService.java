package com.demo.GeVi.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    public static ByteArrayInputStream exportToExcel(List<Vehicle> vehicle, List<VehicleReport> report)
            throws IOException {

        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // ======================== HOJA 1: VEHÍCULOS ========================
            Sheet sheet = workbook.createSheet("Vehículos");

            // Estilos generales
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle greenStyle = workbook.createCellStyle();
            greenStyle.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
            greenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle yellowStyle = workbook.createCellStyle();
            yellowStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            yellowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle redStyle = workbook.createCellStyle();
            redStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
            redStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle summaryStyle = workbook.createCellStyle();
            Font summaryFont = workbook.createFont();
            summaryFont.setBold(true);
            summaryStyle.setFont(summaryFont);
            summaryStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            summaryStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            summaryStyle.setBorderTop(BorderStyle.THIN);
            summaryStyle.setBorderBottom(BorderStyle.THIN);
            summaryStyle.setBorderLeft(BorderStyle.THIN);
            summaryStyle.setBorderRight(BorderStyle.THIN);

            int tableStartRow = 7;
            int dataStartRow = tableStartRow + 1;

            // Título resumen
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
            Row counterTitleRow = sheet.createRow(0);
            Cell counterTitleCell = counterTitleRow.createCell(0);
            counterTitleCell.setCellValue("Resumen de vehículos registrados");
            counterTitleCell.setCellStyle(headerStyle);

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
            for (Vehicle v : vehicle) {
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
                    case DISPONIBLE:
                        statusCell.setCellStyle(greenStyle);
                        break;
                    case OPERANDO_CON_FALLA:
                        statusCell.setCellStyle(yellowStyle);
                        break;
                    case INDISPONIBLE:
                        statusCell.setCellStyle(redStyle);
                        break;
                }
            }

            // Rango final real de datos
            int dataEndRow = rowIdx;

            // Resumen
            String[][] summary = {
                    {
                            "TOTAL:",
                            "SUBTOTAL(103, J" + 9 + ":J" + dataEndRow + ")"
                    },
                    {
                            "DISPONIBLES:",
                            "SUMPRODUCT(SUBTOTAL(103,OFFSET(J" + 9 + ":J" + dataEndRow +
                                    ",ROW(J" + 9 + ":J" + dataEndRow + ")-ROW(J" + 9
                                    + "),0,1)),--(J" +
                                    9 + ":J" + dataEndRow + "=\"DISPONIBLE\"))"
                    },
                    {
                            "OPERANDO CON FALLA:",
                            "SUMPRODUCT(SUBTOTAL(103,OFFSET(J" + 9 + ":J" + dataEndRow +
                                    ",ROW(J" + 9 + ":J" + dataEndRow + ")-ROW(J" + 9
                                    + "),0,1)),--(J" +
                                    9 + ":J" + dataEndRow + "=\"OPERANDO_CON_FALLA\"))"
                    },
                    {
                            "INDISPONIBLES:",
                            "SUMPRODUCT(SUBTOTAL(103,OFFSET(J" + 9 + ":J" + dataEndRow +
                                    ",ROW(J" + 9 + ":J" + dataEndRow + ")-ROW(J" + 9
                                    + "),0,1)),--(J" +
                                    9 + ":J" + dataEndRow + "=\"INDISPONIBLE\"))"
                    }
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

            // Autofiltro y autosize
            sheet.setAutoFilter(new CellRangeAddress(tableStartRow, dataEndRow, 0, headers.length - 1));
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // ======================== HOJA 2: TALLER ========================
            createUnavailableSheet(workbook, report, "Taller", Ubication.TALLER);

            // ======================== HOJA 3: PATIO ========================
            createUnavailableSheet(workbook, report, "Patio", Ubication.PATIO);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private static void createUnavailableSheet(Workbook workbook, List<VehicleReport> reports, String sheetName,
            Ubication ubication) {
        Sheet sheet = workbook.createSheet(sheetName);

        // Estilos
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle summaryStyle = workbook.createCellStyle();
        Font summaryFont = workbook.createFont();
        summaryFont.setBold(true);
        summaryStyle.setFont(summaryFont);
        summaryStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        summaryStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        summaryStyle.setBorderTop(BorderStyle.THIN);
        summaryStyle.setBorderBottom(BorderStyle.THIN);
        summaryStyle.setBorderLeft(BorderStyle.THIN);
        summaryStyle.setBorderRight(BorderStyle.THIN);

        // Título del resumen visual
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Resumen de vehículos en " + sheetName.toLowerCase());
        titleCell.setCellStyle(headerStyle);

        // Contador total
        int summaryCol = 0;
        Row totalRow = sheet.createRow(1);
        Cell label = totalRow.createCell(summaryCol);
        label.setCellValue("TOTAL EN " + sheetName.toUpperCase() + ":");
        label.setCellStyle(summaryStyle);
        Cell valueCell = totalRow.createCell(summaryCol + 1);
        valueCell.setCellStyle(summaryStyle);

        // Encabezado
        String[] headers = { "Económico", "Placa", "Centro Trabajo", "Falla", "Fecha Reporte", "Tiempo Transcurrido" };
        Row headerRow = sheet.createRow(4);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        int dataStartRow = 5;
        int rowIdx = dataStartRow;

        for (VehicleReport r : reports) {
            if (r.getNewStatus() == Status.INDISPONIBLE && r.getLocationUnavailable() == ubication) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(r.getVehicle().getEconomical());
                row.createCell(1).setCellValue(r.getVehicle().getBadge());
                row.createCell(2).setCellValue(r.getVehicle().getWorkCenter().getName());

                String fail = (r.getFailType() != null)
                        ? r.getFailType().getName()
                        : r.getPersonalizedFailure();
                row.createCell(3).setCellValue(fail != null ? fail : "N/A");

                row.createCell(4).setCellValue(r.getReportingDate().format(formatter));

                Duration duration = Duration.between(r.getReportingDate(), LocalDateTime.now());
                long days = duration.toDays();
                long hours = duration.toHoursPart();
                long minutes = duration.toMinutesPart();

                String time = (days > 0)
                        ? String.format("%dd %02dh %02dm", days, hours, minutes)
                        : String.format("%02dh %02dm", hours, minutes);

                row.createCell(5).setCellValue(time);
            }
        }

        int firstDataRowExcel = dataStartRow + 1;
        int lastDataRowExcel = rowIdx;

        if (rowIdx > dataStartRow) {
            valueCell.setCellFormula("SUBTOTAL(103,A" + firstDataRowExcel + ":A" + lastDataRowExcel + ")");
        } else {
            valueCell.setCellValue(0);
        }

        sheet.setAutoFilter(new CellRangeAddress(4, rowIdx - 1, 0, headers.length - 1));
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
