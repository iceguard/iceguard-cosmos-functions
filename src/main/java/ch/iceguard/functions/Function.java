package ch.iceguard.functions;

import java.util.*;
import java.util.function.Consumer;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

/**
 * Azure Functions for getting Data from Cosmos DB with Mongo Api.
 */
public class Function {

    private static final String MONGO_DB_CONNECTION_STRING_PROPERTY = "iceguard-iot_DOCUMENTDB";
    private static final String MONGO_DB_COLLECTION_NAME_PROPERTY = "collection-name-COSMOS";
    private static final String MONGO_DB_NAME = "iceguard-iot";
    private static final String FIELD_DEVICE_ID = "deviceId";

    @FunctionName("measurements")
    public HttpResponseMessage runMeasurments(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        // Parse query parameter
        String deviceId = request.getQueryParameters().get("deviceId");
        String fromDate = request.getQueryParameters().get("fromDate");
        String toDate = request.getQueryParameters().get("toDate");
        String userId = request.getQueryParameters().get("userId");

        if (deviceId == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("deviceId is a mandatory parameter").build();
        }

        List<Document> items = new ArrayList<>();

        try (MongoClient mongoClient = new MongoClient(new MongoClientURI(System.getenv(MONGO_DB_CONNECTION_STRING_PROPERTY)))) {
            MongoCollection<Document> collection = getMongoCollection(mongoClient);
            collection.find(eq(FIELD_DEVICE_ID, deviceId)).limit(50).forEach((Consumer<? super Document>) items::add);
        }

        return request.createResponseBuilder(HttpStatus.OK).body(items).build();
    }

    @FunctionName("latest")
    public HttpResponseMessage runLatestMeasurments(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        // Parse query parameter
        String deviceIds = request.getQueryParameters().get("deviceIds");

        if (deviceIds == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("deviceIds is a mandatory parameter").build();
        }

        List<String> devices = Arrays.asList(deviceIds.split(","));
        List<Document> items = new ArrayList<>();

        try (MongoClient mongoClient = new MongoClient(new MongoClientURI(System.getenv(MONGO_DB_CONNECTION_STRING_PROPERTY)))) {
            MongoCollection<Document> collection = getMongoCollection(mongoClient);
            devices.forEach(device -> items.add(collection.find(eq(FIELD_DEVICE_ID, device)).sort(new Document("_id", -1)).first()));
        }

        return request.createResponseBuilder(HttpStatus.OK).body(items).build();
    }

    private static MongoCollection<Document> getMongoCollection(MongoClient client) {
        MongoDatabase database = client.getDatabase(MONGO_DB_NAME);
        return database.getCollection(System.getenv(MONGO_DB_COLLECTION_NAME_PROPERTY));
    }
}
