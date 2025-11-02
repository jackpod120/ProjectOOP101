package ClassroomProject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class ReservationSystem {
    private List<Classroom> classrooms = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();
    private DataManager dataManager = new DataManager();


    public ReservationSystem() {
        this.classrooms.add(new Classroom("Room 101"));
        this.classrooms.add(new Classroom("Room 102"));
        this.classrooms.add(new Classroom("Room 205"));
        this.classrooms.add(new Classroom("Computer Lab"));
    }

    public void addClassroom(Classroom classroom) {
        this.classrooms.add(classroom);
    }

    public Classroom findClassroomByName(String name) {
        for(Classroom room : classrooms){
            if(room.getName().equals(name)){
                return room;
            }
        }
        return null;
    }
    public List<Classroom> getClassrooms() {
        return classrooms;
    }

    public boolean makeReservation(Teacher teacher, Classroom classroom, TimeSlot timeSlot, ReservationType type, int year, Month month, int day, String course, String code) {
        List<LocalDate> datesToBook = new ArrayList<>();
        DayOfWeek selectedDayOfWeek = timeSlot.getDayOfWeek();

        switch (type) {

            case DAILY:
                try {
                    LocalDate specificDate = LocalDate.of(year, month, day);

                    if (specificDate.getDayOfWeek() != selectedDayOfWeek) {
                        System.err.println("❌ Error: The selected date " + specificDate + " is not a " + selectedDayOfWeek);
                        // ถ้าเป็นการจอง DAILY เราจะบังคับให้วันที่และวันในสัปดาห์ตรงกัน
                        return false;
                    }
                    datesToBook.add(specificDate);
                } catch (Exception e) {
                    System.err.println("❌ Error: Invalid date created: " + year + "-" + month + "-" + day);
                    return false;
                }
                break;

            case MONTHLY:
                YearMonth yearMonth = YearMonth.of(year, month);
                LocalDate dayInMonth;

                if (day > 0) {
                    // ถ้าผู้ใช้เลือกวันที่เริ่มต้น (เช่น 15) ให้เริ่มจากวันนั้น
                    dayInMonth = LocalDate.of(year, month, day);
                    if(dayInMonth.getDayOfWeek() != selectedDayOfWeek) {
                        // หากวันที่เลือกไม่ตรงกับ DayOfWeek ที่เลือก ให้หาวันที่ถูกต้องวันแรก
                        dayInMonth = dayInMonth.with(TemporalAdjusters.nextOrSame(selectedDayOfWeek));
                    }
                } else {
                    // ถ้าผู้ใช้ไม่ได้เลือกวันที่ (day=0) ให้เริ่มจากวันแรกในเดือน
                    dayInMonth = yearMonth.atDay(1).with(TemporalAdjusters.firstInMonth(selectedDayOfWeek));
                }

                while (dayInMonth.getMonth() == month) {
                    datesToBook.add(dayInMonth);
                    dayInMonth = dayInMonth.with(TemporalAdjusters.next(selectedDayOfWeek));
                }
                break;

            case TERM:
                YearMonth startYearMonth = YearMonth.of(year, month);
                LocalDate termDay = null;

                if (day > 0) {
                    // ถ้าผู้ใช้เลือกวันที่เริ่มต้น
                    termDay = LocalDate.of(year, month, day);
                    if(termDay.getDayOfWeek() != selectedDayOfWeek) {
                        termDay = termDay.with(TemporalAdjusters.nextOrSame(selectedDayOfWeek));
                    }
                }

                for (int i = 0; i < 4; i++) { // 1 เทอม = 4 เดือน
                    YearMonth currentYearMonth = startYearMonth.plusMonths(i);
                    Month currentMonth = currentYearMonth.getMonth();

                    if (i == 0 && termDay != null) {
                        // ใช้ termDay ที่คำนวณไว้สำหรับเดือนแรก
                    } else {
                        // สำหรับเดือนถัดไป หรือถ้าไม่ได้กำหนดวันเริ่มต้น ให้เริ่มจากวันแรกของเดือน
                        termDay = currentYearMonth.atDay(1).with(TemporalAdjusters.firstInMonth(selectedDayOfWeek));
                    }

                    while (termDay.getMonth() == currentMonth) {
                        datesToBook.add(termDay);
                        termDay = termDay.with(TemporalAdjusters.next(selectedDayOfWeek));
                    }
                }
                break;
        }

        // ตรวจสอบ Availability ทั้งหมดก่อน
        for (LocalDate date : datesToBook) {
            if (!classroom.isAvailable(date, timeSlot)) {
                System.out.println("❌ การจองล้มเหลว: วันที่ " + date + " เวลา " + timeSlot + " ในห้อง " + classroom.getName() + " ไม่ว่าง");
                return false;
            }
        }

        // ถ้าว่างทั้งหมด ค่อยทำการจอง
        for (LocalDate date : datesToBook) {
            // สร้าง ReservationID ให้ไม่ซ้ำกันสำหรับแต่ละครั้งด้วยการบวก date.toString()
            String reservationID = "R" + teacher.getID() + System.nanoTime() + date.toString();
            Booking booking = new Booking(teacher, date, timeSlot, course, code, classroom.getName(), reservationID, type);

            dataManager.addBooking(teacher, booking); // 1. บันทึกลง CSV
            classroom.addBooking(booking); // 2. เพิ่มเข้าห้อง
            this.bookings.add(booking); // 3. เพิ่มเข้า List หลักของระบบ
        }

        System.out.println("✅ การจองแบบ " + type + " สำเร็จ! จำนวน " + datesToBook.size() + " ครั้ง ในห้อง " + classroom.getName());
        return true;
    }

    // 🟢 FIX: เมธอดที่ต้องอยู่ในคลาส
    public void addBookingInternal(Booking booking) {
        if (booking != null && !this.bookings.contains(booking)) {
            this.bookings.add(booking);
        }
    }

    // 🟢 FIX: เมธอดที่ต้องอยู่ในคลาส
    public boolean removeBookingInternal(Booking bookingToRemove) {
        return this.bookings.remove(bookingToRemove);
    }

    public List<Booking> getBookings() {
        return bookings;
    }
}