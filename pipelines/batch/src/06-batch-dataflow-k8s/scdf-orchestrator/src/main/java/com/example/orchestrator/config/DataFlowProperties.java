package com.example.orchestrator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.net.URISyntaxException;

@ConfigurationProperties(prefix = "orchestrator")
public class DataFlowProperties {

    public static final int MAX_WAIT_TIME_DEFAULT = 0;

    /**
     * The maximum amount of time in millis that a individual task can run before
     * the execution of the task is failed.
     */
    private int maxWaitTime = MAX_WAIT_TIME_DEFAULT;

    /**
     * The URI for the dataflow server that will receive task launch requests.
     * Default is http://localhost:9393;
     */
    private URI dataflowServerUri;

    /**
     * The optional username for the dataflow server that will receive task launch requests.
     * Used to access the the dataflow server using Basic Authentication.
     */
    private String dataflowServerUsername;

    /**
     * The optional password for the dataflow server that will receive task launch requests.
     * Used to access the the dataflow server using Basic Authentication.
     */
    private String dataflowServerPassword;

    public DataFlowProperties() {
        try {
            this.dataflowServerUri = new URI("http://localhost:9393");
        }
        catch (URISyntaxException e) {
            throw new IllegalStateException("Invalid Spring Cloud Data Flow Server URI", e);
        }
    }

    public int getMaxWaitTime() {
        return this.maxWaitTime;
    }

    public void setMaxWaitTime(int maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    public URI getDataflowServerUri() {
        return dataflowServerUri;
    }

    public void setDataflowServerUri(URI dataflowServerUri) {
        this.dataflowServerUri = dataflowServerUri;
    }

    public String getDataflowServerUsername() {
        return dataflowServerUsername;
    }

    public void setDataflowServerUsername(String dataflowServerUsername) {
        this.dataflowServerUsername = dataflowServerUsername;
    }

    public String getDataflowServerPassword() {
        return dataflowServerPassword;
    }

    public void setDataflowServerPassword(String dataflowServerPassword) {
        this.dataflowServerPassword = dataflowServerPassword;
    }

}
