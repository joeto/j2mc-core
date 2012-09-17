package to.joe.j2mc.core.MySQL;

import java.sql.*;

import to.joe.j2mc.core.J2MC_Core;

public class MySQL {

    private final String mySQLDatabase;
    private final String mySQLUsername;
    private final String mySQLPassword;
    private MySQLConnectionPool pool;
    private J2MC_Core plugin;

    public MySQL(String database, String username, String password, J2MC_Core plugin) throws ClassNotFoundException, SQLException {
        this.mySQLDatabase = database;
        this.mySQLUsername = username;
        this.mySQLPassword = password;
        this.plugin = plugin;
        Class.forName("com.mysql.jdbc.Driver");
        pool = new MySQLConnectionPool(this.mySQLDatabase + "?autoReconnect=true&user=" + this.mySQLUsername + "&password=" + this.mySQLPassword);
        
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            
            @Override
            public void run() {
                pool.resetLoads();
            }
            
        }, 100, 100);
        
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
        final PreparedStatement preparedStatement = pool.getConnection().prepareStatement(query);
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
        final PreparedStatement ps = pool.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        return ps;
    }
    
    /**
     * Gets a connection from the connection pool
     * @throws SQLException 
     */
    public Connection getConnection() throws SQLException {
        return pool.getConnection();
    }
}
