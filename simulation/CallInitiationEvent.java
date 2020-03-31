package simulation;

/**
 * The class {@code CallInitiationEvent} for call initiation events
 */
public class CallInitiationEvent implements Event {

    private double time;
    private Station currentStation;
    private double carSpeed;
    private double carPosition;
    private double callDuration;
    private Direction carDirection;

    // Constructor
    public CallInitiationEvent(double time, Station currentStation, double carSpeed, 
                               double carPosition, double callDuration, Direction carDirection) {
        this.time = time;
        this.currentStation = currentStation;
        this.carSpeed = carSpeed;
        this.carPosition = carPosition;
        this.callDuration = callDuration;
        this.carDirection = carDirection;
    }
    
    @Override
    public double getTime() {
        return time;
    }

    @Override
    public Station getCurrentStation() {
        return currentStation;
    }

    // Get the car speed
    public double getCarSpeed() {
        return carSpeed;
    }

    // Get the car position
    public double getCarPosition() {
        return carPosition;
    }

    // Get the call duration
    public double getCallDuration() {
        return callDuration;
    }

    // Get the car direction
    public Direction getCarDirection() {
        return carDirection;
    }
}