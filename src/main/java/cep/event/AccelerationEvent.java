package cep.event;

import java.util.Date;

public class AccelerationEvent {

    /** Acceleration in m/s^2. */
    private int acceleration;

    /** ID of the shelf the accelerator is on*/
    private int shelfID;

    /** Time acceleration reading was taken. */
    private Date timeOfReading;

    /**
     * Temperature constructor.
     * @param acceleration Acceleration in m/s2
     * @param timeOfReading Time of Reading
     * @param shelfID ID of the shelf it is collected at
     */
    public AccelerationEvent(int acceleration, Date timeOfReading, int shelfID) {
        this.acceleration = acceleration;
        this.timeOfReading = timeOfReading;
        this.shelfID = shelfID;
    }

    /**
     * Get the Acceleration of the different directions.
     * @return Acceleration
     */
    public int getAcceleration() {
        return this.acceleration;
    }

    /**
     * Get time Temperature reading was taken.
     * @return Time of Reading
     */
    public long getTimeOfReading() {
        return timeOfReading.getTime();
    }

    public int getShelfID() { return shelfID;}
    @Override
    public String toString() {
        return "AccelerationEvent [" + acceleration + "cm/s^2, " + timeOfReading.toString() + ", " + shelfID + "]";
    }

}
