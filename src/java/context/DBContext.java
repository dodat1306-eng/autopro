package context;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBContext {

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/car_market"
            + "?useUnicode=true"
            + "&characterEncoding=UTF-8"
            + "&serverTimezone=Asia/Ho_Chi_Minh"
            + "&useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&cachePrepStmts=true"
            + "&prepStmtCacheSize=250"
            + "&prepStmtCacheSqlLimit=2048"
            + "&connectTimeout=10000"
            + "&socketTimeout=30000";
    private static final String URL = getConfig("AUTOPRO_DB_URL", DEFAULT_URL);
    private static final String USER = getConfig("AUTOPRO_DB_USER", "root");
    private static final String PASS = getConfig("AUTOPRO_DB_PASSWORD", "");

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static String getConfig(String key, String defaultValue) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.trim().isEmpty()) {
            return systemValue.trim();
        }

        String envValue = System.getenv(key);
        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue.trim();
        }
        return defaultValue;
    }

    public Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    protected boolean hasColumn(Connection conn, String tableName, String columnName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getColumns(conn.getCatalog(), null, tableName, columnName)) {
            return rs.next();
        }
    }
}
