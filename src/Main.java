import Supervisor.Supervisor;

public class Main {
    public static void main(String[] args) {
        Supervisor supervisor = new Supervisor(new SlaveProgramImpl());

        supervisor.startProgram();

    }

}