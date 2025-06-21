package com.autohub.lib.config.debezium;

import com.autohub.exception.AutoHubException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@ConditionalOnProperty(prefix = "debezium", name = "enabled")
public class DebeziumConnectorConfig {

    @Bean
    public io.debezium.config.Configuration debeziumConnector(
            DatabaseProperties databaseProperties,
            DebeziumProperties debeziumProperties
    ) {
        try {
            File offsetStorageTempFile = File.createTempFile("offsets_", ".dat");
            File dbHistoryTempFile = File.createTempFile("dbhistory_", ".dat");

            return io.debezium.config.Configuration.create()
                    .with("name", debeziumProperties.getConnectorName())
                    .with("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
                    .with("plugin.name", "pgoutput")
                    .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                    .with("offset.storage.file.filename", offsetStorageTempFile.getAbsolutePath())
                    .with("offset.flush.interval.ms", "60000")
                    .with("decimal.handling.mode", "string")
                    .with("database.hostname", debeziumProperties.getDatabaseHost())
                    .with("database.port", debeziumProperties.getDatabasePort())
                    .with("database.user", databaseProperties.getUsername())
                    .with("database.password", databaseProperties.getPassword())
                    .with("database.dbname", debeziumProperties.getDatabaseName())
                    .with("table.include.list", debeziumProperties.getSchemaName() + "." + debeziumProperties.getTableName())
                    .with("include.schema.changes", "false")
                    .with("database.allowPublicKeyRetrieval", "true")
                    .with("database.server.id", "10181")
                    .with("database.server.name", debeziumProperties.getServerName())
                    .with("database.history", "io.debezium.relational.history.FileDatabaseHistory")
                    .with("database.history.file.filename", dbHistoryTempFile.getAbsolutePath())
                    .with("topic.prefix", debeziumProperties.getTopicName())
                    .with("publication.name", debeziumProperties.getSlotName())
                    .with("slot.name", debeziumProperties.getSlotName())
                    .with("group.id", debeziumProperties.getGroupId())
                    .build();
        } catch (Exception e) {
            throw new AutoHubException(e.getMessage());
        }
    }

}
