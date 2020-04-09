package simulation;

import java.util.Random;

/**
 * The class {@code RandomNumberGenerator} to generate random variables for the simulation
 */
public class RandomNumberGenerator {

    private static final double INTER_ARRIVAL_TIME_MEAN = 1.369680;
    private static final double SHIFTED_CALL_DURATION_MEAN = 99.831897;
    private static final double SHIFTED_VALUE = 10.004;
    private static final double CAR_SPEED_MEAN = 120.072095;
    private static final double CAR_SPEED_VARIANCE = 81.335230;

    private static Random randomInterArrivalTime = new Random();
    private static Random randomBaseStation = new Random();
    private static Random randomCarSpeed = new Random();
    private static Random randomCarPosition = new Random();
    private static Random randomCallDuration = new Random();
    private static Random randomCarDirection = new Random();

    // Get an exponential random number for inter-arrival time
    public static double getInterArrivalTime() {
        double u = randomInterArrivalTime.nextDouble();

        return (-INTER_ARRIVAL_TIME_MEAN)*Math.log(1 - u);
    }

    // Get an uniform random number for base station
    public static int getBaseStation() {
        int random = randomBaseStation.nextInt(20);
        
        return random + 1;
    }

    // Get a normal random number for car speed
    public static double getCarSpeed() {
        double random = randomCarSpeed.nextGaussian();

        return CAR_SPEED_MEAN + random*Math.sqrt(CAR_SPEED_VARIANCE);
    }

    // Get an uniform random number for the car position
    public static double getCarPosition() {
        double u = randomCarPosition.nextDouble();

        return u*(2 - 0) + 0;
    }

    // Get an exponential random number for call duration
    public static double getCallDuration() {
        double u = randomCallDuration.nextDouble();
        
        return (-SHIFTED_CALL_DURATION_MEAN)*Math.log(1 - u) + SHIFTED_VALUE;
    }

    // Get a random car direction
    public static Direction getCarDirection() {
        boolean random = randomCarDirection.nextBoolean();

        return random ? Direction.TO_1ST_STATION : Direction.TO_20TH_STATION;
    }
}