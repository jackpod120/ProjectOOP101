## โครงการจองห้องเรียน (Project101)

เอกสารนี้อธิบายสิ่งที่ได้ทำ เพิ่มไฟล์ที่เกี่ยวข้อง วิธีการคอมไพล์/รันโปรเจกต์ และการใช้งาน UI สำหรับ Sign Up และ Sign In โดยอ้างอิงตามคลาส `AuthSystem` ที่มีอยู่แล้วในโปรเจกต์

### สิ่งที่เพิ่ม/แก้ไข
- เพิ่มไฟล์ `src/ClassroomProject/LoginUI.java` เป็น UI แบบ Swing ที่มี 2 แท็บ:
  - Sign Up: กรอก `Name`, `Gmail`, `ID`, `Password` แล้วกดปุ่มเพื่อสมัครสมาชิกใหม่ โดยเช็คซ้ำกับระบบ (กันสมัคร Gmail เดิม)
  - Sign In: กรอก `Gmail`, `Password` แล้วกดปุ่มเพื่อเข้าสู่ระบบ โดยตรวจสอบข้อมูลกับที่สมัครไว้
- แก้ไขไฟล์ `src/ClassroomProject/Main.java` ให้เปิดหน้า UI ใหม่ด้วย `LoginUI.show(new AuthSystem())` แทนการรันเดโมจองห้องเดิมในคอนโซล

หมายเหตุ: ระบบเก็บข้อมูลผู้ใช้แบบ In-Memory ด้วย `HashMap` ใน `AuthSystem` (ข้อมูลจะหายเมื่อปิดโปรแกรม)

### โครงสร้างไฟล์ที่เกี่ยวข้อง
- `src/ClassroomProject/AuthSystem.java` ระบบสมัคร/เข้าสู่ระบบ (ตรวจสอบ Gmail ซ้ำและรหัสผ่าน)
- `src/ClassroomProject/LoginUI.java` หน้าต่าง UI แบบ Swing มีแท็บ Sign Up/Sign In เชื่อมกับ `AuthSystem`
- `src/ClassroomProject/Main.java` จุดเริ่มต้นของโปรแกรม เรียกเปิด `LoginUI`

### วิธีคอมไพล์และรัน (Windows PowerShell)
1) เปิด PowerShell ที่โฟลเดอร์รากของโปรเจกต์ `Project101`
2) คอมไพล์โค้ดทั้งหมดไปที่โฟลเดอร์ `bin`

```bash
javac -d bin -cp src src\ClassroomProject\*.java
```

3) รันโปรแกรมหลัก

```bash
java -cp bin ClassroomProject.Main
```

### วิธีใช้งาน UI
- เมื่อเปิดโปรแกรมจะพบหน้าต่างที่มี 2 แท็บ: Sign Up และ Sign In

1) Sign Up
   - กรอกชื่อ (Name), อีเมล Gmail, รหัสอาจารย์ (ID), และรหัสผ่าน (Password)
   - กดปุ่ม Sign Up
   - หากสำเร็จ จะแสดงข้อความ: `✅ Sign Up สำเร็จ! ยินดีต้อนรับ, <ชื่อ>`
   - หากล้มเหลวเพราะ Gmail ซ้ำ จะแสดงข้อความแจ้งข้อผิดพลาด

2) Sign In
   - กรอก Gmail และ Password ที่สมัครไว้
   - กดปุ่ม Sign In
   - หากสำเร็จ จะแสดงข้อความ: `✅ Sign In สำเร็จ! สวัสดี, <ชื่อ>`
   - หากไม่พบข้อมูลหรือรหัสผ่านไม่ถูกต้อง จะแสดงข้อความแจ้งข้อผิดพลาด

### ลำดับขั้นตอนที่ได้ทำ (Comment of steps)
1) อ่านโค้ดที่มีอยู่ (`AuthSystem`, `Teacher`, ฯลฯ) เพื่อทำความเข้าใจรูปแบบข้อมูลและเมธอดที่ต้องใช้
2) ออกแบบ UI ด้วย Java Swing ให้มี 2 แท็บ ได้แก่ Sign Up และ Sign In
3) สร้างไฟล์ `LoginUI.java` และวางองค์ประกอบฟอร์ม (Label/TextField/PasswordField/Button) ด้วย `GridBagLayout`
4) เขียนโค้ด `ActionListener` ของปุ่ม Sign Up เพื่อ:
   - ตรวจสอบการกรอกข้อมูลครบถ้วน
   - เรียก `authSystem.signUp(name, gmail, id, password)`
   - แสดงผลลัพธ์ผ่าน `JOptionPane` พร้อมเคลียร์อินพุตเมื่อสำเร็จ
5) เขียนโค้ด `ActionListener` ของปุ่ม Sign In เพื่อ:
   - ตรวจสอบการกรอกข้อมูลครบถ้วน
   - เรียก `authSystem.signIn(gmail, password)`
   - แสดงผลลัพธ์ผ่าน `JOptionPane`
6) ปรับ `Main.java` ให้เรียก `LoginUI.show(authSystem)` เพื่อเปิด UI เมื่อเริ่มโปรแกรม
7) คอมไพล์และทดสอบการทำงานด้วยคำสั่งในหัวข้อ วิธีคอมไพล์และรัน

### ข้อจำกัด/แนวทางต่อยอด
- ปัจจุบันข้อมูลผู้ใช้เก็บในหน่วยความจำ (หายเมื่อปิดโปรแกรม) หากต้องการถาวร แนะนำบันทึกลงไฟล์/ฐานข้อมูล
- สามารถเชื่อมหน้า Sign In สำเร็จไปยังหน้าจัดการจองห้อง (Reservation UI) ต่อได้ในอนาคต


