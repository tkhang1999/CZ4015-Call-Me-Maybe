package simulation;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * The class {@code Simulator} to run the simulation
 */
public class Simulator {

    private static final int TOTAL_NUMBER_OF_CALLS = 10000;
    private static final int WARM_UP_CALLS = 100;
    private static final Comparator<Event> COMPARATOR = Comparator
            .comparingDouble((Event e) -> e.getTime());
    private double clock;
    private int generatedCalls;
    private int numberOfBlockedCalls;
    private int numberOfDroppedCalls;
    private PriorityQueue<Event> futureEventList;

    public Simulator() {
        clock = 0;
        generatedCalls = 0;
        numberOfBlockedCalls = 0;
        numberOfDroppedCalls = 0;
        futureEventList = new PriorityQueue<>(1, COMPARATOR);
    }

}