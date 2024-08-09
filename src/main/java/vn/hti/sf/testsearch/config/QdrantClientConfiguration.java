package vn.hti.sf.testsearch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QdrantClientConfiguration {

    public static String qdrantHost;
    public static int qdrantPort;

    @Value("${qdrant.host}")
    public void setHost(String host) {
        this.qdrantHost = host;
    }

    @Value("${qdrant.port}")
    public void setQdrantPort(int qdrantPort) {
        this.qdrantPort = qdrantPort;
    }

}
