package ClassroomProject;

import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class EditUI extends JFrame {

    private final Teacher teacher;
    private final ReservationSystem reservationSystem;
    private final Classroom classroom;
    private final Booking bookingToEdit;
    private final ReservationUI reservationUI; // To go back

    // UI Components
    private JComboBox<DayOfWeek> dayOfWeekBox;
    private JComboBox<String> startTimeBox;
    private JComboBox<String> endTimeBox;
    private JTextField courseField;
    private JTextField codeField;

    public EditUI(Teacher teacher, ReservationSystem reservationSystem, Classroom classroom, Booking bookingToEdit, ReservationUI reservationUI) {
        this.teacher = teacher;
        this.reservationSystem = reservationSystem;
        this.classroom = classroom;
        this.bookingToEdit = bookingToEdit;
        this.reservationUI = reservationUI; // Save the reference

        setTitle("Edit Booking");
        setSize(450, 500);
        // Dispose this window, don't exit the whole app
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Card-like panel
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true));
        card.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 10, 8, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Header
        JLabel header = new JLabel("Edit Booking");
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        card.add(header, c);

        // Sub-header with Classroom and Date
        String dateStr = bookingToEdit.getDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        JLabel subHeader = new JLabel("Editing for " + classroom.getName() + " on " + dateStr);
        subHeader.setFont(new Font("SansSerif", Font.PLAIN, 12));
        c.gridy++;
        card.add(subHeader, c);

        // Reset gridwidth
        c.gridwidth = 1;

        // Day of Week
        c.gridy++;
        card.add(new JLabel("Day:"), c);
        dayOfWeekBox = new JComboBox<>(DayOfWeek.values());
        dayOfWeekBox.setSelectedItem(bookingToEdit.getTimeSlot().getDayOfWeek());
        c.gridx = 1;
        card.add(dayOfWeekBox, c);

        // Time selection
        String[] timeSlots = {
                "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00"
        };

        // Start Time
        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Start Time:"), c);
        startTimeBox = new JComboBox<>(timeSlots);
        startTimeBox.setSelectedItem(bookingToEdit.getTimeSlot().getStartTime().toString());
        c.gridx = 1;
        card.add(startTimeBox, c);

        // End Time
        c.gridx = 0; c.gridy++;
        card.add(new JLabel("End Time:"), c);
        endTimeBox = new JComboBox<>(timeSlots);
        endTimeBox.setSelectedItem(bookingToEdit.getTimeSlot().getEndTime().toString());
        c.gridx = 1;
        card.add(endTimeBox, c);

        // Course Name
        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Course Name:"), c);
        courseField = new JTextField(bookingToEdit.getCourse());
        c.gridx = 1;
        card.add(courseField, c);

        // Course Code
        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Course Code:"), c);
        codeField = new JTextField(bookingToEdit.getCode());
        c.gridx = 1;
        card.add(codeField, c);

        // --- Buttons ---
        c.gridx = 0; c.gridy++; c.gridwidth = 2; c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        // Save Changes Button
        JButton saveButton = new JButton("Save Changes");
        saveButton.setBackground(new Color(60, 179, 113)); // Green
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        buttonPanel.add(saveButton);

        // Delete Booking Button
        JButton deleteButton = new JButton("Delete Booking");
        deleteButton.setBackground(new Color(220, 20, 60)); // Red
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        buttonPanel.add(deleteButton);

        card.add(buttonPanel, c);

        // Go Back Button
        JButton goBackButton = new JButton("< Go Back");
        goBackButton.setFont(new Font("SansSerif", Font.BOLD, 10));
        goBackButton.setBackground(new Color(230, 230, 230));
        c.gridy++; c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(20, 10, 8, 10);
        card.add(goBackButton, c);

        // --- Action Listeners ---

        goBackButton.addActionListener(e -> {
            reservationUI.setVisible(true); // Show the main schedule again
            this.dispose(); // Close this edit window
        });

        saveButton.addActionListener(e -> onSaveChanges());

        deleteButton.addActionListener(e -> onDeleteBooking());

        // Add card to main panel
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(card, gbc);
        add(mainPanel);
    }

    private void onSaveChanges() {
        // 1. Get new values from the UI
        DayOfWeek newDay = (DayOfWeek) dayOfWeekBox.getSelectedItem();
        LocalTime newStartTime = LocalTime.parse((String) startTimeBox.getSelectedItem());
        LocalTime newEndTime = LocalTime.parse((String) endTimeBox.getSelectedItem());
        String newCourse = courseField.getText().trim();
        String newCode = codeField.getText().trim();

        // 2. Validate input
        if (newStartTime.isAfter(newEndTime) || newStartTime.equals(newEndTime)) {
            JOptionPane.showMessageDialog(this, "Start time must be before end time.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (newCourse.isEmpty() || newCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Course Name and Code cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Create new TimeSlot and check for availability
        TimeSlot newTimeSlot = new TimeSlot(newDay, newStartTime, newEndTime);
        LocalDate date = bookingToEdit.getDate(); // The date stays the same

        // Temporarily remove the *old* booking to check if the *new* time is free
        classroom.removeBooking(bookingToEdit);

        if (classroom.isAvailable(date, newTimeSlot)) {
            // Success! The new time is available.
            // Create a new booking object
            Booking newBooking = new Booking(teacher, date, newTimeSlot, newCourse, newCode);

            // Add the new booking
            classroom.addBooking(newBooking);

            JOptionPane.showMessageDialog(this, "Booking updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Refresh the main schedule and go back
            reservationUI.refreshSchedule();
            reservationUI.setVisible(true);
            this.dispose();
        } else {
            // Failed. The new time conflicts with *another* booking.
            // Add the original booking back!
            classroom.addBooking(bookingToEdit);
            JOptionPane.showMessageDialog(this, "The new time conflicts with another booking.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDeleteBooking() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this booking?\n" +
                        bookingToEdit.getDate() + " @ " + bookingToEdit.getTimeSlot().getStartTime(),
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            // User confirmed. Remove the booking.
            if (classroom.removeBooking(bookingToEdit)) {
                JOptionPane.showMessageDialog(this, "Booking successfully deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Refresh the main schedule and go back
                reservationUI.refreshSchedule();
                reservationUI.setVisible(true);
                this.dispose();
            } else {
                // This shouldn't happen, but good to check
                JOptionPane.showMessageDialog(this, "Error: Could not delete booking.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}