package com.autohub.lib.config.debezium;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "debezium")
@Getter
@Setter
public class DebeziumProperties {

    private String connectorName;
    private String databaseHost;
    private String databaseName;
    private String databasePort;
    private String topicName;
    private String serverName;
    private int serverId;
    private String tableName;
    private String schemaName;
    private String slotName;
    private String groupId;

}
