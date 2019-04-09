package ch.iceguard.functions;

import org.bson.types.ObjectId;

import java.util.Date;

public class Measurement {

    private ObjectId _id;
    private String deviceId;
    private int messageId;
    private double temperature;
    private double humidity;
    private double acceleratorX;
    private double acceleratorY;
    private double acceleratorZ;
    private double gyroscopeX;
    private double gyroscopeY;
    private double gyroscopeZ;
    private Date timestamp;

    public Measurement() {

    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
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

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }
}
