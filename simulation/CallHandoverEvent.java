package simulation;

/**
 * The class {@code CallHandoverEvent} for the call handover events
 */
public class CallHandoverEvent implements Event {

    private double time;
    private Station currentStation;
    private double carSpeed;
    private double callDuration;
    private Direction carDirection;

    // Constructor
    public CallHandoverEvent(double time, Station currentStation, double carSpeed,
                             double callDuration, Direction carDirection) {
        this.time = time;
        this.currentStation = currentStation;
        this.carSpeed = carSpeed;
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

    // Get the call duration
    public double getCallDuration() {
        return callDuration;
    }

    // Get the car direction
    public Direction getCarDirection() {
        return carDirection;
    }
}