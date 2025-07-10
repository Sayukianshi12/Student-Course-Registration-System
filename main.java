import java.util.*;
import java.io.*;

class User {
    String username, password;
    boolean isAdmin;

    User(String username, String password, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
    }
}

class Course {
    String id, name;

    Course(String id, String name) {
        this.id = id;
        this.name = name;
    }
}

public class Main {
    static Scanner sc = new Scanner(System.in);
    static Map<String, User> users = new HashMap<>();
    static Map<String, Course> courses = new HashMap<>();
    static Map<String, List<String>> registrations = new HashMap<>();
    static String dataFile = "data.txt";

    public static void main(String[] args) {
        loadDummyData(); // or use loadFromFile();

        while (true) {
            System.out.println("\n=== Welcome to Course Registration System ===");
            System.out.println("1. Sign Up");
            System.out.println("2. Login");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            int ch = sc.nextInt();
            sc.nextLine();

            if (ch == 1) signUp();
            else if (ch == 2) login();
            else if (ch == 0) {
                //saveToFile(); // optional
                System.out.println("Thank you!");
                break;
            } else System.out.println("Invalid choice.");
        }
    }

    static void signUp() {
        System.out.print("Enter username: ");
        String u = sc.nextLine();
        if (users.containsKey(u)) {
            System.out.println("Username exists.");
            return;
        }
        System.out.print("Enter password: ");
        String p = sc.nextLine();
        users.put(u, new User(u, p, false));
        System.out.println("Signup successful.");
    }

    static void login() {
        System.out.print("Enter username: ");
        String u = sc.nextLine();
        System.out.print("Enter password: ");
        String p = sc.nextLine();
        if (!users.containsKey(u) || !users.get(u).password.equals(p)) {
            System.out.println("Invalid credentials.");
            return;
        }
        User user = users.get(u);
        if (user.isAdmin) adminMenu(user);
        else studentMenu(user);
    }

    static void adminMenu(User user) {
        while (true) {
            System.out.println("\n--- Admin Panel ---");
            System.out.println("1. Add Course");
            System.out.println("2. Remove Course");
            System.out.println("3. View All Courses");
            System.out.println("0. Logout");
            System.out.print("Choice: ");
            int ch = sc.nextInt();
            sc.nextLine();

            if (ch == 1) addCourse();
            else if (ch == 2) removeCourse();
            else if (ch == 3) listCourses();
            else if (ch == 0) break;
            else System.out.println("Invalid choice.");
        }
    }

    static void studentMenu(User user) {
        while (true) {
            System.out.println("\n--- Student Panel ---");
            System.out.println("1. View Available Courses");
            System.out.println("2. Enroll in a Course");
            System.out.println("3. Drop a Course");
            System.out.println("4. My Courses");
            System.out.println("0. Logout");
            System.out.print("Choice: ");
            int ch = sc.nextInt();
            sc.nextLine();

            if (ch == 1) listCourses();
            else if (ch == 2) enroll(user.username);
            else if (ch == 3) drop(user.username);
            else if (ch == 4) viewMyCourses(user.username);
            else if (ch == 0) break;
            else System.out.println("Invalid choice.");
        }
    }

    static void addCourse() {
        System.out.print("Enter Course ID: ");
        String id = sc.nextLine();
        if (courses.containsKey(id)) {
            System.out.println("Course already exists.");
            return;
        }
        System.out.print("Enter Course Name: ");
        String name = sc.nextLine();
        courses.put(id, new Course(id, name));
        System.out.println("Course added.");
    }

    static void removeCourse() {
        System.out.print("Enter Course ID to remove: ");
        String id = sc.nextLine();
        if (!courses.containsKey(id)) {
            System.out.println("Course not found.");
            return;
        }
        courses.remove(id);
        registrations.values().forEach(list -> list.remove(id));
        System.out.println("Course removed.");
    }

    static void listCourses() {
        System.out.println("\nAvailable Courses:");
        if (courses.isEmpty()) {
            System.out.println("No courses available.");
        } else {
            for (Course c : courses.values()) {
                System.out.println(c.id + ": " + c.name);
            }
        }
    }

    static void enroll(String username) {
        listCourses();
        System.out.print("Enter Course ID to enroll: ");
        String id = sc.nextLine();
        if (!courses.containsKey(id)) {
            System.out.println("Invalid course.");
            return;
        }
        registrations.putIfAbsent(username, new ArrayList<>());
        if (registrations.get(username).contains(id)) {
            System.out.println("Already enrolled.");
            return;
        }
        registrations.get(username).add(id);
        System.out.println("Enrolled successfully.");
    }

    static void drop(String username) {
        viewMyCourses(username);
        System.out.print("Enter Course ID to drop: ");
        String id = sc.nextLine();
        if (registrations.containsKey(username) && registrations.get(username).remove(id)) {
            System.out.println("Course dropped.");
        } else {
            System.out.println("Not enrolled in course.");
        }
    }

    static void viewMyCourses(String username) {
        System.out.println("\nYour Courses:");
        List<String> list = registrations.get(username);
        if (list == null || list.isEmpty()) {
            System.out.println("No courses enrolled.");
        } else {
            for (String id : list) {
                Course c = courses.get(id);
                if (c != null) System.out.println(c.id + ": " + c.name);
            }
        }
    }

    // Optional dummy data for testing
    static void loadDummyData() {
        users.put("admin", new User("admin", "admin", true));
        courses.put("C101", new Course("C101", "Java Basics"));
        courses.put("C102", new Course("C102", "Data Structures"));
    }

    // Optional persistence
    static void saveToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dataFile))) {
            out.writeObject(users);
            out.writeObject(courses);
            out.writeObject(registrations);
        } catch (IOException e) {
            System.out.println("Error saving data.");
        }
    }

    static void loadFromFile() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(dataFile))) {
            users = (Map<String, User>) in.readObject();
            courses = (Map<String, Course>) in.readObject();
            registrations = (Map<String, List<String>>) in.readObject();
        } catch (Exception e) {
            System.out.println("No previous data found.");
        }
    }
}
