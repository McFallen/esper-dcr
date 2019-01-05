package cep.event;

import java.util.Date;

public class AccelerationShakeEvent {

    /** Acceleration in m/s^2. */
    private int acceleration;

    /** ID of the shelf the accelerator is on*/
    private int shelfID;

    /** Time acceleration reading was taken. */
    private Date timeOfReading;

    private String unit;


    private AccelerationEvent origin;
    /**
     * Acceleration constructor.
     * @param acceleration Acceleration
     * @param timeOfReading Time of Reading
     * @param shelfID ID of the shelf it is collected
     * @param unit Unit of the measurement
     */
    public AccelerationShakeEvent(int acceleration, Date timeOfReading, int shelfID, String unit, AccelerationEvent origin) {
        this.acceleration = acceleration;
        this.timeOfReading = timeOfReading;
        this.shelfID = shelfID;
        this.unit = unit;
        this.origin = origin;
    }

    /**
     * Get the Acceleration of the different directions.
     * @return Acceleration
     */
    public double getAcceleration() {
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
        return "AccelerationShakeEvent [" + acceleration + " " + unit + ", " + timeOfReading.toString() + ", " + shelfID + "]";
    }

}
