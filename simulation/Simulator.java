package simulation;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * The class {@code Simulator} to run the simulation
 */
public class Simulator {

    private static final int TOTAL_NUMBER_OF_CALLS = 250000;
    private static final int WARM_UP_CALLS = 50000;
    private static final int SCALE = 5;
    private static final int NUMBER_OF_AVAILABLE_CHANNELS = 10;
    private static final Comparator<Event> COMPARATOR = Comparator.comparing(Event::getTime);
    private static final String OUTPUT_FILE = "C:\\Users\\Dell\\Documents\\Simulation\\output.csv";

    private double clock;
    private boolean isHandoverReservation;
    private int generatedCalls;
    private int numberOfBlockedCalls;
    private int numberOfDroppedCalls;
    private PriorityQueue<Event> futureEventList;
    private List<Station> stations;
    private List<List<String>> statistics;

    // Constructor
    public Simulator(boolean isHandoverReservation) {
        this.clock = 0;
        this.isHandoverReservation = isHandoverReservation;
        this.generatedCalls = 0;
        this.numberOfBlockedCalls = 0;
        this.numberOfDroppedCalls = 0;
        this.futureEventList = new PriorityQueue<>(1, COMPARATOR);
        this.stations = new ArrayList<>();
        this.statistics = new ArrayList<>();
    }

    // Start the simulator
    public void start() {
        // Create 20 base stations, each with 10 available channels and given FCA Scheme
        for (int i = 0; i < 20; i++) {
            stations.add(new Station(i + 1, NUMBER_OF_AVAILABLE_CHANNELS, isHandoverReservation));
        }

        // Generate the first initiation record data
        CallInitiationEvent event = generateCallInitiationEvent();
        // Add the event to FEL
        futureEventList.add(event);
        // Calculate the statistic when a call is generated
        calculateStatistic();
        // Start the event handling routine
        handleEvent();
    }

    // Generate statistics report
    public void generateStatisticsReport() {
        int totalNumberOfCalls = TOTAL_NUMBER_OF_CALLS - WARM_UP_CALLS;
        BigDecimal blockedCallsRate = BigDecimal.valueOf(numberOfBlockedCalls)
                    .divide(BigDecimal.valueOf(totalNumberOfCalls),
                            SCALE, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        BigDecimal droppedCallsRate = BigDecimal.valueOf(numberOfDroppedCalls)
                    .divide(BigDecimal.valueOf(totalNumberOfCalls),
                            SCALE, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

        // Print statistics
        System.out.println("FCA Scheme: " + (isHandoverReservation ? "HANDOVER RESERVATION" : "NO RESERVATION"));
        System.out.println("Number of Warm Up Calls: " + WARM_UP_CALLS);
        System.out.println("Total number of Calls (after Warm Up period): " + totalNumberOfCalls);
        System.out.println("Number of Blocked Calls: " + numberOfBlockedCalls);
        System.out.println("Number of Dropped Calls: " + numberOfDroppedCalls);
        System.out.println("Blocked Calls Rate (%): " + blockedCallsRate);
        System.out.println("Dropped Calls Rate (%): " + droppedCallsRate);

        // Write the statistics to output file
        try {
            FileWriter writer = new FileWriter(OUTPUT_FILE, true);

            writer.write(blockedCallsRate.toString() + ',' + droppedCallsRate.toString());
            writer.write("\n");
            writer.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // Event handling routine
    private void handleEvent() {        
        // Handle events from FEL
        while (!futureEventList.isEmpty()) {
            // Get the event from FEL
            Event event = futureEventList.remove();
            // Clock synchronization
            clock = event.getTime();
            // System.out.println(clock);
            // Handle each type of event
            if (event instanceof CallInitiationEvent) {
                handleCallInitiationEvent((CallInitiationEvent) event);
            } else if (event instanceof CallHandoverEvent) {
                handleCallHandoverEvent((CallHandoverEvent) event);
            } else if (event instanceof CallTerminationEvent) {
                handleCallTerminationEvent((CallTerminationEvent) event);
            }

            // Reset after the warm up period
            if (generatedCalls == WARM_UP_CALLS) {
                numberOfBlockedCalls = 0;
                numberOfDroppedCalls = 0;
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
            // Calculate the statistic when a call is generated
            calculateStatistic();
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
        // Get the initiation time
        double time = clock + RandomNumberGenerator.getInterArrivalTime();
        // Get the current station
        int stationId = RandomNumberGenerator.getBaseStation();
        Station currentStation = stations.get(stationId - 1);
        // Get the car speed
        double carSpeed = RandomNumberGenerator.getCarSpeed();
        // Get the car position
        double carPosition = RandomNumberGenerator.getCarPosition();
        // Get the call duration
        double callDuration = RandomNumberGenerator.getCallDuration();
        // Get the car direction
        Direction carDirection = RandomNumberGenerator.getCarDirection();
        
        // Generate the first call initiation event
        CallInitiationEvent event =  new CallInitiationEvent(time, currentStation, carSpeed,
                                                carPosition, callDuration, carDirection);

        // Increase the number of generated calls
        generatedCalls++;

        return event;
    }

    // Calculate statistic and add to the statistics list
    private void calculateStatistic() {            
        List<String> statistic = new ArrayList<>();
        BigDecimal blockedCallsRate = BigDecimal.valueOf(numberOfBlockedCalls)
                    .divide(BigDecimal.valueOf(generatedCalls),
                            SCALE, RoundingMode.HALF_UP);
        BigDecimal droppedCallsRate = BigDecimal.valueOf(numberOfDroppedCalls)
                    .divide(BigDecimal.valueOf(generatedCalls),
                            SCALE, RoundingMode.HALF_UP);

        statistic.add(blockedCallsRate.toString());
        statistic.add(droppedCallsRate.toString());

        statistics.add(statistic);
    }
}