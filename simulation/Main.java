package simulation;

/**
 * The class {@code Main} to run the simulation
 */
public class Main {

    public static void main(String[] args) {
        // Create the simulator
        Simulator simulator = new Simulator();

        System.out.println("----------------------------------------");
        System.out.println("------------START-SIMULATION------------");
        System.out.println("----------------------------------------");
        // Start the simulation
        simulator.start();

        System.out.println("----------------------------------------");
        System.out.println("---------------STATISTICS---------------");
        System.out.println("----------------------------------------");
        // Genrate statistics report
        simulator.generateStatisticsReport();

        System.out.println("----------------------------------------");
        System.out.println("-------------END-SIMULATION-------------");
        System.out.println("----------------------------------------");
    }
}