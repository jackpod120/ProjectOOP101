package ClassroomProject;

public class Teacher {
    private String name;
    private String gmail;
    private String id;
    private String password; // เพิ่มฟิลด์สำหรับรหัสผ่าน

    public Teacher(String name, String gmail, String id, String password) {
        this.name = name;
        this.gmail = gmail;
        this.id = id;
        this.password = password;
    }

    // Getters
    public String getName() { return name; }
    public String getGmail() { return gmail; }
    public String getID() { return id; }
    public String getPassword() { return password; }
}