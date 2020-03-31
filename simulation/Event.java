package simulation;

/**
 * The interface {@code Event} for all events in the simulation
 */
public interface Event {

    // Get the event time
    public double getTime();

    // Get the car speed
    public double getSpeed();

    // Get the current station
    public Station getCurrentStation();

    // Get the car position
    public double getCarPosition();

    // Get the call duration
    public double getCallDuration();

    // Get the car direction
    public Direction getCarDirection();

}