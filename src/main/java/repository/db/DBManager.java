package repository.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public final class DBManager {
    private static volatile DBManager instance;
    private HikariDataSource dataSource;

    private DBManager() {
        try {
            Properties props = loadProperties();
            initConnectionPool(props);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize DBManager", e);
        }
    }

    public static DBManager getInstance() {
        if (instance == null) {
            synchronized (DBManager.class) {
                if (instance == null) {
                    instance = new DBManager();
                }
            }
        }
        return instance;
    }

    private Properties loadProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("Unable to find config.properties");
            }
            props.load(input);
        }
        return props;
    }

    private void initConnectionPool(Properties props) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("db.url"));
        config.setUsername(props.getProperty("db.user"));
        config.setPassword(props.getProperty("db.password"));
        config.setMaximumPoolSize(Integer.parseInt(
                props.getProperty("db.pool.maxSize", "20")));
        config.setMinimumIdle(Integer.parseInt(
                props.getProperty("db.pool.minIdle", "5")));
        config.setConnectionTimeout(Long.parseLong(
                props.getProperty("db.pool.connectionTimeout", "30000")));
        config.setIdleTimeout(Long.parseLong(
                props.getProperty("db.pool.idleTimeout", "600000")));
        config.setMaxLifetime(Long.parseLong(
                props.getProperty("db.pool.maxLifetime", "1800000")));

        this.dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}