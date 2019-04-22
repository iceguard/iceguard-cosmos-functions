package ch.iceguard.functions;

import ch.iceguard.functions.model.Measurement;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.ArrayList;
import java.util.List;

import static ch.iceguard.functions.Function.FIELD_DEVICE_ID;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class CosmosService {

    private static final String MONGO_DB_COLLECTION_NAME_PROPERTY = "collection-name-COSMOS";
    private static final String MONGO_DB_NAME = "iceguard-iot";
    private static final String MONGO_DB_CONNECTION_STRING_PROPERTY = "iceguard-iot_DOCUMENTDB";
    private static MongoClient mongoClient;
    private static MongoCollection<Measurement> collection;

    private CosmosService() {
        // Hide public constructor
    }

    public static void init() {
        if (mongoClient == null || collection == null) {
            mongoClient = new MongoClient(new MongoClientURI(System.getenv(MONGO_DB_CONNECTION_STRING_PROPERTY)));
            collection = getMongoCollection(mongoClient);
        }
    }

    public static void close() {
        mongoClient.close();
        mongoClient = null;
    }

    public static List<Measurement> getMostRecentMeasurements(List<String> deviceIds) {
        List<Measurement> measurements = new ArrayList<>();
        deviceIds.forEach(device -> {
            Measurement measurement = collection.find(eq(FIELD_DEVICE_ID, device))
                    .sort(new Document("_id", -1))
                    .first();
            if (measurement != null) {
                measurements.add(measurement);
            }
        });
        return measurements;
    }

    public static List<Measurement> getLatestMeasurementsByDevice(String deviceId, int noOfMeasurements) {
        List<Measurement> measurements;
            measurements = collection.find(eq(FIELD_DEVICE_ID, deviceId))
                    .sort(new Document("_id", -1))
                    .limit(noOfMeasurements)
                    .into(new ArrayList<>());
        return measurements;
    }

    private static MongoCollection<Measurement> getMongoCollection(MongoClient client) {
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoDatabase database = client.getDatabase(MONGO_DB_NAME);
        return database.getCollection(System.getenv(MONGO_DB_COLLECTION_NAME_PROPERTY), Measurement.class).withCodecRegistry(pojoCodecRegistry);
    }
}
