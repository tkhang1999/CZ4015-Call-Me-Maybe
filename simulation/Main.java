package simulation;

/**
 * The class {@code Main} to run the simulation
 */
public class Main {

    public static void main(String[] args) {
        // Create two simulators with 2 different FCA schemes
        // One with Handover Reservation, the other with No Reservation
        System.out.println("----------------------------------------");
        System.out.println("------------START-SIMULATION------------");
        System.out.println("----------------------------------------");

        // Start the simulation with no reservation
        for (int i = 0; i < 1; i++) {
            Simulator simulatorNoReservation = new Simulator(false);
            simulatorNoReservation.start();
            simulatorNoReservation.generateStatisticsReport();
        }


        System.out.println("----------------------------------------");
        System.out.println("----------------------------------------");
        System.out.println("----------------------------------------");

        // Start the simulation with handover reservation
        for (int i = 0; i < 1; i++) {
            Simulator simulatorHandoverReservation = new Simulator(true);
            simulatorHandoverReservation.start();
            simulatorHandoverReservation.generateStatisticsReport();
        }


        System.out.println("----------------------------------------");
        System.out.println("-------------END-SIMULATION-------------");
        System.out.println("----------------------------------------");
    }
}