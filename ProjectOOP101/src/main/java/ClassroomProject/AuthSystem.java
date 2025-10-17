package ClassroomProject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AuthSystem {
    private static final String FILE_PATH = "data/teachers.csv";
    private Map<String, Teacher> teachers = new HashMap<>();

    public AuthSystem() {
        loadTeachers();
    }

    public boolean signUp(String name, String gmail, String id, String password) {
        if (teachers.containsKey(gmail)) {
            System.out.println("❌ Sign Up ล้มเหลว: " + gmail + " มีอยู่ในระบบแล้ว");
            return false;
        }

        Teacher newTeacher = new Teacher(name, gmail, id, password);
        teachers.put(gmail, newTeacher);
        saveTeacher(newTeacher); // 🔹 บันทึกลงไฟล์
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
        return null;
    }

    // 🔹 โหลดข้อมูลครูจากไฟล์ CSV
    private void loadTeachers() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            file.getParentFile().mkdirs(); // สร้างโฟลเดอร์ data ถ้ายังไม่มี
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String name = parts[0];
                    String gmail = parts[1];
                    String id = parts[2];
                    String password = parts[3];
                    teachers.put(gmail, new Teacher(name, gmail, id, password));
                }
            }
            System.out.println("📂 โหลดข้อมูลครูสำเร็จ (" + teachers.size() + " คน)");
        } catch (IOException e) {
            System.err.println("❌ ไม่สามารถโหลดข้อมูลครูได้: " + e.getMessage());
        }
    }

    // 🔹 บันทึกครูใหม่เพิ่มต่อท้ายในไฟล์ CSV
    private void saveTeacher(Teacher teacher) {
        try (FileWriter fw = new FileWriter(FILE_PATH, true)) {
            fw.write(teacher.getName() + "," +
                     teacher.getGmail() + "," +
                     teacher.getID() + "," +
                     teacher.getPassword() + "\n");
        } catch (IOException e) {
            System.err.println("❌ ไม่สามารถบันทึกข้อมูลได้: " + e.getMessage());
        }
    }
}
