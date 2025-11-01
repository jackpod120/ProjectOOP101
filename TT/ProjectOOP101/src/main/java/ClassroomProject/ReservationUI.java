package ClassroomProject;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Vector;

public class ReservationUI extends JFrame {
    private final Teacher teacher;
    private final ReservationSystem reservationSystem;
    private Classroom classroom;
    private final JPanel mainPanel;     // Make this an instance variable
    private JPanel centerPanel;   // Make this an instance variable

    public ReservationUI(Teacher teacher, ReservationSystem reservationSystem) {
        super("Classroom Reservation System");
        this.teacher = teacher;
        this.reservationSystem = reservationSystem;

        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        this.mainPanel = new JPanel(new BorderLayout(10, 10)); // ใช้ BorderLayout เพื่อแบ่งโซน ซ้าย-กลาง
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // เพิ่มช่องว่างรอบๆ ขอบ
        add(mainPanel);

        JPanel leftPanel = createLeftControlPanel();
        mainPanel.add(leftPanel, BorderLayout.WEST); // เพิ่ม Panel ซ้ายเข้าไปในโซน WEST

        this.centerPanel = createSchedulerPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER); // เพิ่ม Panel กลางเข้าไปในโซน CENTER

    }

    private JPanel createLeftControlPanel() {
        JPanel panel = new JPanel();
        // ใช้ BoxLayout เพื่อจัดเรียง Component จากบนลงล่าง
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(180, 0)); // กำหนดความกว้างของ Panel

        // Dropdown สำหรับเลือกห้อง
        // Create a model for the dropdown
        JComboBox<Classroom> roomSelector = new JComboBox<>();
        List<Classroom> classrooms = reservationSystem.getClassrooms();
        if (classrooms != null && !classrooms.isEmpty()) {
            for (Classroom classroom : classrooms) {
                roomSelector.addItem(classroom);
            }
        }
        this.classroom = classrooms.getFirst();
        roomSelector.setMaximumSize(new Dimension(Integer.MAX_VALUE, roomSelector.getPreferredSize().height));

        // ปุ่มต่างๆ
        JButton bookButton = new JButton("Book This room");
        bookButton.setBackground(new Color(0x1B877A)); // สีเขียวเข้ม
        bookButton.setForeground(Color.WHITE);
        // สามารถเพิ่มไอคอนได้ด้วย: bookButton.setIcon(new ImageIcon("path/to/plus-icon.png"));

        JButton editButton = new JButton("Edit My Booking");
        editButton.setBackground(new Color(0xE7E7C5)); // สีครีม

        JButton exportButton = new JButton("Export");
        exportButton.setBackground(new Color(0xD6A6A8)); // สีชมพูอ่อน

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(242, 73, 73));

        // จัดสไตล์ปุ่มให้เหมือนกัน
        styleButton(bookButton);
        styleButton(editButton);
        styleButton(exportButton);
        styleButton(logoutButton);

        // เพิ่ม Components เข้าไปใน Panel ซ้าย
        panel.add(roomSelector);
        panel.add(Box.createRigidArea(new Dimension(0, 15))); // เพิ่มช่องว่าง
        panel.add(bookButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(editButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(exportButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(logoutButton);
        panel.add(Box.createVerticalGlue());
        roomSelector.addActionListener(e -> {
            this.classroom = (Classroom) roomSelector.getSelectedItem();
            //มาเพิ่มโค้ดตรงนี้สำหรับเปลี่ยนตารางสอนของแต่ละห้อง
            System.out.println("Selected classroom: " + (this.classroom != null ? this.classroom.getName() : "None"));
            refreshSchedule();
        });

        if (!classrooms.isEmpty()) {
            this.classroom = classrooms.get(0);
        }

        bookButton.addActionListener(e -> {
            System.out.println("เปิดเมนูการจอง...");
            if (this.classroom != null) {
                new BookRoomUI(teacher, reservationSystem, this.classroom).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a classroom first.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        editButton.addActionListener(e -> {
            if (this.classroom == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select a classroom from the dropdown list first.",
                        "No Classroom Selected",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<Booking> allBookings = this.classroom.getBookings();
            List<Booking> teacherBookings = allBookings.stream().filter(b -> b.getTeacher().equals(this.teacher)).sorted(Comparator.comparing(Booking::getDate)).toList();; // Sort them by date.collect(Collectors.toList());

            if (teacherBookings.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "You have no bookings in " + this.classroom.getName() + " to edit.",
                        "No Bookings Found",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JPanel dialogPanel = new JPanel(new BorderLayout(5, 5));
            dialogPanel.add(new JLabel("Please select the booking you want to edit:"), BorderLayout.NORTH);

            class BookingWrapper {
                private Booking booking;

                public BookingWrapper(Booking booking) {
                    this.booking = booking;
                }

                public Booking getBooking() {
                    return booking;
                }

                @Override
                public String toString() {
                    return String.format("%s (%s) | %s | %s",
                            booking.getCourse(),
                            booking.getCode(),
                            booking.getDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                            booking.getTimeSlot().toString()
                    );
                }
            }

            Vector<BookingWrapper> bookingVector = new Vector<>();
            for (Booking b : teacherBookings) {
                bookingVector.add(new BookingWrapper(b));
            }
            JComboBox<BookingWrapper> bookingComboBox = new JComboBox<>(bookingVector);
            dialogPanel.add(bookingComboBox, BorderLayout.CENTER);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    dialogPanel,
                    "Select Booking",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                BookingWrapper selectedWrapper = (BookingWrapper) bookingComboBox.getSelectedItem();
                if (selectedWrapper != null) {
                    Booking selectedBooking = selectedWrapper.getBooking();

                    new EditUI(this.teacher, this.reservationSystem, this.classroom, selectedBooking, this).setVisible(true);
                    this.dispose(); // Close the ReservationUI
                }
            }
        });
        exportButton.addActionListener(e -> {
            if (this.classroom == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select a classroom from the dropdown list first.",
                        "No Classroom Selected",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Export exporter = new Export(this.teacher, this.reservationSystem, this.classroom);
            exporter.generateExcel();
        });

        logoutButton.addActionListener(e -> {
            // Logic to go back to LoginUI
            new LoginUI(new AuthSystem(), reservationSystem).setVisible(true);
            this.dispose();
        });

        return panel;
    }

    /**
     * เมธอดสำหรับจัดสไตล์ปุ่ม
     */
    private void styleButton(JButton button) {
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setHorizontalAlignment(SwingConstants.LEFT); // จัดข้อความชิดซ้าย
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
    }

    /**
     * เมธอดสำหรับสร้าง Panel ตารางเวลาด้านขวา
     */
    private JPanel createSchedulerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // ส่วนหัว (Header)
        JLabel headerLabel = new JLabel("< >  Month/Year");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(headerLabel, BorderLayout.NORTH);

        // ส่วนตาราง (Grid)
        JPanel gridPanel = new JPanel();
        int rows = 7; // 1 แถวสำหรับหัวข้อเวลา + 6 แถวสำหรับวัน
        int cols = 12; // 1 คอลัมน์สำหรับวัน + 11 คอลัมน์สำหรับเวลา
        gridPanel.setLayout(new GridLayout(rows, cols));

        // ข้อมูลสำหรับหัวตาราง
        String[] timeSlots = {
                "Day/Time", "8.00-9.00", "9.00-10.00", "10.00-11.00", "11.00-12.00",
                "12.00-13.00", "13.00-14.00", "14.00-15.00", "15.00-16.00",
                "16.00-17.00", "17.00-18.00", "18.00-19.00"
        };
        String[] days = {"MON", "TUE", "WED", "THU", "FRI", "SAT"};

        // Border สำหรับช่องตาราง
        Border cellBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JPanel cell = new JPanel();
                cell.setBorder(cellBorder);

                if (i == 0) { // แถวแรกเป็นหัวข้อเวลา
                    cell.setBackground(new Color(0xF5F5F5)); // สีเทาอ่อน
                    JLabel timeLabel = new JLabel(timeSlots[j], SwingConstants.CENTER);
                    timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    cell.add(timeLabel);
                } else if (j == 0) { // คอลัมน์แรกเป็นวัน
                    cell.setBackground(new Color(0xF5F5F5));
                    JLabel dayLabel = new JLabel(days[i-1], SwingConstants.CENTER);
                    dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    cell.add(dayLabel);
                } else { // ช่องตารางเปล่าๆ
                    cell.setBackground(Color.WHITE);
                }
                gridPanel.add(cell);
            }
        }

        panel.add(gridPanel, BorderLayout.CENTER);
        return panel;
    }
    public void refreshSchedule() {
        System.out.println("Refreshing schedule...");
        if (centerPanel != null) {
            mainPanel.remove(centerPanel);
        }
        this.centerPanel = createSchedulerPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}

