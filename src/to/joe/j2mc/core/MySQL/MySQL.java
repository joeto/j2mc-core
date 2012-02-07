package to.joe.j2mc.core.MySQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL {

    private final String mySQLDatabase;
    private final String mySQLUsername;
    private final String mySQLPassword;

    public MySQL(String database, String username, String password) {
        this.mySQLDatabase = database;
        this.mySQLUsername = username;
        this.mySQLPassword = password;
    }

    /**
     * Hot and ready for consumption
     * 
     * @param query
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public PreparedStatement getFreshPreparedStatementHotFromTheOven(String query) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        connection = this.getConnection();
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
    public PreparedStatement getFreshPreparedStatementWithGeneratedKeys(String query) throws SQLException, ClassNotFoundException{
    	Connection conn = null;
    	conn = this.getConnection();
    	final PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    	return ps;
    }

    private Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection(this.mySQLDatabase + "?autoReconnect=true&user=" + this.mySQLUsername + "&password=" + this.mySQLPassword);
    }
}
