package simulation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * The class {@code Simulator} to run the simulation
 */
public class Simulator {

    private static final int TOTAL_NUMBER_OF_CALLS = 10000;
    private static final int WARM_UP_CALLS = 100;
    private static final int NUMBER_OF_AVAILABLE_CHANNELS = 10;
    private static final boolean IS_HANDOVER_RESERVATION = true;
    private static final String CSV_FILE = "PCS_TEST_DETERMINSTIC_19S2.csv";
    private static final String FILE_LOCATION = "C:\\Users\\Dell\\Documents\\Simulation\\simulation\\";
    private static final String COMMA_DELIMITER = ",";
    private static final Comparator<Event> COMPARATOR = Comparator
            .comparingDouble((Event e) -> e.getTime());

    private double clock;
    private int generatedCalls;
    private int numberOfBlockedCalls;
    private int numberOfDroppedCalls;
    private PriorityQueue<Event> futureEventList;
    private List<Station> stations;
    private Queue<List<String>> callInitiationRecords;

    // Constructor
    public Simulator() {
        clock = 0;
        generatedCalls = 0;
        numberOfBlockedCalls = 0;
        numberOfDroppedCalls = 0;
        futureEventList = new PriorityQueue<>(1, COMPARATOR);
        stations = new ArrayList<>();
        callInitiationRecords = new LinkedList<>();
    }

    // Start the simulator
    public void start() {
        // Create 20 base stations, each with 10 available channels and given FCA Scheme
        for (int i = 0; i < 20; i++) {
            stations.add(new Station(i + 1, 10, IS_HANDOVER_RESERVATION));
        }

        // Read the CSV file
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_LOCATION + CSV_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                callInitiationRecords.add(Arrays.asList(values));
            }
        } catch (FileNotFoundException ffe) {
            ffe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // Remove the dummy header in the CSV file
        callInitiationRecords.remove();

    }
}