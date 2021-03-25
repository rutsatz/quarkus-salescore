package com.salescore.infrastructure.config;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

public class DatabaseResource implements QuarkusTestResourceLifecycleManager {

    public final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.2"));

    @Override
    public Map<String, String> start() {
        mongoDBContainer.start();
        return Map.of(
                "quarkus.mongodb.connection-string",
                "mongodb://" + mongoDBContainer.getContainerIpAddress() + ":" + mongoDBContainer.getFirstMappedPort(),
                "quarkus.mongodb.database", "salescore"
        );
    }

    @Override
    public void stop() {
        mongoDBContainer.stop();
    }
}
