package ClassroomProject;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class Export {

    private final Teacher currentUser;
    private final ReservationSystem reservationSystem;
    private final Classroom selectedClassroom;

    public Export(Teacher currentUser, ReservationSystem reservationSystem, Classroom selectedClassroom) {
        this.currentUser = currentUser;
        this.reservationSystem = reservationSystem;
        this.selectedClassroom = selectedClassroom;
    }

    public void generateExcel() {
        if (selectedClassroom == null) {
            JOptionPane.showMessageDialog(null, "Please select a classroom first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 1. Create Workbook and Sheet
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(selectedClassroom.getName() + " Bookings");

            // 2. Create Header Font and Styles
            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            headerFont.setColor(IndexedColors.BLACK.getIndex());

            XSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            // 3. Create Highlight Style for the current user's bookings
            XSSFFont highlightFont = workbook.createFont();
            highlightFont.setBold(true);
            highlightFont.setColor(IndexedColors.BLUE.getIndex());

            XSSFCellStyle highlightStyle = workbook.createCellStyle();
            highlightStyle.setFont(highlightFont);
            highlightStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            highlightStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 4. Create the Header Row
            String[] headers = {"Date", "Day", "Start Time", "End Time", "Course", "Code", "Booked By"};
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 5. Populate Data Rows
            List<Booking> bookings = selectedClassroom.getBookings();
            // Sort bookings by date first
            bookings.sort(Comparator.comparing(Booking::getDate));

            int rowNum = 1;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            for (Booking b : bookings) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(b.getDate().format(dateFormatter));
                row.createCell(1).setCellValue(b.getTimeSlot().getDayOfWeek().toString());
                row.createCell(2).setCellValue(b.getTimeSlot().getStartTime().format(timeFormatter));
                row.createCell(3).setCellValue(b.getTimeSlot().getEndTime().format(timeFormatter));
                row.createCell(4).setCellValue(b.getCourse());
                row.createCell(5).setCellValue(b.getCode());
                row.createCell(6).setCellValue(b.getTeacher().getName());

                // Apply highlight if the booking belongs to the current user
                if (b.getTeacher().equals(currentUser)) {
                    for (int i = 0; i < headers.length; i++) {
                        row.getCell(i).setCellStyle(highlightStyle);
                    }
                }
            }

            // 6. Add Statistics Section
            rowNum += 2; // Add a 2-row gap
            Row statsHeaderRow = sheet.createRow(rowNum++);
            Cell statsHeaderCell = statsHeaderRow.createCell(0);
            statsHeaderCell.setCellValue("System-Wide Booking Statistics");
            statsHeaderCell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(0); // Widen the first column for the stats

            // Calculate total bookings
            long totalSystemBookings = 0;
            for (Classroom c : reservationSystem.getClassrooms()) {
                totalSystemBookings += c.getBookings().size();
            }

            // Create stats headers
            Row statsDataHeader = sheet.createRow(rowNum++);
            statsDataHeader.createCell(0).setCellValue("Classroom");
            statsDataHeader.createCell(1).setCellValue("Total Bookings");
            statsDataHeader.createCell(2).setCellValue("% of All Bookings");
            statsDataHeader.getCell(0).setCellStyle(headerStyle);
            statsDataHeader.getCell(1).setCellStyle(headerStyle);
            statsDataHeader.getCell(2).setCellStyle(headerStyle);


            // Write stats for each room
            for (Classroom c : reservationSystem.getClassrooms()) {
                Row roomStatRow = sheet.createRow(rowNum++);
                int bookingCount = c.getBookings().size();
                double percentage = (totalSystemBookings == 0) ? 0 : ((double) bookingCount / totalSystemBookings) * 100.0;

                roomStatRow.createCell(0).setCellValue(c.getName());
                roomStatRow.createCell(1).setCellValue(bookingCount);
                roomStatRow.createCell(2).setCellValue(String.format("%.2f%%", percentage));
            }

            // 7. Auto-size all columns for a clean look
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 8. Write the file
            String fileName = "data/Export_" + selectedClassroom.getName().replaceAll("\\s+", "_") + ".xlsx";
            File file = new File(fileName);
            file.getParentFile().mkdirs();

            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();

            JOptionPane.showMessageDialog(null,
                    "Export successful!\nFile saved to: " + file.getAbsolutePath(),
                    "Export Complete",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error during export: " + e.getMessage(), "Export Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}