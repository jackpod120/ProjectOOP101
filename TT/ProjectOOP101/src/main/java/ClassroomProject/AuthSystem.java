package ClassroomProject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AuthSystem {
    private static final String FILE_PATH = System.getProperty("user.dir") + File.separator + "data" + File.separator + "teachers.csv";
    private Map<String, Teacher> teachers = new HashMap<>();

    public AuthSystem() {
        loadTeachers();
    }

    public boolean signUp(String name, String gmail, String id, String password) {
        if (teachers.containsKey(gmail)) {
            System.out.println("❌ Sign Up ล้มเหลว: " + gmail + " มีอยู่ในระบบแล้ว");
            return false;
        }
        for (Teacher t : teachers.values()) {
            if (t.getID().equals(id)) {
                System.out.println("❌ Sign Up ล้มเหลว: รหัสอาจารย์ " + id + " มีอยู่ในระบบแล้ว");
                return false;
            }
        }
        Teacher newTeacher = new Teacher(name, gmail, id, password);
        teachers.put(gmail, newTeacher);
        saveTeacher(newTeacher); // บันทึกลง teachers.csv
        createTeacherReservationFile(id); // 🔸 สร้างไฟล์ของอาจารย์

        // ✅ สร้างไฟล์จองของอาจารย์ใหม่
        createTeacherReservationFile(id);
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

    // 🔹 สร้างไฟล์ CSV สำหรับอาจารย์แต่ละคน เช่น data/T001.csv
    private void createTeacherReservationFile(String teacherID) {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, teacherID + ".csv");
        if (!file.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                // FIX: เพิ่ม Course และ Code
                bw.write("ReservationID,Room,Course,Code,Day,StartTime,EndTime,Type,Month,Year");
                bw.newLine();
                System.out.println("📁 สร้างไฟล์ใหม่ให้ " + teacherID);
            } catch (IOException e) {
                System.err.println("❌ ไม่สามารถสร้างไฟล์ของ " + teacherID + ": " + e.getMessage());
            }
        } else {
            System.out.println("ℹ️ ไฟล์ของ " + teacherID + " มีอยู่แล้ว ไม่ต้องสร้างใหม่");
            }
        }
    }

