package Supervisor;

public class Supervisor {
    private final SlaveProgram program;

    public Supervisor(SlaveProgram program) {
        this.program = program;
    }

    public State getProgramState() {
        synchronized (program) {
            return program.getState();
        }
    }

    /*
    public synchronized void stopProgram() {
        System.err.println("SUPERVISOR : Stopping program");
        synchronized (program) {
            thread.interrupt();
        }
    }
    */

    public void suspendProgram() {
        System.err.println("SUPERVISOR : Suspending program");
        synchronized (program) {
            program.suspend();
        }
    }

    public void resumingProgram() {
        System.err.println("SUPERVISOR : Resuming program");
        synchronized (program) {
            program.resume();
        }
    }

    public void startProgram() {
        System.err.println("SUPERVISOR : Starting program");
        Thread thread = new Thread(program);
        thread.start();
        State state = State.UNKNOWN;
        synchronized (program) {
            state = getProgramState();
        }
        while (state != State.FATAL_ERROR) {
            while (state == State.RUNNING) {
                synchronized (program) {
                    try {
                        program.wait();
                    } catch (InterruptedException e) {
                        System.err.println("SUPERVISOR : Program has been interrupted");
                        throw new RuntimeException(e);
                    }
                    state = getProgramState();
                }
            }
            if (state == State.STOPPING) {
                System.err.println("SUPERVISOR : Program stopped, resuming ...");
                synchronized (program) {
                    program.resume();
                }
            }
            synchronized (program) {
                state = getProgramState();
            }
        }
        System.err.println("SUPERVISOR : fatal error");
        thread.interrupt();
    }
}

