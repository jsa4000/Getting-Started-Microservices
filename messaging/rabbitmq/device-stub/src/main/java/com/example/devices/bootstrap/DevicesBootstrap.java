package com.example.devices.bootstrap;

import com.example.devices.utils.ArrayUtils;
import com.example.devices.utils.BCryptHelper;
import com.example.devices.utils.MacGenerator;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

@Slf4j
@Controller
public class DevicesBootstrap {

    private final String DEVICE_CREDENTIALS_COLLECTION = "device_credentials";
    private final String DEVICE_INFO_COLLECTION = "device_info";
    private final String DEVICE_STATUS_COLLECTION = "device_status";

    private final String DEVICES_STUB_TAGS_VALUE = "stub";
    private final String DEVICES_STUB_TAGS_KEY = "tags";

    @Value("${spring.data.mongodb.uri}")
    private String mongoURI;

    @Value("${devices.databases.auth}")
    private String databaseAuth;

    @Value("${devices.databases.devices}")
    private String databaseDevices;

    public List<Long> createDevices(Long fromMac, Long toMac) {
        final List<Long> credentialsCreated = new ArrayList<>();

        Long[] macDevices = LongStream.range(fromMac, toMac).boxed().toArray(Long[]::new);
        List<Long[]> chunks = ArrayUtils.chunk(macDevices,2);

        MongoClient client = MongoClients.create(mongoURI);

        MongoDatabase authDb = client.getDatabase(databaseAuth);
        MongoCollection credentialsCol = authDb.getCollection(DEVICE_CREDENTIALS_COLLECTION);

        MongoDatabase devicesDb = client.getDatabase(databaseDevices);
        MongoCollection statusCol = devicesDb.getCollection(DEVICE_STATUS_COLLECTION);
        MongoCollection infoCol = devicesDb.getCollection(DEVICE_INFO_COLLECTION);

        chunks.stream().forEach(chunk -> {
            List<Document> credentialsDocs = new ArrayList<>();
            List<Document> statusDocs = new ArrayList<>();
            List<Document> classificationsDocs = new ArrayList<>();
            for (Long item : chunk) {
                String deviceMac = MacGenerator.getMacByNumber(item);

                Document credential = (Document) credentialsCol
                        .find(Filters.eq("_id", deviceMac))
                        .iterator()
                        .tryNext();

                if (credential == null){
                    // Create Credentials
                    Document dbObject = new Document();
                    dbObject.append("_id", deviceMac);
                    dbObject.append("password", BCryptHelper.hash(deviceMac));
                    dbObject.append(DEVICES_STUB_TAGS_KEY, DEVICES_STUB_TAGS_VALUE);
                    credentialsDocs.add(dbObject);

                    // Create Device Status
                    dbObject = new Document();
                    dbObject.append("_id", deviceMac);
                    dbObject.append("online", "true");
                    dbObject.append(DEVICES_STUB_TAGS_KEY, DEVICES_STUB_TAGS_VALUE);
                    statusDocs.add(new Document(dbObject));

                    // Create Device Info
                    dbObject = new Document();
                    dbObject.append("_id", deviceMac);
                    dbObject.append("sensorName", "Temperature Sensor");
                    dbObject.append("measureType", "degrees");
                    dbObject.append(DEVICES_STUB_TAGS_KEY, DEVICES_STUB_TAGS_VALUE);
                    classificationsDocs.add(new Document(dbObject));

                    credentialsCreated.add(item);
                }
                else if (DEVICES_STUB_TAGS_VALUE.equals(credential.getString(DEVICES_STUB_TAGS_KEY))) {
                    credentialsCreated.add(item);
                }
            }
            if (credentialsDocs.size() > 0) {
                credentialsCol.insertMany(credentialsDocs);
                statusCol.insertMany(statusDocs);
                infoCol.insertMany(classificationsDocs);
            }
        });

        client.close();

        return credentialsCreated;
    }

    public void removeDevices(List<Long> createdMacs) {
        log.info("Removing Devices Created");

        Long[] macDevices = createdMacs.stream().toArray(Long[]::new);
        List<Long[]> chunks = ArrayUtils.chunk(macDevices,2);

        MongoClient client = MongoClients.create(mongoURI);

        MongoDatabase authDb = client.getDatabase(databaseAuth);
        MongoCollection credentialsCol = authDb.getCollection(DEVICE_CREDENTIALS_COLLECTION);

        MongoDatabase devicesDb = client.getDatabase(databaseDevices);
        MongoCollection statusCol = devicesDb.getCollection(DEVICE_STATUS_COLLECTION);
        MongoCollection infoCol = devicesDb.getCollection(DEVICE_INFO_COLLECTION);

        chunks.stream().forEach(chunk -> {
            List<Document> documents = new ArrayList<>();
            for (Long item : chunk) {
                String deviceMac = MacGenerator.getMacByNumber(item);
                credentialsCol.deleteMany(Filters.eq("_id", deviceMac));
                statusCol.deleteMany(Filters.eq("_id", deviceMac));
                infoCol.deleteMany(Filters.eq("_id", deviceMac));
            }
        });

        client.close();
    }
}
