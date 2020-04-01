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
    // Constants to process CSV data
    private static final int CSV_INDEX_TIME = 1;
    private static final int CSV_INDEX_STATION = 2;
    private static final int CSV_INDEX_CALL_DURATION = 3;
    private static final int CSV_INDEX_CAR_SPEED = 4;

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

        // Generate the first initiation record data
        CallInitiationEvent event = generateCallInitiationEvent();
        // Add the event to FEL
        futureEventList.add(event);

        // Start the event handling routine
        handleEvent();
    }

    // Generate statistics report
    public void generateStatisticsReport() {
        double blockedCallsRate = numberOfBlockedCalls/TOTAL_NUMBER_OF_CALLS;
        double droppedCallsRate = numberOfDroppedCalls/TOTAL_NUMBER_OF_CALLS;

        System.out.println("Blocked Calls Rate: " + blockedCallsRate);
        System.out.println("Dropped Calls Rate: " + droppedCallsRate);
    }

    // Event handling routine
    private void handleEvent() {        
        // Handle events from FEL
        while (!futureEventList.isEmpty()) {
            // Get the event from FEL
            Event event = futureEventList.remove();
            // Clock synchronization
            clock = event.getTime();
            // Handle each type of event
            if (event instanceof CallInitiationEvent) {
                handleCallInitiationEvent((CallInitiationEvent) event);
            } else if (event instanceof CallHandoverEvent) {
                handleCallHandoverEvent((CallHandoverEvent) event);
            } else if (event instanceof CallTerminationEvent) {
                handleCallTerminationEvent((CallTerminationEvent) event);
            }
        }
    }

    // Handle CallInitiationEvent
    private void handleCallInitiationEvent(CallInitiationEvent event) {
        // Clock synchronization
        clock = event.getTime();
        // Get current station
        Station currentStation = event.getCurrentStation();
        // Get car speed
        double carSpeed = event.getCarSpeed();
        // Get car position
        double carPosition = event.getCarPosition();
        // Get call duration
        double callDuration = event.getCallDuration();
        // Get car direction
        Direction carDirection = event.getCarDirection();

        // Check for an available channel for Call Initiation event
        int numberOfAvailableChannels = currentStation.getNumberOfAvailableChannels();
        /* The Call Initiation event is blocked if:
        1. there is no available channel OR
        2. there is only 1 available channel AND
        the Fix Channel Allocation (FCA) scheme is Handover Reservation */
        if (numberOfAvailableChannels == 0 || 
           (numberOfAvailableChannels == 1 && 
           currentStation.isHandoverReservation())) {
            // Increase the number of blocked calls
            numberOfBlockedCalls++;
        } else {
            // Acquire an available channel
            currentStation.acquireAnAvailableChannel();

            // Get the station id
            int stationId = currentStation.getStationId();
            // Calculate distance to next station (km)
            double distanceToNextStation = 2 - carPosition;
            // Calculate time to next station (sec)
            double timeToNextStation = (distanceToNextStation/carSpeed)*3600;

            // Initialize next event
            Event nextEvent;
            // Create Call Termination event if:
            // 1. call duration is less than or equal to the time to next station
            if (callDuration <= timeToNextStation) {
                // Calculate termination time
                double terminationTime = clock + callDuration;
                // Create a Call Termination event
                nextEvent = new CallTerminationEvent(terminationTime, currentStation);
            } 
            // 2. call is in the last station, depending on the direction of the car
            else if ((carDirection == Direction.TO_20TH_STATION && stationId == 20)
                    || (carDirection == Direction.TO_1ST_STATION && stationId == 1)) {
                // Calculate termination time
                double terminationTime = clock + timeToNextStation;
                // Create a Call Termination event
                nextEvent = new CallTerminationEvent(terminationTime, currentStation);
            }
            // create Call Handover event otherwise
            else  {
                // Calculate Handover time
                double handoverTime = clock + timeToNextStation;
                // Calculate call remaining duration
                double callRemainingDuration = callDuration - timeToNextStation;
                // Create a Call Handover event
                nextEvent = new CallHandoverEvent(handoverTime, currentStation, 
                                        carSpeed, callRemainingDuration, carDirection);
            }

            // Add the next event to FEL
            futureEventList.add(nextEvent);
        }

        // Generate next Call Initiation event
        if (generatedCalls < TOTAL_NUMBER_OF_CALLS) {
            Event nextCallInitiationEvent = generateCallInitiationEvent();
            // Add the event to FEL
            futureEventList.add(nextCallInitiationEvent);
        }
    }

    // Handle CallHandoverEvent
    private void handleCallHandoverEvent(CallHandoverEvent event) {
        // Clock synchronization
        clock = event.getTime();
        // Get current station
        Station currentStation = event.getCurrentStation();
        // Get car speed
        double carSpeed = event.getCarSpeed();
        // Get call duration
        double callDuration = event.getCallDuration();
        // Get car direction
        Direction carDirection = event.getCarDirection();

        // Release the previously acquired channel
        currentStation.releaseAnAcquiredChannel();
        // Update the current station
        if (carDirection == Direction.TO_20TH_STATION) {
            currentStation = stations.get(currentStation.getStationId());
        } else {
            currentStation = stations.get(currentStation.getStationId() - 2);
        }

        // Check for an available channel for Call Initiation event
        int numberOfAvailableChannels = currentStation.getNumberOfAvailableChannels();
        // The Call Handover event is dropped if there is no available channel 
        // regardless of the FCA scheme
        if (numberOfAvailableChannels == 0) {
            // Increase the number of dropped calls
            numberOfDroppedCalls++;
            // Exit the handling function
            return;
        } else {
            // Acquire an available channel
            currentStation.acquireAnAvailableChannel();
        }

        // Get the station id
        int stationId = currentStation.getStationId();
        // Distance to the next station in a handover event is alwyas 2 km
        double distanceToNextStation = 2;
        // Calculate time to next station (sec)
        double timeToNextStation = (distanceToNextStation/carSpeed)*3600;

        // Initialize next event
        Event nextEvent;
        // Create Call Termination event if:
        // 1. call duration is less than or equal to the time to next station
        if (callDuration <= timeToNextStation) {
            // Calculate termination time
            double terminationTime = clock + callDuration;
            // Create a Call Termination event
            nextEvent = new CallTerminationEvent(terminationTime, currentStation);
        } 
        // 2. call is in the last station, depending on the direction of the car
        else if ((carDirection == Direction.TO_20TH_STATION && stationId == 20)
                || (carDirection == Direction.TO_1ST_STATION && stationId == 1)) {
            // Calculate termination time
            double terminationTime = clock + timeToNextStation;
            // Create a Call Termination event
            nextEvent = new CallTerminationEvent(terminationTime, currentStation);
        }
        // create Call Handover event otherwise
        else  {
            // Calculate Handover time
            double handoverTime = clock + timeToNextStation;
            // Calculate call remaining duration
            double callRemainingDuration = callDuration - timeToNextStation;
            // Create a Call Handover event
            nextEvent = new CallHandoverEvent(handoverTime, currentStation, 
                                    carSpeed, callRemainingDuration, carDirection);
        }

        // Add the next event to FEL
        futureEventList.add(nextEvent);
    }

    // Handle CallTerminationEvent
    private void handleCallTerminationEvent(CallTerminationEvent event) {
        // Clock synchronization
        clock = event.getTime();
        // Get current station
        Station currentStation = event.getCurrentStation();
        // Release the previously acquired channel
        currentStation.releaseAnAcquiredChannel();
    }

    // Generate a Call Initiation event
    private CallInitiationEvent generateCallInitiationEvent() {
        // Generate the first initiation record data
        List<String> record = callInitiationRecords.remove();
        // Get the initiation time
        double time = Double.parseDouble(record.get(CSV_INDEX_TIME));
        // Get the current station
        int stationId = Integer.parseInt(record.get(CSV_INDEX_STATION));
        Station currentStation = stations.get(stationId - 1);
        // Get the car speed
        double carSpeed = Double.parseDouble(record.get(CSV_INDEX_CAR_SPEED));
        // Get the car position
        double carPosition = RandomNumberGenerator.getCarPosition();
        // Get the call duration
        double callDuration = Double.parseDouble(record.get(CSV_INDEX_CALL_DURATION));
        // Get the car direction
        Direction carDirection = RandomNumberGenerator.getCarDirection();
        
        // Generate the first call initiation event
        CallInitiationEvent event =  new CallInitiationEvent(time, currentStation, carSpeed,
                                                carPosition, callDuration, carDirection);

        // Increase the number of generated calls
        generatedCalls++;

        return event;
    }
}