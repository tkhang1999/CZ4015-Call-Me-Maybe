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

    // Get an exponential random number for inter-arrival time
    public static double getInterArrivalTime() {

        return getExponentialRandomNumber(INTER_ARRIVAL_TIME_MEAN);
    }

    // Get an uniform random number for base station
    public static int getBaseStation() {
        
        return (int) Math.round(getUniformRandomNumber(1, 20));
    }

    // Get a normal random number for car speed
    public static double getCarSpeed() {

        return getNormalRandomNumber(CAR_SPEED_MEAN, Math.sqrt(CAR_SPEED_VARIANCE));
    }

    // Get an uniform random number for the car position
    public static double getCarPosition() {

        return getUniformRandomNumber(0, 2); 
    }

    // Get an exponential random number for call duration
    public static double getCallDuration() {
        
        return getExponentialRandomNumber(SHIFTED_CALL_DURATION_MEAN) + SHIFTED_VALUE;
    }

    // Get a random car direction
    public static Direction getCarDirection() {
        int randomNumber = (int) (Math.random()*2);

        return (randomNumber == 0) ? Direction.TO_1ST_STATION : Direction.TO_20TH_STATION;
    }

    private static double getUniformRandomNumber(double a, double b) {
        double u = Math.random();
        double randomNumber = u*(b - a) + a;

        return randomNumber;
    }

    private static double getExponentialRandomNumber(double beta) {
        double u = Math.random();

        return -beta*Math.log(1 - u);
    }

    private static double getNormalRandomNumber(double mu, double sigma) {
        Random random = new Random();

        return mu + random.nextGaussian()*sigma;
    }
}