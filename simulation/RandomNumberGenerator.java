package simulation;

/**
 * The class {@code RandomNumberGenerator} to generate random variables for the simulation
 */
public class RandomNumberGenerator {

    // Get an uniform random number for the car position
    public static double getCarPosition() {

        return getUniformRandomNumber(0, 2); 
    }

    // Get a random car direction
    public static Direction getCarDirection() {
        int randomNumber = (int) (Math.random()*2);

        return (randomNumber == 0) ? Direction.TO_1ST_STATION : Direction.TO_20TH_STATION;
    }

    private static double getUniformRandomNumber(int a, int b) {
        double u = Math.random();
        double randomNumber = u*(b - a) + a;

        return randomNumber;
    }
}