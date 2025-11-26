public class AutoSaveThread extends Thread {

    private boolean running = true;
    private final int intervalMs;   // interval auto-save
    private final Runnable saveAction;

    public AutoSaveThread(int intervalMs, Runnable saveAction) {
        this.intervalMs = intervalMs;
        this.saveAction = saveAction;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(intervalMs);
                saveAction.run();  // jalankan aksi save
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }

    public void stopAutoSave() {
        running = false;
        this.interrupt();
    }
}
