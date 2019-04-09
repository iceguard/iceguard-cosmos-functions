package ch.iceguard.functions;

import com.mongodb.MongoClientURI;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ch.iceguard.functions.Function.FIELD_DEVICE_ID;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MeasurementDaoCosmosImpl implements MeasurementDao {

    private static final String MONGO_DB_COLLECTION_NAME_PROPERTY = "collection-name-COSMOS";
    private static final String MONGO_DB_NAME = "iceguard-iot";
    private static final String MONGO_DB_CONNECTION_STRING_PROPERTY = "iceguard-iot_DOCUMENTDB";
    private static final String MONGO_URL = Objects.toString(System.getenv(MONGO_DB_CONNECTION_STRING_PROPERTY), "mongodb://test");

    @Override
    public List<Measurement> getMostRecentMeasurements(List<String> deviceIds) {

        List<Measurement> measurements = new ArrayList<>();
        try (MongoClient mongoClient = new MongoClient(new MongoClientURI(System.getenv(MONGO_DB_CONNECTION_STRING_PROPERTY)))) {
            MongoCollection<Document> collection = getMongoCollection(mongoClient);
            deviceIds.forEach(device -> measurements.add(collection.find(eq(FIELD_DEVICE_ID, device), Measurement.class).sort(new Document("_id", -1)).first()));
        }
        return measurements;
    }

    @Override
    public List<Measurement> getLatestMeasurementsByDevice(String deviceId, int noOfMeasurements) {
        List<Measurement> measurements;

        try (MongoClient mongoClient = new MongoClient(new MongoClientURI(MONGO_URL))) {
            MongoCollection<Document> collection = getMongoCollection(mongoClient);
            measurements = collection.find(eq(FIELD_DEVICE_ID, deviceId), Measurement.class).limit(50).into(new ArrayList<>());
        }
        return measurements;
    }

    private static MongoCollection<Document> getMongoCollection(MongoClient client) {
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoDatabase database = client.getDatabase(MONGO_DB_NAME);
        return database.getCollection(System.getenv(MONGO_DB_COLLECTION_NAME_PROPERTY)).withCodecRegistry(pojoCodecRegistry);
    }
}
