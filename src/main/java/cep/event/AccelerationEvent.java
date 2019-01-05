package cep.event;

import java.util.Date;

public class AccelerationEvent {

    /** Acceleration in m/s^2. */
    private int acceleration;

    /** ID of the shelf the accelerator is on*/
    private int shelfID;

    /** Time acceleration reading was taken. */
    private Date timeOfReading;

    private String unit;
    /**
     * Acceleration constructor.
     * @param acceleration Acceleration
     * @param timeOfReading Time of Reading
     * @param shelfID ID of the shelf it is collected
     * @param unit Unit of the measurement
     */
    public AccelerationEvent(int acceleration, Date timeOfReading, int shelfID, String unit) {
        this.acceleration = acceleration;
        this.timeOfReading = timeOfReading;
        this.shelfID = shelfID;
        this.unit = unit;
    }

    /**
     * Get the Acceleration of the different directions.
     * @return Acceleration
     */
    public int getAcceleration() {
        return this.acceleration;
    }

    /**
     * Get time Acceleration reading was taken.
     * @return Time of Reading
     */
    public long getTimeOfReading() {
        return timeOfReading.getTime();
    }

    public int getShelfID() { return shelfID;}
    public String getUnit() { return unit;}
    @Override
    public String toString() {
        return "AccelerationEvent [" + acceleration + " " + unit + ", " + timeOfReading.toString() + ", " + shelfID + "]";
    }

}
