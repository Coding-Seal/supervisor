package Supervisor;

public interface SlaveProgram extends Runnable {
    public void suspend();

    public void resume();

    public void run();

    State getState();
}
