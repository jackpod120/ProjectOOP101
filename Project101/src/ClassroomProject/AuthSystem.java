package ClassroomProject;

import java.util.HashMap;
import java.util.Map;

public class AuthSystem {
    // ใช้ HashMap เก็บข้อมูล โดยใช้ Gmail เป็น Key
    private Map<String, Teacher> teachers = new HashMap<>();

    public boolean signUp(String name, String gmail, String id, String password) {
        if (teachers.containsKey(gmail)) {
            System.out.println("❌ Sign Up ล้มเหลว: " + gmail + " มีอยู่ในระบบแล้ว");
            return false;
        }
        Teacher newTeacher = new Teacher(name, gmail, id, password);
        teachers.put(gmail, newTeacher);
        System.out.println("✅ Sign Up สำเร็จ! ยินดีต้อนรับ, " + name);
        return true;
    }

    public Teacher signIn(String gmail, String password) {
        Teacher teacher = teachers.get(gmail);
        if (teacher != null && teacher.getPassword().equals(password)) {
            System.out.println("✅ Sign In สำเร็จ! สวัสดี, " + teacher.getName());
            return teacher;
        }
        System.out.println("❌ Sign In ล้มเหลว: Gmail หรือรหัสผ่านไม่ถูกต้อง");
        return null; // คืนค่า null ถ้าล็อคอินไม่สำเร็จ
    }
}