package ch.iceguard.functions.model;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.Date;

public class Measurement {

    @BsonProperty("_id")
    public ObjectId id;

    @BsonProperty("deviceId")
    public String deviceId;

    @BsonProperty("messageId")
    public int messageId;

    @BsonProperty("values")
    public MeasurementValues measurementValues;

    @BsonProperty("timestamp")
    public Date timestamp;

    public Measurement() {
        // for serializing purposes
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public MeasurementValues getMeasurementValues() {
        return measurementValues;
    }

    public void setMeasurementValues(MeasurementValues measurementValues) {
        this.measurementValues = measurementValues;
    }

}
