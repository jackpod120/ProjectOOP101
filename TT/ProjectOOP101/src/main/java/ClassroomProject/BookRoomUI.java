package ClassroomProject;
import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;
import java.util.Vector;


public class BookRoomUI extends JFrame {

    private final Teacher teacher;
    private final Classroom classroom;
    private final JComboBox<String> startTimeBox;
    private final JComboBox<String> endTimeBox;
    private final JComboBox<Month> monthBox;
    private final JComboBox<DayOfWeek> dayOfWeekBox;
    private final JComboBox<ReservationType> typeBox;
    private final JSpinner yearSpinner;
    private final JComboBox<DateWrapper> dateBox;
    private final JLabel dateLabel;

    private static class DateWrapper {
        private final int day;
        private final String text;
        public DateWrapper(int day, String text) {
            this.day = day;
            this.text = text;
        }
        public int getDay() { return day; }
        @Override public String toString() { return text; }
    }


    public BookRoomUI(Teacher teacher, ReservationSystem reservationSystem, Classroom classroom) {
        this.teacher = teacher;
        this.classroom = classroom;
        setTitle("Book a Room");
        setSize(350, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
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

        JLabel subHeader = new JLabel("Classroom: " + classroom.getName());
        subHeader.setFont(new Font("SansSerif", Font.PLAIN, 12));
        c.gridy++;
        card.add(subHeader, c);

        c.gridwidth = 1;

        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Year:"), c);
        int currentYear = LocalDate.now().getYear();
        yearSpinner = new JSpinner(new SpinnerNumberModel(currentYear, currentYear, currentYear + 10, 1));
        yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "#")); // No commas
        c.gridx = 1;
        card.add(yearSpinner, c);

        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Month:"), c);
        monthBox = new JComboBox<>(Month.values());
        monthBox.setSelectedItem(LocalDate.now().getMonth());
        c.gridx = 1;
        card.add(monthBox, c);

        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Day:"), c);
        dayOfWeekBox = new JComboBox<>(DayOfWeek.values());
        dayOfWeekBox.setSelectedItem(LocalDate.now().getDayOfWeek());
        dayOfWeekBox.removeItem(DayOfWeek.SUNDAY);
        c.gridx = 1;
        card.add(dayOfWeekBox, c);

        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Type:"), c);
        typeBox = new JComboBox<>(ReservationType.values());
        c.gridx = 1;
        card.add(typeBox, c);

        dateLabel = new JLabel("Date:");
        c.gridx = 0; c.gridy++;
        card.add(dateLabel, c);
        dateBox = new JComboBox<>();
        c.gridx = 1;
        card.add(dateBox, c);

        String[] timeSlots = { "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00" };
        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Start Time:"), c);
        startTimeBox = new JComboBox<>(timeSlots);
        c.gridx = 1;
        card.add(startTimeBox, c);

        c.gridx = 0; c.gridy++;
        card.add(new JLabel("End Time:"), c);
        endTimeBox = new JComboBox<>(timeSlots);
        c.gridx = 1;
        card.add(endTimeBox, c);

        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Course Name:"), c);
        JTextField courseField = new JTextField();
        c.gridx = 1;
        card.add(courseField, c);

        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Course Code:"), c);
        JTextField codeField = new JTextField();
        c.gridx = 1;
        card.add(codeField, c);

        c.gridx = 0; c.gridy++; c.gridwidth = 2; c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        JButton bookButton = new JButton("Book Now");
        bookButton.setBackground(new Color(52, 152, 219));
        bookButton.setForeground(Color.WHITE);
        bookButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        card.add(bookButton, c);

        Runnable dateUpdater = this::updateDateComboBox;
        yearSpinner.addChangeListener(e -> dateUpdater.run());
        monthBox.addActionListener(e -> dateUpdater.run());
        dayOfWeekBox.addActionListener(e -> dateUpdater.run());
        typeBox.addActionListener(e -> dateUpdater.run());

        bookButton.addActionListener(e -> {
            boolean isAvailable = true;
            TimeSlot timeSlot;

            DayOfWeek dayOfWeek = (DayOfWeek) Objects.requireNonNull(dayOfWeekBox.getSelectedItem());
            LocalTime timeTimeSlotStart = LocalTime.parse((String) Objects.requireNonNull(startTimeBox.getSelectedItem()));
            LocalTime timeTimeSlotEnd = LocalTime.parse((String) Objects.requireNonNull(endTimeBox.getSelectedItem()));
            ReservationType reservationType = (ReservationType) Objects.requireNonNull(typeBox.getSelectedItem());

            Month monthSelected = (Month) Objects.requireNonNull(monthBox.getSelectedItem());
            int yearSelected = (Integer) yearSpinner.getValue();

            DateWrapper selectedWrapper = (DateWrapper) dateBox.getSelectedItem();
            if (selectedWrapper == null) {
                JOptionPane.showMessageDialog(this, "There are no valid " + dayOfWeek + "s in " + monthSelected + " " + yearSelected + ".", "Date Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int daySelected = selectedWrapper.getDay();

            String course = courseField.getText().trim();
            String code = codeField.getText().trim();

            if (timeTimeSlotStart.isAfter(timeTimeSlotEnd) || timeTimeSlotStart.equals(timeTimeSlotEnd)) {
                isAvailable = false;
                JOptionPane.showMessageDialog(this, "Start time must be before end time.", "Error", JOptionPane.ERROR_MESSAGE);
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

                if (reservationSystem.makeReservation(this.teacher, this.classroom, timeSlot, reservationType, yearSelected, monthSelected, daySelected, course, code)) {
                    this.classroom.displaySchedule();
                    new ReservationUI(teacher, reservationSystem).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Sorry, your time is already in use or the date is invalid.", "Error", JOptionPane.ERROR_MESSAGE);
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

        updateDateComboBox();

        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(card, gbc);
        add(mainPanel);
    }

    private void updateDateComboBox() {
        int year = (Integer) yearSpinner.getValue();
        Month month = (Month) monthBox.getSelectedItem();
        DayOfWeek day = (DayOfWeek) dayOfWeekBox.getSelectedItem();
        ReservationType type = (ReservationType) typeBox.getSelectedItem();

        if (month == null || day == null) return;

        Vector<DateWrapper> dates = new Vector<>();

        if (type == ReservationType.DAILY) {
            dateLabel.setText("Date:");
        } else {
            dateLabel.setText("Start Date:");
            dates.add(new DateWrapper(0, "All " + day + "s"));
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate date = yearMonth.atDay(1).with(TemporalAdjusters.firstInMonth(day));

        while (date.getMonth() == month) {
            dates.add(new DateWrapper(date.getDayOfMonth(), String.valueOf(date.getDayOfMonth())));
            date = date.with(TemporalAdjusters.next(day));
        }

        dateBox.setModel(new DefaultComboBoxModel<>(dates));
    }
}