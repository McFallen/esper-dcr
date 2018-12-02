package cep.event;

import java.util.Date;

public class AccelerationShakeEvent {

    /** Acceleration in m/s^2. */
    private int acceleration;

    /** ID of the shelf the accelerator is on*/
    private int shelfID;

    /** Time acceleration reading was taken. */
    private Date timeOfReading;

    /**
     * Single value constructor.
     * @param value Temperature in Celsius.
     */
    /**
     * Temerature constructor.
     * @param acceleration Temperature in Celsius
     * @param timeOfReading Time of Reading
     */
    public AccelerationShakeEvent(int acceleration, Date timeOfReading, int shelfID) {
        this.acceleration = acceleration;
        this.timeOfReading = timeOfReading;
        this.shelfID = shelfID;
    }

    /**
     * Get the Acceleration of the different directions.
     * @return Temperature in Celsius
     */
    public double getAcceleration() {
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
        return "AccelerationShakeEvent [" + acceleration + "cm/s^2, " + timeOfReading.toString() + ", " + shelfID + "]";
    }

}
