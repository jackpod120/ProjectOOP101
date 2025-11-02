package ClassroomProject;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate; // 🟢 เพิ่ม import
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters; // 🟢 เพิ่ม import
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class ReservationUI extends JFrame {
    private final Teacher teacher;
    private final ReservationSystem reservationSystem;
    private Classroom classroom;
    private final JPanel mainPanel;
    private JPanel centerPanel;
    // 🟢 FIX: เพิ่มตัวแปรสำหรับวันที่ปัจจุบัน (เพื่อใช้ในการคำนวณสัปดาห์ปัจจุบัน)
    private LocalDate currentDate = LocalDate.now();

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public ReservationUI(Teacher teacher, ReservationSystem reservationSystem) {
        super("Classroom Reservation System");
        this.teacher = teacher;
        this.reservationSystem = reservationSystem;

        // 🟢 โหลดการจองทั้งหมดของอาจารย์นี้ทันทีที่เข้าสู่ระบบ (เรียกแค่ครั้งเดียว)
        // เพื่อให้แน่ใจว่าตารางสอนเป็นปัจจุบัน
        new DataManager().loadBookings(teacher, reservationSystem);

        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        this.mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // 🟢 FIX: ตั้งค่าห้องเริ่มต้นก่อนที่จะวาดตาราง
        if (this.classroom == null && !reservationSystem.getClassrooms().isEmpty()) {
            this.classroom = reservationSystem.getClassrooms().get(0);
        } else if (this.classroom == null) {
            // กรณีไม่มีห้องเรียนเลย
            JOptionPane.showMessageDialog(this, "No classrooms registered in the system.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel leftPanel = createLeftControlPanel();
        mainPanel.add(leftPanel, BorderLayout.WEST);

        this.centerPanel = createSchedulerPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createLeftControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(180, 0));

        JComboBox<Classroom> roomSelector = new JComboBox<>();
        List<Classroom> classrooms = reservationSystem.getClassrooms();
        if (classrooms != null && !classrooms.isEmpty()) {
            for (Classroom classroom : classrooms) {
                roomSelector.addItem(classroom);
            }
            if (this.classroom != null) {
                roomSelector.setSelectedItem(this.classroom);
            } else {
                this.classroom = classrooms.get(0);
            }
        }
        roomSelector.setMaximumSize(new Dimension(Integer.MAX_VALUE, roomSelector.getPreferredSize().height));

        JButton bookButton = new JButton("Book This room");
        bookButton.setBackground(new Color(0x1B877A));
        bookButton.setForeground(Color.WHITE);

        JButton editButton = new JButton("Edit My Booking");
        editButton.setBackground(new Color(0xE7E7C5));

        JButton exportButton = new JButton("Export");
        exportButton.setBackground(new Color(0xD6A6A8));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(242, 73, 73));
        logoutButton.setForeground(Color.WHITE);

        styleButton(bookButton);
        styleButton(editButton);
        styleButton(exportButton);
        styleButton(logoutButton);

        panel.add(roomSelector);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
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
            System.out.println("Selected classroom: " + (this.classroom != null ? this.classroom.getName() : "None"));
            refreshSchedule();
        });

        bookButton.addActionListener(e -> {
            System.out.println("เปิดเมนูการจอง...");
            if (this.classroom != null) {
                // 🟢 FIX: เพิ่ม reservationSystem ใน BookRoomUI constructor (ถ้ายังไม่มี)
                // new BookRoomUI(teacher, reservationSystem, this.classroom).setVisible(true);
                // ต้องตรวจสอบ BookRoomUI constructor อีกครั้ง
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
            List<Booking> teacherBookings = allBookings.stream()
                    .filter(b -> b.getTeacher().equals(this.teacher))
                    .sorted(Comparator.comparing(Booking::getDate))
                    .collect(Collectors.toList());

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
                            booking.getDate().format(dateFormatter),
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
                    // this.dispose(); // ห้าม dispose
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
            new LoginUI(new AuthSystem(reservationSystem), reservationSystem).setVisible(true);
            this.dispose();
        });

        return panel;
    }

    private void styleButton(JButton button) {
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
    }


    /**
     * 🟢 FIX: แก้ไขเมธอดนี้เพื่อป้องกัน DayOfWeek.valueOf("MON")
     */
    private JPanel createSchedulerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // 🟢 FIX: อัปเดต Header ให้แสดงชื่อห้องที่เลือกและวันที่ปัจจุบัน
        LocalDate startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = currentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

        String headerText = String.format("Schedule for: %s | Week: %s - %s",
                (this.classroom != null ? this.classroom.getName() : "No Room Selected"),
                startOfWeek.format(DateTimeFormatter.ofPattern("dd/MM")),
                endOfWeek.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );
        JLabel headerLabel = new JLabel(headerText);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(headerLabel, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel();
        int rows = 7; // 1 หัวข้อ + 6 วัน (จันทร์-เสาร์)
        int cols = 12; // 1 หัวข้อ + 11 เวลา (8:00 - 19:00)
        gridPanel.setLayout(new GridLayout(rows, cols));

        String[] timeSlots = {
                "Day/Time", "8.00-9.00", "9.00-10.00", "10.00-11.00", "11.00-12.00",
                "12.00-13.00", "13.00-14.00", "14.00-15.00", "15.00-16.00",
                "16.00-17.00", "17.00-18.00", "18.00-19.00"
        };
        String[] days = {"MON", "TUE", "WED", "THU", "FRI", "SAT"}; // ชื่อย่อสำหรับแสดงผล

        // 🟢 FIX 1: สร้าง Array ของ DayOfWeek Enum ตัวเต็ม เพื่อใช้ในการค้นหาการจอง (แก้ Error)
        DayOfWeek[] dayEnumValues = {
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
        };

        Border cellBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY);

        List<Booking> bookings = (this.classroom != null) ? this.classroom.getBookings() : List.of();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JPanel cell = new JPanel();
                cell.setBorder(cellBorder);

                if (i == 0) { // แถวแรก (หัวข้อเวลา)
                    cell.setBackground(new Color(0xF5F5F5));
                    JLabel timeLabel = new JLabel(timeSlots[j], SwingConstants.CENTER);
                    timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    cell.add(timeLabel);

                } else if (j == 0) { // คอลัมน์แรก (วัน)
                    cell.setBackground(new Color(0xF5F5F5));
                    JLabel dayLabel = new JLabel(days[i - 1], SwingConstants.CENTER);
                    dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    cell.add(dayLabel);

                } else { // ช่องข้อมูล (ตารางเวลา)

                    // 1. หาว่าช่องนี้คือวันอะไร เวลาอะไร
                    // 🟢 FIX 2: ใช้ Array Enum ที่ถูกต้องแทนการใช้ .valueOf()
                    DayOfWeek cellDay = dayEnumValues[i - 1]; // i=1 คือ MONDAY (Index 0)

                    LocalTime cellStartTime = LocalTime.of(j + 7, 0); // j=1 คือ 8:00
                    LocalTime cellEndTime = LocalTime.of(j + 8, 0);   // j=1 คือ 9:00

                    // 2. ค้นหาการจองที่ทับซ้อนกับช่องนี้
                    Booking foundBooking = null;

                    // 🟢 FIX 3: คำนวณวันที่ที่ถูกต้องสำหรับช่องตารางนี้ (ในสัปดาห์ปัจจุบัน)
                    LocalDate targetDate = startOfWeek.with(TemporalAdjusters.nextOrSame(cellDay));


                    for (Booking b : bookings) {
                        TimeSlot bookedSlot = b.getTimeSlot();

                        // 🟢 FIX 4: เช็คทั้งวัน (Date) และเวลาทับซ้อน (TimeSlot)
                        if (b.getDate().isEqual(targetDate) && bookedSlot.overlapsWith(new TimeSlot(cellDay, cellStartTime, cellEndTime)))
                        {
                            foundBooking = b;
                            break;
                        }

                        // NOTE: ถ้า TimeSlot.overlapsWith() ทำงานถูกต้องตามเงื่อนไข (Start < End && OtherStart < ThisEnd)
                        // จะสามารถใช้แบบด้านล่างนี้ได้
                        /*
                        if (b.getDate().isEqual(targetDate) &&
                            bookedSlot.getStartTime().isBefore(cellEndTime) &&
                            bookedSlot.getEndTime().isAfter(cellStartTime)) {
                                foundBooking = b;
                                break;
                        }
                        */
                    }

                    // 3. แสดงผลตามการจองที่พบ
                    if (foundBooking != null) {
                        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
                        cell.setAlignmentX(Component.CENTER_ALIGNMENT);

                        JLabel courseLabel = new JLabel(foundBooking.getCourse());
                        courseLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
                        JLabel teacherLabel = new JLabel(foundBooking.getTeacher().getName());
                        teacherLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));

                        // ไฮไลต์การจองของตัวเอง
                        if (foundBooking.getTeacher().equals(this.teacher)) {
                            cell.setBackground(new Color(0x1B877A)); // สีเขียว
                            courseLabel.setForeground(Color.WHITE);
                            teacherLabel.setForeground(Color.WHITE);

                            // 🟢 เพิ่ม MouseListener สำหรับ Edit/Delete
                            Booking finalFoundBooking = foundBooking;
                            cell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                            cell.addMouseListener(new java.awt.event.MouseAdapter() {
                                @Override
                                public void mouseClicked(java.awt.event.MouseEvent evt) {
                                    new EditUI(teacher, reservationSystem, classroom, finalFoundBooking, ReservationUI.this).setVisible(true);
                                    ReservationUI.this.dispose();
                                }
                            });
                        } else {
                            cell.setBackground(new Color(0xD6A6A8)); // สีชมพู (คนอื่นจอง)
                            courseLabel.setForeground(Color.BLACK);
                            teacherLabel.setForeground(Color.BLACK);
                            cell.setToolTipText("Booked by: " + foundBooking.getTeacher().getName() + " (" + foundBooking.getCourse() + ")");
                        }

                        cell.add(Box.createVerticalStrut(5));
                        cell.add(courseLabel);
                        cell.add(teacherLabel);

                    } else {
                        // ถ้าไม่เจอ: ช่องสีขาวปกติ & เพิ่ม Listener สำหรับการจองใหม่
                        cell.setBackground(Color.WHITE);
                        cell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        DayOfWeek finalCellDay = cellDay;
                        LocalTime finalCellStartTime = cellStartTime;

                        cell.addMouseListener(new java.awt.event.MouseAdapter() {
                            @Override
                            public void mouseClicked(java.awt.event.MouseEvent evt) {
                                // 🟢 เปิดหน้า BookRoomUI
                                // NOTE: หากต้องการเปิด BookRoomUI แบบมีวันและเวลาเริ่มต้นกำหนดไว้
                                // ต้องเพิ่มเมธอด openBookRoomUI และ constructor ใน BookRoomUI
                                // เนื่องจากโค้ดนี้ไม่มีเมธอด openBookRoomUI ผมจะใช้ BookRoomUI constructor ปกติ
                                if (classroom != null) {
                                    new BookRoomUI(teacher, reservationSystem, classroom).setVisible(true);
                                    ReservationUI.this.dispose();
                                }
                            }
                        });
                    }
                }
                gridPanel.add(cell);
            }
        }

        panel.add(gridPanel, BorderLayout.CENTER);
        return panel;
    }


    public void refreshSchedule() {
        System.out.println("Refreshing schedule for: " + (this.classroom != null ? this.classroom.getName() : "None"));
        if (centerPanel != null) {
            mainPanel.remove(centerPanel);
        }

        this.centerPanel = createSchedulerPanel();

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}