package ro.nexs.db.manager.manager;

import com.mysql.cj.jdbc.Blob;
import ro.nexs.db.manager.connection.DBConnection;
import ro.nexs.db.manager.exception.DatabaseCreationException;
import ro.nexs.db.manager.exception.NoDataFoundException;
import ro.nexs.db.manager.listener.QueryListener;
import ro.nexs.db.manager.response.Query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class DBManager {

    /**
     * The {@link DBConnection} object which will
     * be used for each individual query done towards
     * our database.
     * <p>
     * We require only a specific {@link DBConnection} because
     * this is under no circumstance a static class.
     * <p>
     * Each {@link DBConnection} object/class has its own
     * {@link DBManager} object which manages the connection.
     * <p>
     * This is done to better organize the work done.
     * In case you ever need to change the database/connection,
     * simply change the fields within the {@link DBConnection}
     * object. You can also use {@link java.lang.reflect} methods
     * in case you really feel like manually changing the {@link DBConnection}
     * object is your only way around a certain problem.
     */
    private final DBConnection dbConnection;

    /**
     * The {@link ro.nexs.db.manager.response.Query} object
     * which will be used for listening to queries sent
     * by this {@link DBManager} object.
     */
    private QueryListener queryListener;

    /**
     * Method used for setting the {@link QueryListener}.
     *
     * @param queryListener The {@link QueryListener} object
     *                      itself.
     */
    public <T> void setQueryListener(QueryListener<T> queryListener) {
        this.queryListener = queryListener;
    }

    /**
     * Method used for returning the {@link QueryListener}
     * object.
     *
     * @param <T> The typedef.
     * @return {@link QueryListener}
     */
    public <T> QueryListener getQueryListener() {
        return this.queryListener;
    }

    /**
     * Default constructor.
     * Takes no parameters but is also useless.
     */
    public DBManager() {
        this.dbConnection = null;
    }

    /**
     * Default constructor.
     * This acts as the constructor above, but it also
     * takes into account the {@link DBConnection} object
     * for which we've created this class.
     *
     * @param dbConnection The {@link DBConnection} object
     *                     itself.
     */
    public DBManager(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    /**
     * Utility method which returns the {@link PreparedStatement}
     * object determined by a specific query as input.
     *
     * @param query The queried {@link String}
     * @return {@link PreparedStatement}
     */
    public PreparedStatement getPreparedStatement(String query) {
        try {
            assert this.dbConnection != null;
            return this.dbConnection
                    .getConnection().prepareStatement(query);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    /**
     * Executes an SQL query from within
     * a {@link Statement} object.
     *
     * @param query The SQL query parameter
     *              itself.
     */
    public void executeStatement(String query) {
        try {
            assert this.dbConnection != null;
            Statement statement = this.dbConnection
                    .getConnection().createStatement();
            statement.executeUpdate(query);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * Utility method used for asynchronously executing
     * a new query towards the database.
     *
     * @param preparedStatement The {@link PreparedStatement} which
     *                          we will attempt at executing.
     */
    public void execute(PreparedStatement preparedStatement) {
        CompletableFuture.runAsync(() -> {
            try {
                preparedStatement.execute();
                Query query = new Query(preparedStatement.toString(), this.dbConnection.getDatabase());
                if (this.queryListener != null) {
                    this.queryListener.onQuerySent(query);
                }
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Utility method used for asynchronously executing
     * an update towards the database.
     *
     * @param preparedStatement The {@link PreparedStatement} which
     *                          we will attempt to execute.
     */
    public void executeUpdate(PreparedStatement preparedStatement) {
        CompletableFuture.runAsync(() -> {
            try {
                preparedStatement.executeUpdate();
                Query query = new Query(preparedStatement.toString(), this.dbConnection.getDatabase());
                if (this.queryListener != null) {
                    this.queryListener.onQuerySent(query);
                }
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Utility method used for asynchronously executing
     * a new query towards the database.
     *
     * @param preparedStatement The {@link PreparedStatement} which
     *                          we will attempt at executing.
     */
    public ResultSet executeQuery(PreparedStatement preparedStatement) {
        CompletableFuture<ResultSet> res = CompletableFuture.supplyAsync(() -> {
            try {
                Query query = new Query(preparedStatement.toString(), this.dbConnection.getDatabase());
                if (this.queryListener != null) {
                    this.queryListener.onQueryComplete(query);
                }
                return preparedStatement.executeQuery();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
                return null;
            }
        });
        try {
            return res.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method used for creating a new database inside
     * our SQL server.
     *
     * @param database The database name itself.
     * @throws DatabaseCreationException in case the {@link DBConnection}'s database
     *                                   is already set.
     */
    public void createDatabase(String database) throws DatabaseCreationException {
        assert this.dbConnection != null;
        if (!(this.dbConnection.getDatabase().equals(""))) {
            throw new DatabaseCreationException("A database can't be created because your DBConnection points to an already existing database. Please switch to root.");
        }
        String query = "CREATE DATABASE IF NOT EXISTS `" + database + "`";
        this.executeStatement(query);
    }

    /**
     * Returns whether or not a specified value
     * exists in our table.
     * Requires the table name, the field which we will
     * access and the typedef of the object we're
     * searching for.
     *
     * @param tableName The given tablename.
     * @param field     The given field.
     * @param type      The given type.
     * @param <T>       Typedef.
     * @return {@link Boolean}
     */
    public <T> boolean exists(String tableName, String field, T type) {
        PreparedStatement preparedStatement = this.getPreparedStatement("SELECT * FROM `" + tableName + "` WHERE " + field + " = ?");
        try {
            preparedStatement.setObject(1, type);
            ResultSet resultSet = this.executeQuery(preparedStatement);
            return resultSet.next();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
    }

    /**
     * Method used for generifying the query algorithm
     * into our {@link DBConnection} database.
     * <p>
     * This should only be used in case your object
     * doesn't appear in our object list!
     *
     * @param tableName The table name.
     * @param getField  The field which we want to get.
     *                  Example: We want to get the amount of coins
     *                  a user has, therefore the getField will be
     *                  "COINS".
     * @param keyField  The field which we're performing to lookup
     *                  after. Example: We want to know how many coins X
     *                  has and we know X's name, therefore the keyField
     *                  will be "NAME".
     * @param key       The key itself.
     * @param clazz     The {@link Class} which the result should be given us.
     *                  You want a String, utilize String.class etc.
     * @param <T>       The typedef.
     * @return {@link T}
     * @throws NoDataFoundException in case there's no data to be found
     *                              also known as if the {@link ResultSet} is empty.
     */
    public <T> T get(String tableName, String getField, String keyField, Object key, Class<T> clazz) throws NoDataFoundException {
        String query = "SELECT * FROM `" + tableName + "` WHERE " + keyField + " = ?";
        PreparedStatement preparedStatement = this.getPreparedStatement(query);
        Query queryObject = new Query(preparedStatement.toString(), this.dbConnection.getDatabase());
        if (this.queryListener != null) {
            this.queryListener.onQueryComplete(queryObject);
        }
        try {
            preparedStatement.setObject(1, key);
            ResultSet resultSet = this.executeQuery(preparedStatement);
            if (!(resultSet.next())) {
                throw new NoDataFoundException("No results were found wile attempting lookup for " + key.toString() + " in field " + keyField + " in database " + getField);
            }
            return resultSet.getObject(getField, clazz);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    /**
     * Method used for generifying the algorithm for
     * setting a specific object in our database/replacing
     * the old one.
     *
     * @param tableName The table name itself.
     * @param setField  The field which we want to update.
     * @param keyField  The field which we're keying our lookup
     *                  after.
     * @param set       The new object.
     * @param key       The key
     * @param <T>       The typedef.
     */
    public <T> void set(String tableName, String setField, String keyField, Object set, Object key) {
        String query = "UPDATE " + tableName + " SET " + setField + " = ? WHERE " + keyField + " = ?";
        PreparedStatement preparedStatement = this.getPreparedStatement(query);
        try {
            preparedStatement.setObject(1, set);
            preparedStatement.setObject(2, key);
            this.executeUpdate(preparedStatement);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * Method used for setting a {@link String}
     * in our database.
     *
     * @param tableName The table name itself.
     * @param setField  The field which we want to update.
     * @param keyField  The field which we're keying our lookup
     *                  after.
     * @param set       The new object.
     * @param key       The key
     */
    public void setString(String tableName, String setField, String keyField, String set, Object key) {
        set(tableName, setField, keyField, set, key);
    }

    /**
     * Method used for setting a {@link Integer}
     * in our database.
     *
     * @param tableName The table name itself.
     * @param setField  The field which we want to update.
     * @param keyField  The field which we're keying our lookup
     *                  after.
     * @param set       The new object.
     * @param key       The key
     */
    public void setInt(String tableName, String setField, String keyField, int set, Object key) {
        set(tableName, setField, keyField, set, key);
    }

    /**
     * Method used for setting a {@link Boolean}
     * in our database.
     *
     * @param tableName The table name itself.
     * @param setField  The field which we want to update.
     * @param keyField  The field which we're keying our lookup
     *                  after.
     * @param set       The new object.
     * @param key       The key
     */
    public void setBoolean(String tableName, String setField, String keyField, boolean set, Object key) {
        set(tableName, setField, keyField, set, key);
    }

    /**
     * Method used for setting a {@link Blob}
     * in our database.
     *
     * @param tableName The table name itself.
     * @param setField  The field which we want to update.
     * @param keyField  The field which we're keying our lookup
     *                  after.
     * @param set       The new object.
     * @param key       The key
     */
    public void setBlob(String tableName, String setField, String keyField, Blob set, Object key) {
        set(tableName, setField, keyField, set, key);
    }

    /**
     * Method used for setting a {@link Long}
     * in our database.
     *
     * @param tableName The table name itself.
     * @param setField  The field which we want to update.
     * @param keyField  The field which we're keying our lookup
     *                  after.
     * @param set       The new object.
     * @param key       The key
     */
    public void setLong(String tableName, String setField, String keyField, long set, Object key) {
        set(tableName, setField, keyField, set, key);
    }

    /**
     * Method used for returning a {@link String} object
     * by performing a lookup in our database.
     * <p>
     * Utilizes the general method {@link DBManager#get(String, String, String, Object, Class)}
     * which returns a typedef.
     *
     * @param tableName The table name.
     * @param getField  The field which we want to get.
     *                  Example: We want to get the amount of coins
     *                  a user has, therefore the getField will be
     *                  "COINS".
     * @param keyField  The field which we're performing to lookup
     *                  after. Example: We want to know how many coins X
     *                  has and we know X's name, therefore the keyField
     *                  will be "NAME".
     * @param key       The key itself.
     * @return {@link String}
     * @throws NoDataFoundException in case there's no data to be found
     *                              also known as if the {@link ResultSet} is empty.
     */
    public String getString(String tableName, String getField, String keyField, Object key) throws NoDataFoundException {
        return get(tableName, getField, keyField, key, String.class);
    }

    /**
     * Method used for returning a {@link Integer} object
     * by performing a lookup in our database.
     * <p>
     * Utilizes the general method {@link DBManager#get(String, String, String, Object, Class)}
     * which returns a typedef.
     *
     * @param tableName The table name.
     * @param getField  The field which we want to get.
     *                  Example: We want to get the amount of coins
     *                  a user has, therefore the getField will be
     *                  "COINS".
     * @param keyField  The field which we're performing to lookup
     *                  after. Example: We want to know how many coins X
     *                  has and we know X's name, therefore the keyField
     *                  will be "NAME".
     * @param key       The key itself.
     * @return {@link Integer}
     * @throws NoDataFoundException in case there's no data to be found
     *                              also known as if the {@link ResultSet} is empty.
     */
    public int getInt(String tableName, String getField, String keyField, Object key) throws NoDataFoundException {
        return get(tableName, getField, keyField, key, Integer.class);
    }

    /**
     * Method used for returning a {@link Boolean} object
     * by performing a lookup in our database.
     * <p>
     * Utilizes the general method {@link DBManager#get(String, String, String, Object, Class)}
     * which returns a typedef.
     *
     * @param tableName The table name.
     * @param getField  The field which we want to get.
     *                  Example: We want to get the amount of coins
     *                  a user has, therefore the getField will be
     *                  "COINS".
     * @param keyField  The field which we're performing to lookup
     *                  after. Example: We want to know how many coins X
     *                  has and we know X's name, therefore the keyField
     *                  will be "NAME".
     * @param key       The key itself.
     * @return {@link Boolean}
     * @throws NoDataFoundException in case there's no data to be found
     *                              also known as if the {@link ResultSet} is empty.
     */
    public boolean getBoolean(String tableName, String getField, String keyField, Object key) throws NoDataFoundException {
        return get(tableName, getField, keyField, key, Boolean.class);
    }

    /**
     * Method used for returning a {@link Blob} object
     * by performing a lookup in our database.
     * <p>
     * Utilizes the general method {@link DBManager#get(String, String, String, Object, Class)}
     * which returns a typedef.
     *
     * @param tableName The table name.
     * @param getField  The field which we want to get.
     *                  Example: We want to get the amount of coins
     *                  a user has, therefore the getField will be
     *                  "COINS".
     * @param keyField  The field which we're performing to lookup
     *                  after. Example: We want to know how many coins X
     *                  has and we know X's name, therefore the keyField
     *                  will be "NAME".
     * @param key       The key itself.
     * @return {@link Blob}
     * @throws NoDataFoundException in case there's no data to be found
     *                              also known as if the {@link ResultSet} is empty.
     */
    public Blob getBlob(String tableName, String getField, String keyField, Object key) throws NoDataFoundException {
        return get(tableName, getField, keyField, key, Blob.class);
    }

    /**
     * Method used for returning a {@link Long} object
     * by performing a lookup in our database.
     * <p>
     * Utilizes the general method {@link DBManager#get(String, String, String, Object, Class)}
     * which returns a typedef.
     *
     * @param tableName The table name.
     * @param getField  The field which we want to get.
     *                  Example: We want to get the amount of coins
     *                  a user has, therefore the getField will be
     *                  "COINS".
     * @param keyField  The field which we're performing to lookup
     *                  after. Example: We want to know how many coins X
     *                  has and we know X's name, therefore the keyField
     *                  will be "NAME".
     * @param key       The key itself.
     * @return {@link Long}
     * @throws NoDataFoundException in case there's no data to be found
     *                              also known as if the {@link ResultSet} is empty.
     */
    public Long getLong(String tableName, String getField, String keyField, Object key) throws NoDataFoundException {
        return get(tableName, getField, keyField, key, Long.class);
    }

}
