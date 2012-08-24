package to.joe.j2mc.core.MySQL;

import java.sql.*;

public class MySQL {

    private final String mySQLDatabase;
    private final String mySQLUsername;
    private final String mySQLPassword;
    private Connection connection;

    public MySQL(String database, String username, String password) throws ClassNotFoundException, SQLException {
        this.mySQLDatabase = database;
        this.mySQLUsername = username;
        this.mySQLPassword = password;
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection(this.mySQLDatabase + "?autoReconnect=true&user=" + this.mySQLUsername + "&password=" + this.mySQLPassword);
    }

    /**
     * Hot and ready for consumption
     * 
     * @param query
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public PreparedStatement getFreshPreparedStatementHotFromTheOven(String query) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement(query);
        return preparedStatement;
    }

    /**
     * Delicious PreparedStatements but with a generate keys attached!
     * 
     * @param query
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public PreparedStatement getFreshPreparedStatementWithGeneratedKeys(String query) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        return ps;
    }
}
