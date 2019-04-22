package ch.iceguard.functions;

import ch.iceguard.functions.model.Measurement;
import ch.iceguard.functions.model.MeasurementValues;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import mockit.Deencapsulation;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.*;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.mockito.Mockito.*;

public class MeasurementFunctionTest {

    private static MongoClient mongoClient;
    private static MongodProcess mongod;
    private static MongodExecutable mongodExecutable;
    private static MongoCollection<Measurement> collection;

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (mongoClient != null) {
            mongoClient.close();
        }
        if (mongod != null) {
            mongod.stop();
        }
        if (mongodExecutable != null) {
            mongodExecutable.stop();
        }
    }

    @Before
    public void setUp() throws Exception {
        try {
            final IMongodConfig mongodConfig = new MongodConfigBuilder()
                    .version(Version.Main.PRODUCTION).build();

            MongodStarter runtime = MongodStarter.getDefaultInstance();

            mongodExecutable = runtime.prepare(mongodConfig);
            mongod = mongodExecutable.start();

            mongoClient = new MongoClient(new ServerAddress(mongodConfig.net()
                    .getServerAddress(), mongodConfig.net().getPort()));
            Deencapsulation.setField(CosmosService.class, "mongoClient", mongoClient);

        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        collection = mongoClient.getDatabase(
                "test-measurements").getCollection("measurements", Measurement.class).withCodecRegistry(pojoCodecRegistry);
        Deencapsulation.setField(CosmosService.class, "collection",
                collection);
    }

    @Test
    public void testGetMeasurementsWithUnknownDevice() throws Exception {

        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("deviceId", "testDevice");
        doReturn(queryParams).when(req).getQueryParameters();

        final Optional<String> queryBody = Optional.empty();
        doReturn(queryBody).when(req).getBody();
        doAnswer((Answer<HttpResponseMessage.Builder>) invocation -> {
            HttpStatus status = (HttpStatus) invocation.getArguments()[0];
            return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
        }).when(req).createResponseBuilder(any(HttpStatus.class));

        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        // Invoke
        final HttpResponseMessage ret = new Function().runMeasurements(req, context);

        // Verify
        assertThat(ret.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(ret.getBody().toString()).isEqualTo("[]");
    }

    @Test
    public void testGetMeasurementsWithKnownDevice() throws Exception {

        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("deviceId", "testDevice");
        doReturn(queryParams).when(req).getQueryParameters();

        final Optional<String> queryBody = Optional.empty();
        doReturn(queryBody).when(req).getBody();
        doAnswer((Answer<HttpResponseMessage.Builder>) invocation -> {
            HttpStatus status = (HttpStatus) invocation.getArguments()[0];
            return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
        }).when(req).createResponseBuilder(any(HttpStatus.class));

        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        collection.insertOne(getTestDeviceMeasurement(1));

        // Invoke
        final HttpResponseMessage ret = new Function().runMeasurements(req, context);

        // Verify
        assertThat(ret.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        List<Measurement> measurements = (List<Measurement>) ret.getBody();
        assertThat(measurements).hasSize(1);
        assertThat(measurements).extracting("messageId").contains(1);
        assertThat(measurements).extracting("measurementValues")
                .extracting("temperature").contains(20.5);
    }

    @Test
    public void testGetMeasurementsWithoutDevice() throws Exception {

        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);
        final Map<String, String> queryParams = new HashMap<>();
        doReturn(queryParams).when(req).getQueryParameters();

        final Optional<String> queryBody = Optional.empty();
        doReturn(queryBody).when(req).getBody();
        doAnswer((Answer<HttpResponseMessage.Builder>) invocation -> {
            HttpStatus status = (HttpStatus) invocation.getArguments()[0];
            return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
        }).when(req).createResponseBuilder(any(HttpStatus.class));

        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        // Invoke
        final HttpResponseMessage ret = new Function().runMeasurements(req, context);

        // Verify
        assertThat(ret.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testGetLatestMeasurementWithKnownDevice() throws Exception {

        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("deviceIds", "testDevice");
        doReturn(queryParams).when(req).getQueryParameters();

        final Optional<String> queryBody = Optional.empty();
        doReturn(queryBody).when(req).getBody();
        doAnswer((Answer<HttpResponseMessage.Builder>) invocation -> {
            HttpStatus status = (HttpStatus) invocation.getArguments()[0];
            return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
        }).when(req).createResponseBuilder(any(HttpStatus.class));

        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        collection.insertOne(getTestDeviceMeasurement(1));
        collection.insertOne(getTestDeviceMeasurement(2));

        // Invoke
        final HttpResponseMessage ret = new Function().runLatestMeasurments(req, context);

        // Verify
        assertThat(ret.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        List<Measurement> measurements = (List<Measurement>) ret.getBody();
        assertThat(measurements).hasSize(1);
        assertThat(measurements).extracting("messageId").containsOnly(2);
        assertThat(measurements).extracting("measurementValues")
                .extracting("temperature").contains(20.5);
    }

    @Test
    public void testGetLatestMeasurementWithoutDevice() throws Exception {

        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);
        final Map<String, String> queryParams = new HashMap<>();
        doReturn(queryParams).when(req).getQueryParameters();

        final Optional<String> queryBody = Optional.empty();
        doReturn(queryBody).when(req).getBody();
        doAnswer((Answer<HttpResponseMessage.Builder>) invocation -> {
            HttpStatus status = (HttpStatus) invocation.getArguments()[0];
            return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
        }).when(req).createResponseBuilder(any(HttpStatus.class));

        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        collection.insertOne(getTestDeviceMeasurement(1));
        collection.insertOne(getTestDeviceMeasurement(2));

        // Invoke
        final HttpResponseMessage ret = new Function().runLatestMeasurments(req, context);

        // Verify
        assertThat(ret.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private static Measurement getTestDeviceMeasurement(int messageId) {
        Measurement measurement = new Measurement();
        measurement.setDeviceId("testDevice");
        measurement.setMessageId(messageId);
        measurement.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));

        MeasurementValues values = new MeasurementValues();
        values.setTemperature(20.5);
        values.setHumidity(37.32);
        measurement.setMeasurementValues(values);

        return measurement;
    }

}
