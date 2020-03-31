package simulation;

/**
 * The class {@code Station} for the base station in the simulation
 */
public class Station {

    private int numberOfAvailableChannels;

    public int getNumberOfAvailableChannels() {
        return numberOfAvailableChannels;
    }

    public void setNumberOfAvailableChannels(int numberOfAvailableChannels) {
        this.numberOfAvailableChannels = numberOfAvailableChannels;
    }

    public void acquireAnChannel() {
        this.numberOfAvailableChannels--;
    }

    public void releaseAnChannel() {
        this.numberOfAvailableChannels++;
    }
}