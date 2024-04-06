import Supervisor.SlaveProgram;
import Supervisor.State;

import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;

public class SlaveProgramImpl implements SlaveProgram {
    private State state = State.UNKNOWN;
    private final Object lock = new Object();

    @Override
    public synchronized void suspend() {
        System.out.println("PROGRAM : Suspending");
        state = State.STOPPING;

        notify();
    }

    @Override
    public synchronized void resume() {

        System.out.println("PROGRAM : Resuming");
        state = State.RUNNING;
        notify();

    }

    @Override
    public synchronized void run() {
        System.out.println("PROGRAM :  Running");
        state = State.RUNNING;
        Thread thread = new Thread(new Daemon());
        thread.setDaemon(true);
        thread.start();
    }

    public synchronized void error() {
        System.out.println("PROGRAM : fatal error");
        state = State.FATAL_ERROR;
        notify();
    }

    @Override
    public synchronized State getState() {
        return state;
    }

    public class Daemon implements Runnable {
        public void run() {
            Random random = new Random();
            while (true) {
                synchronized (this){
                    try {
                        Thread.sleep(random.nextInt(3000));
                        switch (random.nextInt(11)) {
                            case 0, 1, 2, 3, 4:
                                suspend();
                                break;
                            case 5, 6, 7, 8, 9:
                                resume();
                            case 10:
                                error();
                                break;
                        }
                    } catch (InterruptedException e) {
                        System.err.println("DAEMON : Interrupted");
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
