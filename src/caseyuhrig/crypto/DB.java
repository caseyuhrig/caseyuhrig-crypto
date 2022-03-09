package caseyuhrig.crypto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DB
{
    public Connection getConnection()
            throws SQLException
    {
        final var url = "jdbc:postgresql://localhost/crypto";
        final var props = new Properties();
        props.setProperty("user", "crypto");
        props.setProperty("password", "crypto");
        props.setProperty("ssl", "false");
        final var connection = DriverManager.getConnection(url, props);
        return connection;
    }
}
