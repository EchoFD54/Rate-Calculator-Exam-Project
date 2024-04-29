package DAL;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.sql.Connection;


public class ConnectionManager {
    private final SQLServerDataSource ds;
    public ConnectionManager() {
        ds = new SQLServerDataSource();
        ds.setDatabaseName("rata_tech_db");
        ds.setUser("CSe2023b_e_16");
        ds.setPassword("CSe2023bE16#23");
        ds.setServerName("EASV-DB4");
        ds.setTrustServerCertificate(true);
    }

    public Connection getConnection() throws SQLServerException {
        return (Connection) ds.getConnection();
    }
}
