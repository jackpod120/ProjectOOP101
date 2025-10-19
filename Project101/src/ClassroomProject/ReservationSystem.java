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

    public boolean makeReservation(Teacher teacher, Classroom classroom, TimeSlot timeSlot, ReservationType type, int year, Month month) {
        List<LocalDate> datesToBook = new ArrayList<>();

        // 1. สร้างรายการวันที่จะจองตามประเภท
        switch (type) {
            case DAILY:
                // สำหรับรายวัน, เราจะสมมติว่าจองวันจันทร์ที่จะถึงนี้
                LocalDate nextMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
                datesToBook.add(nextMonday);
                break;
            case MONTHLY:
                YearMonth yearMonth = YearMonth.of(year, month);
                LocalDate firstDayOfMonth = yearMonth.atDay(1);
                LocalDate day = firstDayOfMonth.with(TemporalAdjusters.firstInMonth(timeSlot.getDayOfWeek()));
                while (day.getMonth() == month) {
                    datesToBook.add(day);
                    day = day.with(TemporalAdjusters.next(timeSlot.getDayOfWeek()));
                }
                break;
            case TERM:
                // สมมติว่า 1 เทอมคือ 4 เดือน (เช่น ส.ค. - พ.ย.)
                for (int i = 0; i < 4; i++) {
                    Month currentMonth = month.plus(i);
                    YearMonth termYearMonth = YearMonth.of(year, currentMonth);
                    LocalDate firstDay = termYearMonth.atDay(1);
                    LocalDate termDay = firstDay.with(TemporalAdjusters.firstInMonth(timeSlot.getDayOfWeek()));
                    while (termDay.getMonth() == currentMonth) {
                        datesToBook.add(termDay);
                        termDay = termDay.with(TemporalAdjusters.next(timeSlot.getDayOfWeek()));
                    }
                }
                break;
        }

        // 2. ตรวจสอบว่าทุกวันที่ต้องการจองว่างหรือไม่
        for (LocalDate date : datesToBook) {
            if (!classroom.isAvailable(date, timeSlot)) {
                System.out.println("❌ การจองล้มเหลว: วันที่ " + date + " เวลา " + timeSlot + " ในห้อง " + classroom.getName() + " ไม่ว่าง");
                return false;
            }
        }

        // 3. ถ้าทุกวันว่าง ให้ทำการจองทั้งหมด
        for (LocalDate date : datesToBook) {
            classroom.addBooking(new Booking(teacher, date, timeSlot));
        }

        System.out.println("✅ การจองแบบ " + type + " สำเร็จ! จำนวน " + datesToBook.size() + " ครั้ง โดย " + teacher.getName());
        return true;
    }
}