package simulation;

/**
 * The class {@code Station} for the base station in the simulation
 */
public class Station {

    private int stationId;
    private int numberOfAvailableChannels;

    // Constructor
    public Station(int stationId, int numberOfAvailableChannels) {
        this.stationId = stationId;
        this.numberOfAvailableChannels = numberOfAvailableChannels;
    }

    // Get the station id
    public int getStationId() {
        return stationId;
    }

    // Get the number of available channels
    public int getNumberOfAvailableChannels() {
        return numberOfAvailableChannels;
    }

    // Acquire an available channel in the station
    public void acquireAnAvailableChannel() {
        this.numberOfAvailableChannels--;
    }

    // Release an acquired channel
    public void releaseAnAcquiredChannel() {
        this.numberOfAvailableChannels++;
    }
}