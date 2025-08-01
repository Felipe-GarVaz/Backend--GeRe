package com.demo.GeVi.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;
import com.demo.GeVi.model.Vehicle;

public class VehicleExcelService {

    public static ByteArrayInputStream exportToExcel(List<Vehicle> vehicle) throws IOException {

        try (
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Vehículos");

            // Estilos comunes
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

            // Variables contadoras
            int total = 0;
            int available = 0;
            int withFailure = 0;
            int unavailable = 0;

            for (Vehicle v : vehicle) {
                total++;

                String status = v.getStatus().name();
                if (status.equals("OPERANDO") || status.equals("DISPONIBLE")) {
                    available++;
                }
                if (status.equals("OPERANDO_CON_FALLA") || status.equals("DISPONIBLE_CON_FALLA")) {
                    withFailure++;
                }
                if (status.equals("INDISPONIBLE") || status.equals("INOPERATIVO")) {
                    unavailable++;
                }
            }

            // 👉 Resumen de totales (en la parte superior derecha)
            int summaryCol = 11;

            Row totalRow = sheet.createRow(0);
            Cell totalLabel = totalRow.createCell(summaryCol);
            totalLabel.setCellValue("TOTAL:");
            totalLabel.setCellStyle(summaryStyle);
            Cell totalValue = totalRow.createCell(summaryCol + 1);
            totalValue.setCellValue(total);
            totalValue.setCellStyle(summaryStyle);

            Row availableRow = sheet.createRow(1);
            Cell availableLabel = availableRow.createCell(summaryCol);
            availableLabel.setCellValue("DISPONIBLES:");
            availableLabel.setCellStyle(summaryStyle);
            Cell availableValue = availableRow.createCell(summaryCol + 1);
            availableValue.setCellValue(available);
            availableValue.setCellStyle(summaryStyle);

            Row failureRow = sheet.createRow(2);
            Cell failureLabel = failureRow.createCell(summaryCol);
            failureLabel.setCellValue("CON FALLA:");
            failureLabel.setCellStyle(summaryStyle);
            Cell failureValue = failureRow.createCell(summaryCol + 1);
            failureValue.setCellValue(withFailure);
            failureValue.setCellStyle(summaryStyle);

            Row unavailableRow = sheet.createRow(3);
            Cell unavailableLabel = unavailableRow.createCell(summaryCol);
            unavailableLabel.setCellValue("INDISPONIBLES:");
            unavailableLabel.setCellStyle(summaryStyle);
            Cell unavailableValue = unavailableRow.createCell(summaryCol + 1);
            unavailableValue.setCellValue(unavailable);
            unavailableValue.setCellStyle(summaryStyle);

            // 👉 Encabezado
            String[] headers = { "Económico", "Placa", "Propiedad", "Kilometraje", "Marca", "Modelo", "Año",
                    "Centro Trabajo", "Proceso", "Estado" };

            int headerRowIndex = 5;
            Row headerRow = sheet.createRow(headerRowIndex);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 👉 Datos
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
                    case "OPERANDO":
                        statusCell.setCellStyle(greenStyle);
                        break;
                    case "OPERANDO_CON_FALLA":
                    case "DISPONIBLE_CON_FALLA":
                        statusCell.setCellStyle(yellowStyle);
                        break;
                    case "INDISPONIBLE":
                    case "INOPERATIVO":
                        statusCell.setCellStyle(redStyle);
                        break;
                    default:
                        break;
                }
            }

            // Autoajustar columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            sheet.autoSizeColumn(summaryCol);
            sheet.autoSizeColumn(summaryCol + 1);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
