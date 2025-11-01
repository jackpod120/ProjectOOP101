package ClassroomProject;
import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Objects;


public class BookRoomUI extends JFrame {

    private final Teacher teacher;
    private final Classroom classroom;
    private final JComboBox<String> startTimeBox;
    private final JComboBox<String> endTimeBox;

    public BookRoomUI(Teacher teacher, ReservationSystem reservationSystem, Classroom classroom) {
        this.teacher = teacher;
        this.classroom = classroom;
        setTitle("Book a Room");
        setSize(350, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true));
        card.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 10, 8, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel header = new JLabel("Book a room");
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        card.add(header, c);

        JLabel subHeader = new JLabel("Enter your information below to Book a room");
        subHeader.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subHeader.setForeground(Color.GRAY);
        c.gridy++;
        card.add(subHeader, c);

        c.gridwidth = 1;
        c.gridy++;
        card.add(new JLabel("Course : "), c);
        JTextField courseField = new JTextField();
        c.gridx = 1;
        card.add(courseField, c);

        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Code : "), c);
        JTextField codeField = new JTextField();
        c.gridx = 1;
        card.add(codeField, c);

        String[] timeSlots = {
                "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00"
        };

        // Start Time
        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Start Time:"), c);
        startTimeBox = new JComboBox<>(timeSlots);
        c.gridx = 1;
        card.add(startTimeBox, c);

        // End Time
        c.gridx = 0; c.gridy++;
        card.add(new JLabel("End Time:"), c);
        endTimeBox = new JComboBox<>(timeSlots);
        c.gridx = 1;
        card.add(endTimeBox, c);

        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Booking Type:"), c);
        JComboBox<ReservationType> typeBox = new JComboBox<>(ReservationType.values());
        c.gridx = 1;
        card.add(typeBox, c);

        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Day:"), c);
        JComboBox<DayOfWeek> dayOfWeekBox = new JComboBox<>(DayOfWeek.values());
        dayOfWeekBox.removeItem(DayOfWeek.SUNDAY);
        c.gridx = 1;
        card.add(dayOfWeekBox, c);

        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Start Month:"), c);
        JComboBox<Month> monthBox = new JComboBox<>(Month.values());
        monthBox.setSelectedItem(LocalDate.now().getMonth()); // Default to current month
        c.gridx = 1;
        card.add(monthBox, c);

        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Start Year:"), c);
        int currentYear = LocalDate.now().getYear();
        JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(currentYear, currentYear - 1, currentYear + 10, 1));
        yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "#")); // No commas
        c.gridx = 1;
        card.add(yearSpinner, c);

        // Confirm button
        c.gridx = 0; c.gridy++;
        c.gridwidth = 2;
        JButton confirmBtn = new JButton("confirm");
        confirmBtn.setBackground(new Color(0, 102, 91));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        card.add(confirmBtn, c);
        confirmBtn.addActionListener(_ -> {
            System.out.println("กำลังพยายามจอง...");
            boolean isAvailable = true;
            TimeSlot timeSlot;
            int yearSelected = (Integer) yearSpinner.getValue();
            Month monthSelected = (Month) monthBox.getSelectedItem();

            DayOfWeek dayOfWeek = (DayOfWeek) Objects.requireNonNull(dayOfWeekBox.getSelectedItem());
            String startString = (String) Objects.requireNonNull(startTimeBox.getSelectedItem());
            String endString = (String) Objects.requireNonNull(endTimeBox.getSelectedItem());

            LocalTime timeTimeSlotStart = LocalTime.parse(startString);
            LocalTime timeTimeSlotEnd = LocalTime.parse(endString);

            ReservationType reservationType = (ReservationType) Objects.requireNonNull(typeBox.getSelectedItem());
            String course = courseField.getText().trim();
            String code = codeField.getText().trim();

            if (timeTimeSlotStart.isAfter(timeTimeSlotEnd) || timeTimeSlotStart.equals(timeTimeSlotEnd)) {
                JOptionPane.showMessageDialog(this, "Start time must be before end time.", "Time Error", JOptionPane.ERROR_MESSAGE);
                isAvailable = false;
            }

            if (course.isEmpty()) {
                isAvailable = false;
                System.out.println("❌ เกิดข้อผิดพลาด, โปรดใส่ชื่อวิชา");
                JOptionPane.showMessageDialog(this, "Please enter your course's name you want to book.", "Error", JOptionPane.ERROR_MESSAGE);

            }
            if (code.isEmpty()) {
                isAvailable = false;
                System.out.println("❌ เกิดข้อผิดพลาด, โปรดใส่รหัสวิชา");
                JOptionPane.showMessageDialog(this, "Please enter your course's code you want to book.", "Error", JOptionPane.ERROR_MESSAGE);

            }

            if (isAvailable) {
                timeSlot = new TimeSlot(dayOfWeek, timeTimeSlotStart, timeTimeSlotEnd);

                if (reservationSystem.makeReservation(this.teacher, this.classroom, timeSlot, reservationType, yearSelected, monthSelected, course, code)) {
                    this.classroom.displaySchedule();
                    new ReservationUI(teacher, reservationSystem).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Sorry, your time is already in use.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton goBackButton = new JButton("< Go Back");
        goBackButton.setFont(new Font("SansSerif", Font.BOLD, 10));
        goBackButton.setBackground(new Color(230, 230, 230));
        c.gridx = 0; c.gridy++; c.gridwidth = 1;
        card.add(goBackButton, c);
        goBackButton.addActionListener(_ -> {
            new ReservationUI(teacher, reservationSystem).setVisible(true);
            this.dispose();
        });

        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(card, gbc);
        add(mainPanel);
        setVisible(true);
    }

}

