package ch.iceguard.functions;

import java.util.List;

public interface MeasurementDao {

    List<Measurement> getMostRecentMeasurements(List<String> deviceIds);

    List<Measurement> getLatestMeasurementsByDevice(String deviceId, int noOfMeasurements);

}
