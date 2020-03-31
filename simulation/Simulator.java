package simulation;

import java.util.PriorityQueue;

/**
 * The class {@code Simulator} to run the simulation
 */
public class Simulator {

    private double clock;
    private PriorityQueue<Event> futureEventList;
    private int generatedCalls = 0;
    private int numberOfBlockedCalls = 0;
    private int numberOfDroppedCalls = 0;
    private int totalNumberOfCalls = 10000;

}