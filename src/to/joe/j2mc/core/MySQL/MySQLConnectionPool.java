package to.joe.j2mc.core.MySQL;

import to.joe.j2mc.core.J2MC_Manager;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MySQLConnectionPool implements Closeable {

    private final static int poolSize = 4;
    private final JDCConnection[] connections;
    private final String url;
    private final Lock lock = new ReentrantLock();

    public MySQLConnectionPool(String url) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        this.url = url;
        connections = new JDCConnection[poolSize];
        for (int i = 0; i < connections.length; i++) {
            connections[i] = new JDCConnection(DriverManager.getConnection(url));
        }
    }

    @Override
    public void close() {
        lock.lock();
        for (JDCConnection connection : connections) {
            connection.terminate();
        }
        lock.unlock();
    }
    
    public void resetLoads() {
        lock.lock();
        for (JDCConnection connection : connections) {
            connection.setLoad(0);
        }
        lock.unlock();
    }
    
    public JDCConnection[] getConnections() {
        return this.connections;
    }

    public Connection getConnection() throws SQLException {
        lock.lock();
        try {
            int lowestIndex = 0;
            int lowestLoad = Integer.MAX_VALUE;
            for (int i = 0; i < connections.length; i++) {
                JDCConnection connection = connections[i];
                if (!connection.getConnection().isValid(1)) {
                    connection.terminate();
                    connections[i] = new JDCConnection(DriverManager.getConnection(url));
                }
                if (connection.getLoad() < lowestLoad) {
                    lowestLoad = connection.getLoad();
                    lowestIndex = i;
                }
            }

            connections[lowestIndex].incrementLoad();
            return connections[lowestIndex].getConnection();
        } finally {
            lock.unlock();
        }
    }

    public class JDCConnection {
        private final Connection conn;
        private int load = 0;

        JDCConnection(Connection conn) {
            this.conn = conn;
        }

        public void close() {
            try {
                if (!conn.getAutoCommit()) {
                    conn.setAutoCommit(true);
                }
            } catch (final SQLException ex) {
                terminate();
            }
        }

        synchronized Connection getConnection() {
            return this.conn;
        }

        public int getLoad() {
            return this.load;
        }

        public void setLoad(int load) {
            this.load = load;
        }

        public void incrementLoad() {
            incrementLoad(1);
        }

        public void incrementLoad(int factor) {
            this.load += factor;
        }

        void terminate() {
            try {
                conn.close();
            } catch (final SQLException ex) {
                J2MC_Manager.getCore().getLogger().warning("SQLException while terminating pool connection: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}
