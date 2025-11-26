public class LoadingAnimationThread extends Thread {

    private boolean running = true;

    @Override
    public void run() {
        String[] frames = {"-", "\\", "|", "/"};
        int i = 0;

        while (running) {
            System.out.print("\rLoading " + frames[i++ % frames.length]);
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }

    public void stopLoading() {
        running = false;
        this.interrupt();
    }
}
