package simulation;

/**
 * The class {@code CallTerminationEvent} for the call termination events
 */
public class CallTerminationEvent implements Event {

    private double time;
    private Station currentStation;
    
    // Constructor
    public CallTerminationEvent(double time, Station currentStation) {
        this.time = time;
        this.currentStation = currentStation;
    }

    @Override
    public double getTime() {
        return time;
    }

    @Override
    public Station getCurrentStation() {
        return currentStation;
    }
}