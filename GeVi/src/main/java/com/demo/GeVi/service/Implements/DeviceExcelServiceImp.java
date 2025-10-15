package com.demo.GeVi.service.Implements;

import com.demo.GeVi.model.Device;
import com.demo.GeVi.model.Device.DeviceStatus;
import com.demo.GeVi.service.DeviceExcelService;
import com.demo.GeVi.model.DeviceReport;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DeviceExcelServiceImp implements DeviceExcelService {

    @Override
    public byte[] exportDevicesExcel(List<Device> devices, List<DeviceReport> reports) {
        try (ByteArrayInputStream in = DeviceExcelServiceImp.exportToExcel(devices, reports)) {
            // Java 9+: readAllBytes(); si usas Java 8, cambia por utilitario equivalente.
            return in.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo generar el Excel de dispositivos", e);
        }
    }

    /*
     * Exporta dispositivos y reportes a un archivo Excel con resumen y detalles.
     */
    public static ByteArrayInputStream exportToExcel(List<Device> devices, List<DeviceReport> reports)
            throws IOException {

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // ===== Hoja 1: Dispositivos =====
            Sheet sheet = workbook.createSheet("Dispositivos");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle greenStyle = createSolidStyle(workbook, IndexedColors.BRIGHT_GREEN);
            CellStyle redStyle = createSolidStyle(workbook, IndexedColors.RED);
            CellStyle summaryStyle = createSummaryStyle(workbook);

            int tableStartRow = 9;
            int dataStartRow = tableStartRow + 1;

            // Título
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Resumen de dispositivos registrados");
            titleCell.setCellStyle(headerStyle);

            // Encabezados
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
            String colRef = "C" + (dataStartRow + 1) + ":C" + dataEndRow;

            // Resumen
            String[][] summary = {
                    { "TOTAL:", "SUBTOTAL(103, " + colRef + ")" },
                    { "TP NEWLAND:", getSubtotalFormula(colRef, "TP_NEWLAND", dataStartRow) },
                    { "LECTOR NEWLAND:", getSubtotalFormula(colRef, "LECTOR_NEWLAND", dataStartRow) },
                    { "TP DOLPHIN 9900:", getSubtotalFormula(colRef, "TP_DOLPHIN_9900", dataStartRow) },
                    { "LECTOR DOLPHIN 9900:", getSubtotalFormula(colRef, "LECTOR_DOLPHIN_9900", dataStartRow) },
                    { "BLUEBIRD:", getSubtotalFormula(colRef, "BLUEBIRD", dataStartRow) },
                    { "CELULAR/OTROS:", getSubtotalFormula(colRef, "CELULAR_OTROS", dataStartRow) }
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

            sheet.setAutoFilter(new CellRangeAddress(tableStartRow, dataEndRow - 1, 0, headers.length - 1));
            for (int i = 0; i < headers.length; i++)
                sheet.autoSizeColumn(i);

            // ===== Hoja 2: Defectuosos =====
            createDefectSheet(workbook, reports, devices);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    // ===== Hoja Defectuosos =====
    private static void createDefectSheet(Workbook workbook, List<DeviceReport> reports, List<Device> devices) {
        Sheet sheet = workbook.createSheet("Defectuosos");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle summaryStyle = createSummaryStyle(workbook);

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Resumen de dispositivos defectuosos");
        titleCell.setCellStyle(headerStyle);

        int tableStartRow = 9;
        int dataStartRow = tableStartRow + 1;
        String[] headers = {
                "Centro de Trabajo", "Número de Serie", "Tipo de Dispositivo", "Falla", "Fecha de Reporte",
                "Tiempo Transcurrido"
        };

        Row headerRow = sheet.createRow(tableStartRow);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Mapear: último reporte por dispositivo
        // (Si no hay relación getDevice(), ajusta a cómo accedes al id del device desde
        // el reporte)
        java.util.Map<Integer, DeviceReport> lastReportByDeviceId = new java.util.HashMap<>();
        for (DeviceReport r : reports) {
            if (r.getDevice() == null)
                continue;
            Integer devId = r.getDevice().getId();
            DeviceReport curr = lastReportByDeviceId.get(devId);
            if (curr == null || r.getReportingDate().isAfter(curr.getReportingDate())) {
                lastReportByDeviceId.put(devId, r);
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        int rowIdx = dataStartRow;

        // SOLO dispositivos actualmente DEFECTUOSO
        for (Device d : devices) {
            if (d.getStatus() != DeviceStatus.DEFECTUOSO)
                continue;

            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(d.getWorkCenter().getName());
            row.createCell(1).setCellValue(d.getSerialNumber());
            row.createCell(2).setCellValue(d.getDeviceType().name());

            DeviceReport last = lastReportByDeviceId.get(d.getId());
            String fail = "Sin información";
            String dt = "";
            String elapsed = "";

            if (last != null) {
                fail = (last.getFailTypeDevice() != null)
                        ? last.getFailTypeDevice().getName()
                        : (last.getPersonalizedFailure() != null && !last.getPersonalizedFailure().isBlank()
                                ? last.getPersonalizedFailure()
                                : "Sin información");

                dt = last.getReportingDate() != null ? last.getReportingDate().format(formatter) : "";
                if (last.getReportingDate() != null) {
                    elapsed = formatElapsed(Duration.between(last.getReportingDate(), LocalDateTime.now()));
                }
            }

            row.createCell(3).setCellValue(fail);
            row.createCell(4).setCellValue(dt);
            row.createCell(5).setCellValue(elapsed);
        }

        int dataEndRow = rowIdx;
        String colRef = "C" + (dataStartRow + 1) + ":C" + dataEndRow; // Columna C = Tipo de Dispositivo

        String[][] summary = {
                { "TOTAL EN DEFECTUOSOS:", "SUBTOTAL(103, " + colRef + ")" },
                { "TP NEWLAND:", getSubtotalFormula(colRef, "TP_NEWLAND", dataStartRow) },
                { "LECTOR NEWLAND:", getSubtotalFormula(colRef, "LECTOR_NEWLAND", dataStartRow) },
                { "TP DOLPHIN 9900:", getSubtotalFormula(colRef, "TP_DOLPHIN_9900", dataStartRow) },
                { "LECTOR DOLPHIN 9900:", getSubtotalFormula(colRef, "LECTOR_DOLPHIN_9900", dataStartRow) },
                { "BLUEBIRD:", getSubtotalFormula(colRef, "BLUEBIRD", dataStartRow) },
                { "CELULARES/OTROS:", getSubtotalFormula(colRef, "CELULAR_OTROS", dataStartRow) }
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

        sheet.setAutoFilter(new CellRangeAddress(tableStartRow, dataEndRow - 1, 0, headers.length - 1));
        for (int i = 0; i < headers.length; i++)
            sheet.autoSizeColumn(i);
    }

    // ===== Metodos auxiliares =====
    private static String formatElapsed(Duration d) {
        long days = d.toDays();
        long hrs = d.toHoursPart();
        long mins = d.toMinutesPart();
        return (days > 0) ? String.format("%dd %02dh %02dm", days, hrs, mins) : String.format("%02dh %02dm", hrs, mins);
    }

    private static String getSubtotalFormula(String colRef, String value, int startRow) {
        return "SUMPRODUCT(SUBTOTAL(103,OFFSET(" + colRef + ",ROW(" + colRef + ")-ROW(C" + (startRow + 1)
                + "),0,1)),--(" + colRef + "=\"" + value + "\"))";
    }

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
