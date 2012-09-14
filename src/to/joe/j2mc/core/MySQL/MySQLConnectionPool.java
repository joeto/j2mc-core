package to.joe.j2mc.core.MySQL;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MySQLConnectionPool implements Closeable {

    private final static int poolsize = 6;
    private final static long timeToLive = 300000;
    private final Vector<JDCConnection> connections;
    private final ConnectionReaper reaper;
    private final String url;
    private final Lock lock = new ReentrantLock();

    public MySQLConnectionPool(String url) throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        this.url = url;
        connections = new Vector<JDCConnection>(poolsize);
        reaper = new ConnectionReaper();
        reaper.start();
    }

    @Override
    public void close() {
        lock.lock();
        final Enumeration<JDCConnection> conns = connections.elements();
        while (conns.hasMoreElements()) {
            final JDCConnection conn = conns.nextElement();
            connections.remove(conn);
            conn.terminate();
        }
        lock.unlock();
    }

    public Connection getConnection() throws SQLException {
        lock.lock();
        try {
            final Enumeration<JDCConnection> conns = connections.elements();
            while (conns.hasMoreElements()) {
                final JDCConnection conn = conns.nextElement();
                if (conn.lease()) {
                    if (conn.isValid()) {
                        return conn.getConnection();
                    }
                    connections.remove(conn);
                    conn.terminate();
                }
            }
            final JDCConnection conn = new JDCConnection(DriverManager.getConnection(url));
            conn.lease();
            if (!conn.isValid()) {
                conn.terminate();
                throw new SQLException("Failed to validate a brand new connection");
            }
            connections.add(conn);
            return conn.getConnection();
        } finally {
            lock.unlock();
        }
    }

    private void reapConnections() {
        lock.lock();
        final long stale = System.currentTimeMillis() - timeToLive;
        final Iterator<JDCConnection> itr = connections.iterator();
        while (itr.hasNext()) {
            final JDCConnection conn = itr.next();
            if (conn.inUse() && stale > conn.getLastUse() && !conn.isValid())
                itr.remove();
        }
        lock.unlock();
    }

    private class ConnectionReaper extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(300000);
                } catch (final InterruptedException e) {
                }
                reapConnections();
            }
        }
    }

    private class JDCConnection {
        private final Connection conn;
        private boolean inuse;
        private long timestamp;

        JDCConnection(Connection conn) {
            this.conn = conn;
            inuse = false;
            timestamp = 0;
        }

        @SuppressWarnings("unused")
        public void close() {
            inuse = false;
            try {
                if (!conn.getAutoCommit()) {
                    conn.setAutoCommit(true);
                }
            } catch (final SQLException ex) {
                connections.remove(conn);
                terminate();
            }
        }

        long getLastUse() {
            return timestamp;
        }

        synchronized Connection getConnection() {
            return this.conn;
        }

        boolean inUse() {
            return inuse;
        }

        boolean isValid() {
            try {
                return conn.isValid(1);
            } catch (final SQLException ex) {
                return false;
            }
        }

        synchronized boolean lease() {
            if (inuse)
                return false;
            inuse = true;
            timestamp = System.currentTimeMillis();
            return true;
        }

        void terminate() {
            try {
                conn.close();
            } catch (final SQLException ex) {
            }
        }
    }
}
