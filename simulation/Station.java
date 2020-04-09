package simulation;

/**
 * The class {@code Station} for the base station in the simulation
 */
public class Station {

    private int stationId;
    private int numberOfAvailableChannels;
    private int numberOfReservations;

    // Constructor
    public Station(int stationId, int numberOfAvailableChannels, int numberOfReservations) {
        this.stationId = stationId;
        this.numberOfAvailableChannels = numberOfAvailableChannels;
        this.numberOfReservations = numberOfReservations;
    }

    // Get the station id
    public int getStationId() {
        return stationId;
    }

    // Get the number of available channels
    public int getNumberOfAvailableChannels() {
        return numberOfAvailableChannels;
    }

    // Get the boolean value for call handover reservation
    public int getNumberOfReservations() {
        return numberOfReservations;
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