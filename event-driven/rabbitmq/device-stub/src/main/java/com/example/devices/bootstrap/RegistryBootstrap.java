package com.example.devices.bootstrap;

import com.example.devices.data.StubConfigurationData;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.util.stream.IntStream;

@Slf4j
@Controller
public class RegistryBootstrap {

    private final int MAXIMUN_DEVICES_STUB_RETRIES = 5000;

    private final String STUB_PROPERTIES_MAIN_ID = "main";
    private final String STUB_PROPERTIES_COUNT_KEY = "count";
    private final String STUB_SERVICE_NAME_KEY = "name";

    private final String STUB_SERVICES_COLLECTION = "services";
    private final String STUB_PROPERTIES_COLLECTION = "properties";

    @Value("${spring.data.mongodb.uri}")
    private String mongoURI;

    @Value("${devices.databases.stub}")
    private String databaseStub;

    @Value("${devices.parameters.partitionId:0}")
    private int partitionId;

    @Value("${devices.parameters.count:10}")
    private int devicesCount;

    @Value("${spring.application.name:devices-stub}")
    private String serviceName;

    public StubConfigurationData register() {

        log.info("Registering current devices stubs instances and getting global properties");
        MongoClient client = MongoClients.create(mongoURI);

        MongoDatabase authDb = client.getDatabase(databaseStub);
        MongoCollection propertiesCol = authDb.getCollection(STUB_PROPERTIES_COLLECTION);
        MongoCollection servicesCol = authDb.getCollection(STUB_SERVICES_COLLECTION);

        Document properties = (Document) propertiesCol
                .find(Filters.eq("_id", STUB_PROPERTIES_MAIN_ID))
                .iterator()
                .tryNext();

        if (properties != null ) {
            log.info("Getting existing global properties");
            devicesCount = properties.getInteger(STUB_PROPERTIES_COUNT_KEY);
        }
        else {
            log.info("Setting global properties: count {}", devicesCount);

            // Create Global Properties
            Document dbObject = new Document();
            dbObject.append("_id", STUB_PROPERTIES_MAIN_ID);
            dbObject.append(STUB_PROPERTIES_COUNT_KEY, devicesCount);

            try {
                propertiesCol.insertOne(dbObject);
            } catch (MongoWriteException ex) {
                log.info("Getting existing global properties (again)");
                devicesCount = properties.getInteger(STUB_PROPERTIES_COUNT_KEY);
            }
        }

        int servicesCount = (int) servicesCol.countDocuments();
        for (int currentId : IntStream.range(servicesCount, MAXIMUN_DEVICES_STUB_RETRIES).toArray()) {
            log.info("Devices stubs trying to register with Id {}", currentId);
            // Create Global Properties
            Document dbObject = new Document();
            dbObject.append("_id", currentId);
            dbObject.append(STUB_SERVICE_NAME_KEY, serviceName);

            try {
                servicesCol.insertOne(dbObject);
                partitionId = currentId;
                break;
            } catch (MongoWriteException ex) {
                log.info("Devices stubs with Id {} already exists", currentId);
            }
        }

        client.close();

        return new StubConfigurationData(partitionId, devicesCount);
    }

    public void unregister() {
        log.info("Unregistering current devices stubs instances and getting global properties");
        MongoClient client = MongoClients.create(mongoURI);

        MongoDatabase authDb = client.getDatabase(databaseStub);
        MongoCollection propertiesCol = authDb.getCollection(STUB_PROPERTIES_COLLECTION);
        MongoCollection servicesCol = authDb.getCollection(STUB_SERVICES_COLLECTION);

        servicesCol.deleteOne(Filters.eq("_id", partitionId));
        if (partitionId == 0) {
            propertiesCol.deleteOne(Filters.eq("_id", STUB_PROPERTIES_MAIN_ID));
        }
        client.close();
    }
}
