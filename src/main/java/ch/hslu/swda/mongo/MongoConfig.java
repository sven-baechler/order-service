package ch.hslu.swda.mongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MongoConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MongoConfig.class);

    private static final String HOST_ENV = "MONGO_HOST";
    private static final String USER_ENV = "MONGO_USER";
    private static final String PW_ENV = "MONGO_PW";
    private static final String CONFIG_FILE_NAME = "mongo.properties";

    private final Properties properties = new Properties();

    public MongoConfig() {
        this(CONFIG_FILE_NAME);
    }

    private MongoConfig(String configFileName) {
        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFileName);
        try {
            properties.load(inputStream);
            assert inputStream != null;
            inputStream.close();
        } catch (IOException e) {
            LOG.error("Error while reading from file {}", CONFIG_FILE_NAME);
        }
    }

    private String getHost() {
        String host = System.getenv(HOST_ENV);
        if (host != null) {
            return host;
        }
        return this.properties.getProperty("host");
    }

    String getPort() {
        return this.properties.getProperty("port");
    }

    public String getDatabaseName() {
        return this.properties.getProperty("databaseName");
    }

    public String getOrderCollectionName() {
        return this.properties.getProperty("orderCollection");
    }


    public String getConnectionString() {
        String user = System.getenv(USER_ENV);
        String pw = System.getenv(PW_ENV);

        if (user != null && pw != null) {
            return String.format("mongodb://%s:%s@%s:%s", user, pw, this.getHost(), this.getPort());
        }

        return  String.format("mongodb://%s:%s", this.getHost(), this.getPort());
    }
}
