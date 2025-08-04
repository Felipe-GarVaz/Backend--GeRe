package com.demo.GeVi.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;
import com.demo.GeVi.model.Status;
import com.demo.GeVi.model.Ubication;
import com.demo.GeVi.model.Vehicle;
import com.demo.GeVi.model.VehicleReport;
import org.apache.poi.ss.util.CellRangeAddress;

public class VehicleExcelService {

    public static ByteArrayInputStream exportToExcel(List<Vehicle> vehicle, List<VehicleReport> report)
            throws IOException {

        try (
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // ======================== HOJA 1: VEHÍCULOS ========================
            Sheet sheet = workbook.createSheet("Vehículos");

            // Estilos
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle greenStyle = workbook.createCellStyle();
            greenStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            greenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle yellowStyle = workbook.createCellStyle();
            yellowStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            yellowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle redStyle = workbook.createCellStyle();
            redStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
            redStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle summaryStyle = workbook.createCellStyle();
            Font summaryFont = workbook.createFont();
            summaryFont.setBold(true);
            summaryStyle.setFont(summaryFont);
            summaryStyle.setBorderTop(BorderStyle.THIN);
            summaryStyle.setBorderBottom(BorderStyle.THIN);
            summaryStyle.setBorderLeft(BorderStyle.THIN);
            summaryStyle.setBorderRight(BorderStyle.THIN);

            // Fila 0 – Total de registros
            int summaryCol = 11;

            Row totalRow = sheet.createRow(0);
            Cell totalLabel = totalRow.createCell(summaryCol);
            totalLabel.setCellValue("TOTAL:");
            totalLabel.setCellStyle(summaryStyle);

            Cell totalForm = totalRow.createCell(summaryCol + 1);
            totalForm.setCellFormula("SUBTOTAL(103, A7:A" + (vehicle.size() + 6) + ")");
            totalForm.setCellStyle(summaryStyle);

            // Fila 1 – DISPONIBLES
            Row availableRow = sheet.createRow(1);
            availableRow.createCell(summaryCol).setCellValue("DISPONIBLES:");
            availableRow.getCell(summaryCol).setCellStyle(summaryStyle);

            Cell availableForm = availableRow.createCell(summaryCol + 1);
            availableForm.setCellFormula(
                    "SUMPRODUCT(SUBTOTAL(103,OFFSET(J7:J" + (vehicle.size() + 6) + ",ROW(J7:J" + (vehicle.size() + 6)
                            + ")-ROW(J7),0,1)),--(J7:J" + (vehicle.size() + 6) + "=\"DISPONIBLE\"))");
            availableForm.setCellStyle(summaryStyle);

            // Fila 2 – CON FALLA:
            Row withFailureRow = sheet.createRow(2);
            withFailureRow.createCell(summaryCol).setCellValue("CON FALLA:");
            withFailureRow.getCell(summaryCol).setCellStyle(summaryStyle);

            Cell withFailureForm = withFailureRow.createCell(summaryCol + 1);
            withFailureForm.setCellFormula(
                    "SUMPRODUCT(SUBTOTAL(103,OFFSET(J7:J" + (vehicle.size() + 6) + ",ROW(J7:J" + (vehicle.size() + 6)
                            + ")-ROW(J7),0,1)),--(J7:J" + (vehicle.size() + 6) + "=\"OPERANDO_CON_FALLA\"))");
            withFailureForm.setCellStyle(summaryStyle);

            // Fila 3 – INDISPONIBLES:
            Row unavailableRow = sheet.createRow(3);
            unavailableRow.createCell(summaryCol).setCellValue("INDISPONIBLES:");
            unavailableRow.getCell(summaryCol).setCellStyle(summaryStyle);

            Cell unvailableForm = unavailableRow.createCell(summaryCol + 1);
            unvailableForm.setCellFormula(
                    "SUMPRODUCT(SUBTOTAL(103,OFFSET(J7:J" + (vehicle.size() + 6) + ",ROW(J7:J" + (vehicle.size() + 6)
                            + ")-ROW(J7),0,1)),--(J7:J" + (vehicle.size() + 6) + "=\"INDISPONIBLE\"))");
            unvailableForm.setCellStyle(summaryStyle);

            // Encabezados
            String[] headers = { "Económico", "Placa", "Propiedad", "Kilometraje", "Marca", "Modelo", "Año",
                    "Centro Trabajo", "Proceso", "Estado" };

            Row headerRow = sheet.createRow(5);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            int rowIdx = 6;
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

                switch (v.getStatus().name()) {
                    case "DISPONIBLE":
                        statusCell.setCellStyle(greenStyle);
                        break;
                    case "DISPONIBLE_CON_FALLA":
                        statusCell.setCellStyle(yellowStyle);
                        break;
                    case "INDISPONIBLE":
                        statusCell.setCellStyle(redStyle);
                        break;
                }
            }

            for (int i = 0; i < headers.length; i++)
                sheet.autoSizeColumn(i);
            sheet.autoSizeColumn(summaryCol);
            sheet.autoSizeColumn(summaryCol + 1);

            // Agrega autofiltro en encabezado hoja Vehiculos
            sheet.setAutoFilter(new CellRangeAddress(5, rowIdx - 1, 0, headers.length - 1));

            // ======================== HOJA 2: TALLER ========================
            Sheet workshopSheet = workbook.createSheet("Taller");

            // Estilos
            CellStyle headerStyleWS = workbook.createCellStyle();
            Font headerFontWS = workbook.createFont();
            headerFontWS.setBold(true);
            headerFontWS.setColor(IndexedColors.WHITE.getIndex());
            headerStyleWS.setFont(headerFontWS);
            headerStyleWS.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerStyleWS.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle summaryStyleWS = workbook.createCellStyle();
            Font summaryFontWS = workbook.createFont();
            summaryFontWS.setBold(true);
            summaryStyleWS.setFont(summaryFontWS);
            summaryStyleWS.setBorderTop(BorderStyle.THIN);
            summaryStyleWS.setBorderBottom(BorderStyle.THIN);
            summaryStyleWS.setBorderLeft(BorderStyle.THIN);
            summaryStyleWS.setBorderRight(BorderStyle.THIN);

            // Contador total
            int summaryColWS = 6;

            // Total parte superior
            Row totalRowWS = workshopSheet.createRow(0);
            Cell labelWS = totalRowWS.createCell(summaryColWS);
            labelWS.setCellValue("TOTAL EN TALLER:");
            labelWS.setCellStyle(summaryStyleWS);
            Cell valueWS = totalRowWS.createCell(summaryColWS + 1);
            valueWS.setCellStyle(summaryStyleWS);

            // Encabezado
            String[] wsHeaders = { "Económico", "Placa", "Centro Trabajo", "Falla", "Fecha Reporte",
                    "Tiempo Transcurrido" };
            Row headerRowWS = workshopSheet.createRow(2);
            for (int i = 0; i < wsHeaders.length; i++) {
                Cell cell = headerRowWS.createCell(i);
                cell.setCellValue(wsHeaders[i]);
                cell.setCellStyle(headerStyleWS);
            }

            // Datos
            int rowIdxWS = 3;
            for (VehicleReport r : report) {
                if (r.getNewStatus() == Status.INDISPONIBLE && r.getLocationUnavailable() == Ubication.TALLER) {
                    Row row = workshopSheet.createRow(rowIdxWS++);
                    row.createCell(0).setCellValue(r.getVehicle().getEconomical());
                    row.createCell(1).setCellValue(r.getVehicle().getBadge());
                    row.createCell(2).setCellValue(r.getVehicle().getWorkCenter().getName());
                    String fail = (r.getFailType() != null) ? r.getFailType().getName() : r.getPersonalizedFailure();
                    row.createCell(3).setCellValue(fail);
                    row.createCell(4).setCellValue(r.getReportingDate().toString());

                    java.time.Duration duration = java.time.Duration.between(r.getReportingDate(),
                            java.time.LocalDateTime.now());
                    long totalMinutes = duration.getSeconds() / 60;
                    long days = totalMinutes / (24 * 60);
                    long hours = (totalMinutes % (24 * 60)) / 60;
                    long minutes = totalMinutes % 60;
                    String time = (days > 0)
                            ? String.format("%dd %02dh %02dm", days, hours, minutes)
                            : String.format("%02dh %02dm", hours, minutes);

                    row.createCell(5).setCellValue(time);
                }
            }

            // Aplicar fórmula dinámica correctamente al final del bloque de datos
            if (rowIdxWS > 3) {
                valueWS.setCellFormula("SUBTOTAL(103,A5:A" + rowIdxWS + ")");
            } else {
                valueWS.setCellValue(0);
            }

            // Agrega autofiltro en encabezado hoja Taller
            workshopSheet.setAutoFilter(new CellRangeAddress(2, rowIdxWS - 1, 0, wsHeaders.length - 1));

            // ======================== HOJA 3: PATIO ========================
            Sheet yardSheet = workbook.createSheet("Patio");

            // Estilos
            CellStyle headerStyleYard = workbook.createCellStyle();
            Font headerFontYard = workbook.createFont();
            headerFontYard.setBold(true);
            headerFontYard.setColor(IndexedColors.WHITE.getIndex());
            headerStyleYard.setFont(headerFontYard);
            headerStyleYard.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerStyleYard.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle summaryStyleYard = workbook.createCellStyle();
            Font summaryFontYard = workbook.createFont();
            summaryFontYard.setBold(true);
            summaryStyleYard.setFont(summaryFontYard);
            summaryStyleYard.setBorderTop(BorderStyle.THIN);
            summaryStyleYard.setBorderBottom(BorderStyle.THIN);
            summaryStyleYard.setBorderLeft(BorderStyle.THIN);
            summaryStyleYard.setBorderRight(BorderStyle.THIN);

            // Total parte superior
            int summaryColYard = 6;

            Row totalRowTopYard = yardSheet.createRow(0);
            Cell labelTopYard = totalRowTopYard.createCell(summaryColYard);
            labelTopYard.setCellValue("TOTAL EN PATIO:");
            labelTopYard.setCellStyle(summaryStyleYard);
            Cell valueTopYard = totalRowTopYard.createCell(summaryColYard + 1);
            valueTopYard.setCellStyle(summaryStyleYard);

            // Encabezados
            String[] yardHeaders = { "Económico", "Placa", "Centro Trabajo", "Falla", "Fecha Reporte",
                    "Tiempo Transcurrido" };
            Row headerRowYard = yardSheet.createRow(2);
            for (int i = 0; i < yardHeaders.length; i++) {
                Cell cell = headerRowYard.createCell(i);
                cell.setCellValue(yardHeaders[i]);
                cell.setCellStyle(headerStyleYard);
            }

            // Datos
            int rowIdxYard = 3;
            for (VehicleReport r : report) {
                if (r.getNewStatus() == Status.INDISPONIBLE && r.getLocationUnavailable() == Ubication.PATIO) {
                    Row row = yardSheet.createRow(rowIdxYard++);
                    row.createCell(0).setCellValue(r.getVehicle().getEconomical());
                    row.createCell(1).setCellValue(r.getVehicle().getBadge());
                    row.createCell(2).setCellValue(r.getVehicle().getWorkCenter().getName());
                    String fail = (r.getFailType() != null) ? r.getFailType().getName() : r.getPersonalizedFailure();
                    row.createCell(3).setCellValue(fail);
                    row.createCell(4).setCellValue(r.getReportingDate().toString());

                    java.time.Duration duration = java.time.Duration.between(r.getReportingDate(),
                            java.time.LocalDateTime.now());
                    long totalMinutes = duration.getSeconds() / 60;
                    long days = totalMinutes / (24 * 60);
                    long hours = (totalMinutes % (24 * 60)) / 60;
                    long minutes = totalMinutes % 60;

                    String time = (days > 0)
                            ? String.format("%dd %02dh %02dm", days, hours, minutes)
                            : String.format("%02dh %02dm", hours, minutes);
                    row.createCell(5).setCellValue(time);
                }
            }

            // Fórmula dinámica para total en Patio (solo visibles al filtrar)
            if (rowIdxYard > 3) {
                valueTopYard.setCellFormula("SUBTOTAL(103,A5:A" + rowIdxYard + ")");
            }else {
                valueTopYard.setCellValue(0);
            }

            // Agrega autofiltro en encabezado hoja Patio
            yardSheet.setAutoFilter(new CellRangeAddress(2, rowIdxYard - 1, 0, yardHeaders.length - 1));

            // Autoajustar columnas
            for (int i = 0; i < yardHeaders.length; i++)
                yardSheet.autoSizeColumn(i);
            yardSheet.autoSizeColumn(6);
            yardSheet.autoSizeColumn(7);

            // Ajustar columnas
            for (int i = 0; i < wsHeaders.length; i++)
                workshopSheet.autoSizeColumn(i);
            workshopSheet.autoSizeColumn(6);
            workshopSheet.autoSizeColumn(7);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
