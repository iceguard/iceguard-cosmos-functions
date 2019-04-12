package ch.iceguard.functions;

import ch.iceguard.functions.model.Measurement;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Azure Functions for getting Data from Cosmos DB with Mongo Api.
 */
public class Function {

    static final String FIELD_DEVICE_ID = "deviceId";
    private static final String FIELD_DEVICE_IDS = "deviceIds";
    private static final int MAX_RESULTS = 50;
    private MeasurementDao measurementDao = new MeasurementDaoCosmosImpl();

    @FunctionName("measurements")
    public HttpResponseMessage runMeasurements(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        // Parse query parameter
        String deviceId = request.getQueryParameters().get(FIELD_DEVICE_ID);

        if (deviceId == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("deviceId is a mandatory parameter").build();
        }

        List<Measurement> measurements;
        measurements = measurementDao.getLatestMeasurementsByDevice(deviceId, MAX_RESULTS);


        return request.createResponseBuilder(HttpStatus.OK).body(measurements).build();
    }

    @FunctionName("latest")
    public HttpResponseMessage runLatestMeasurments(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        String deviceIds = request.getQueryParameters().get(FIELD_DEVICE_IDS);

        if (deviceIds == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("deviceIds is a mandatory parameter").build();
        }

        List<String> devices = Arrays.asList(deviceIds.split(","));
        List<Measurement> measurements;
        measurements = measurementDao.getMostRecentMeasurements(devices);

        return request.createResponseBuilder(HttpStatus.OK).body(measurements).build();
    }


}
