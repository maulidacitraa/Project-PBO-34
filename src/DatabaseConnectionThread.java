public class DatabaseConnectionThread extends Thread {

    private final Runnable connectAction;

    public DatabaseConnectionThread(Runnable connectAction) {
        this.connectAction = connectAction;
    }

    @Override
    public void run() {
        System.out.println("⏳ Menghubungkan ke database...");
        connectAction.run();
        System.out.println("✔️ Koneksi database berhasil!");
    }
}
