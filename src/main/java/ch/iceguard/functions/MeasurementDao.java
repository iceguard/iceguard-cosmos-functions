package ch.iceguard.functions;

import ch.iceguard.functions.model.Measurement;

import java.util.List;

public interface MeasurementDao {

    List<Measurement> getMostRecentMeasurements(List<String> deviceIds);

    List<Measurement> getLatestMeasurementsByDevice(String deviceId, int noOfMeasurements);

}
