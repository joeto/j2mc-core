package to.joe.j2mc.core.MySQL;

import java.sql.*;

import to.joe.j2mc.core.exceptions.LostSQLConnectionException;

public class MySQL {

    private final String mySQLDatabase;
    private final String mySQLUsername;
    private final String mySQLPassword;

    public MySQL(String database, String username, String password) {
        this.mySQLDatabase = database;
        this.mySQLUsername = username;
        this.mySQLPassword = password;
    }

    public void execute(PreparedStatement preparedStatement) throws SQLException, LostSQLConnectionException, ClassNotFoundException {
        Connection connection = null;
        connection = this.getConnection();
        if (connection == null) {
            throw new LostSQLConnectionException();
        }
        preparedStatement.execute();
    }

    public ResultSet executeQuery(PreparedStatement preparedStatement) throws SQLException, LostSQLConnectionException, ClassNotFoundException {
        Connection connection = null;
        connection = this.getConnection();
        if (connection == null) {
            throw new LostSQLConnectionException();
        }
        return preparedStatement.executeQuery();
    }

    public PreparedStatement getFreshPreparedStatementHotFromTheOven(String query) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        connection = this.getConnection();
        final PreparedStatement preparedStatement = connection.prepareStatement(query);
        return preparedStatement;
    }

    private Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection(this.mySQLDatabase + "?autoReconnect=true&user=" + this.mySQLUsername + "&password=" + this.mySQLPassword);
    }
}
