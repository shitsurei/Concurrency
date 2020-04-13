package Chapter7;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * 03
 * 享元模式实现连接池
 */
@Slf4j
public class Pool {
//    TODO:连接的动态增长和收缩，连接保活（可用性检测），等待超时处理，分布式hash
    public static void main(String[] args) {
        Pool pool = new Pool(3);
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                Connection connection = pool.borrow();
                try {
//                    模拟使用连接池工作
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    pool.free(connection);
                }
            }, "t" + i).start();
        }
        /**
         * 2020-04-13 19:54:39.444 [t2] INFO  Chapter7.Pool - 获取连接：MockConnection{name='连接3'}
         * 2020-04-13 19:54:39.444 [t1] INFO  Chapter7.Pool - 获取连接：MockConnection{name='连接2'}
         * 2020-04-13 19:54:39.444 [t3] INFO  Chapter7.Pool - 无连接，等待……
         * 2020-04-13 19:54:39.444 [t0] INFO  Chapter7.Pool - 获取连接：MockConnection{name='连接1'}
         * 2020-04-13 19:54:39.453 [t4] INFO  Chapter7.Pool - 无连接，等待……
         * 2020-04-13 19:54:41.450 [t2] INFO  Chapter7.Pool - 归还连接:MockConnection{name='连接3'}
         * 2020-04-13 19:54:41.450 [t4] INFO  Chapter7.Pool - 获取连接：MockConnection{name='连接3'}
         * 2020-04-13 19:54:41.450 [t3] INFO  Chapter7.Pool - 无连接，等待……
         * 2020-04-13 19:54:41.457 [t0] INFO  Chapter7.Pool - 归还连接:MockConnection{name='连接1'}
         * 2020-04-13 19:54:41.457 [t1] INFO  Chapter7.Pool - 归还连接:MockConnection{name='连接2'}
         * 2020-04-13 19:54:41.457 [t3] INFO  Chapter7.Pool - 获取连接：MockConnection{name='连接1'}
         * 2020-04-13 19:54:43.452 [t4] INFO  Chapter7.Pool - 归还连接:MockConnection{name='连接3'}
         * 2020-04-13 19:54:43.462 [t3] INFO  Chapter7.Pool - 归还连接:MockConnection{name='连接1'}
         */
    }

    //    连接池大小
    private final int poolSize;
    //    连接池数组
    private Connection[] connections;
    //    连接状态数组
    private AtomicIntegerArray states;

    public Pool(int poolSize) {
        this.poolSize = poolSize;
        this.connections = new Connection[poolSize];
        this.states = new AtomicIntegerArray(poolSize);
        for (int i = 0; i < poolSize; i++) {
            connections[i] = new MockConnection("连接" + (i + 1));
        }
    }

    //    借连接
    public Connection borrow() {
        while (true) {
            for (int i = 0; i < poolSize; i++) {
                if (states.get(i) == 0) {
//                    CAS操作借连接
                    if (states.compareAndSet(i, 0, 1)) {
                        log.info("获取连接：" + connections[i]);
                        return connections[i];
                    }
                }
            }
//            如果没有空闲连接，进入等待区等待其他线程唤醒（如果一直自旋会耗费CPU资源，因此CAS适合短时间等待）
            synchronized (this) {
                log.info("无连接，等待……");
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //    归还连接
    public void free(Connection connection) {
        for (int i = 0; i < poolSize; i++) {
            if (connections[i] == connection) {
                states.set(i, 0);
                log.info("归还连接:" + connections[i]);
                synchronized (this) {
                    this.notifyAll();
                }
                break;
            }
        }
    }
}

class MockConnection implements Connection {
    private String name;

    public MockConnection(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MockConnection{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public Statement createStatement() throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return null;
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return null;
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return null;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {

    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return false;
    }

    @Override
    public void commit() throws SQLException {

    }

    @Override
    public void rollback() throws SQLException {

    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {

    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return false;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {

    }

    @Override
    public String getCatalog() throws SQLException {
        return null;
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {

    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return 0;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return null;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

    }

    @Override
    public void setHoldability(int holdability) throws SQLException {

    }

    @Override
    public int getHoldability() throws SQLException {
        return 0;
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return null;
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return null;
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {

    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {

    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return null;
    }

    @Override
    public Clob createClob() throws SQLException {
        return null;
    }

    @Override
    public Blob createBlob() throws SQLException {
        return null;
    }

    @Override
    public NClob createNClob() throws SQLException {
        return null;
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return null;
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return false;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {

    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {

    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return null;
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return null;
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return null;
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return null;
    }

    @Override
    public void setSchema(String schema) throws SQLException {

    }

    @Override
    public String getSchema() throws SQLException {
        return null;
    }

    @Override
    public void abort(Executor executor) throws SQLException {

    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {

    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
