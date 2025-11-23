import java.time.LocalDate;
import java.util.Scanner;

public class MainApp {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        AuthService auth = new AuthService();
        ToDoManager manager = new ToDoManager();

        System.out.println("===== LOGIN =====");
        System.out.print("Username: ");
        String u = sc.nextLine();
        System.out.print("Password: ");
        String p = sc.nextLine();

        if (!auth.login(u, p)) {
            System.out.println("Login gagal!");
            return;
        }

        System.out.println("Login berhasil!");

        int choice;
        do {
            System.out.println("\n===== MENU =====");
            System.out.println("1. Tambah ToDo");
            System.out.println("2. Lihat Semua");
            System.out.println("3. Hapus ToDo");
            System.out.println("0. Keluar");
            System.out.print("Pilih: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1 -> {
                    System.out.println("Pilih tipe: 1=Task 2=Activity 3=Event");
                    int type = sc.nextInt(); sc.nextLine();

                    System.out.print("Judul: "); 
                    String title = sc.nextLine();

                    System.out.print("Deskripsi: "); 
                    String desc = sc.nextLine();

                    System.out.print("Tanggal (yyyy-mm-dd): ");
                    LocalDate date = LocalDate.parse(sc.nextLine());

                    System.out.print("Prioritas (LOW/MEDIUM/HIGH): ");
                    Priority prio = Priority.valueOf(sc.nextLine().toUpperCase());

                    if (type == 1) manager.add(new TaskToDo(title, desc, date, prio));
                    else if (type == 2) {
                        System.out.print("Lokasi: "); 
                        String loc = sc.nextLine();
                        manager.add(new ActivityToDo(title, desc, date, prio, loc));
                    }
                    else {
                        System.out.print("Durasi jam: ");
                        int d = sc.nextInt();
                        manager.add(new EventToDo(title, desc, date, prio, d));
                    }
                }

                case 2 -> manager.showAll();

                case 3 -> {
                    System.out.print("Index hapus: ");
                    int i = sc.nextInt();
                    manager.remove(i);
                }

                case 0 -> System.out.println("Keluar...");
            }

        } while (choice != 0);

        sc.close();
    }
}
