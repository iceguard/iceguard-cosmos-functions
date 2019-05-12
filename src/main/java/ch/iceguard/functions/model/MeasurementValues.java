package ch.iceguard.functions.model;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class MeasurementValues {

    @BsonProperty("temperature")
    public double temperature;

    @BsonProperty("humidity")
    public double humidity;

    @BsonProperty("acceleratorX")
    public double acceleratorX;

    @BsonProperty("acceleratorY")
    public double acceleratorY;

    @BsonProperty("acceleratorZ")
    public double acceleratorZ;

    @BsonProperty("gyroscopeX")
    public double gyroscopeX;

    @BsonProperty("gyroscopeY")
    public double gyroscopeY;

    @BsonProperty("gyroscopeZ")
    public double gyroscopeZ;

    @BsonProperty("steps")
    public double steps;

    public MeasurementValues() {
        // for serializing purposes
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getAcceleratorX() {
        return acceleratorX;
    }

    public void setAcceleratorX(double acceleratorX) {
        this.acceleratorX = acceleratorX;
    }

    public double getAcceleratorY() {
        return acceleratorY;
    }

    public void setAcceleratorY(double acceleratorY) {
        this.acceleratorY = acceleratorY;
    }

    public double getAcceleratorZ() {
        return acceleratorZ;
    }

    public void setAcceleratorZ(double acceleratorZ) {
        this.acceleratorZ = acceleratorZ;
    }

    public double getGyroscopeX() {
        return gyroscopeX;
    }

    public void setGyroscopeX(double gyroscopeX) {
        this.gyroscopeX = gyroscopeX;
    }

    public double getGyroscopeY() {
        return gyroscopeY;
    }

    public void setGyroscopeY(double gyroscopeY) {
        this.gyroscopeY = gyroscopeY;
    }

    public double getGyroscopeZ() {
        return gyroscopeZ;
    }

    public void setGyroscopeZ(double gyroscopeZ) {
        this.gyroscopeZ = gyroscopeZ;
    }

    public double getSteps() {
        return steps;
    }

    public void setSteps(double steps) {
        this.steps = steps;
    }
}

