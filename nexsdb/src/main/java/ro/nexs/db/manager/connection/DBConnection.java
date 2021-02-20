/*
Copyright (c) thmihnea <mihneathm@gmail.com>
Copyright (c) contributors
This file is part of NexsDBManager, licensed under the MIT License.
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE..
 */

package ro.nexs.db.manager.connection;

import lombok.Getter;
import lombok.Setter;
import ro.nexs.db.manager.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@Setter
public class DBConnection {

    /**
     * This is the {@link java.sql.Connection}
     * which will be used for when we attempt a direct
     * connection to our database.
     */
    private Connection connection;

    /**
     * These are variables used for setting up
     * the {@link java.sql.Connection} toward the
     * SQL server for when we generate the {@link DBConnection}
     * object.
     * The {@link #database} field is not necessarily needed
     * to be set, as if someone wants to utilize the general
     * localhost connection in order to generate new databases
     * (if the user wishes so).
     */
    private String host, database, username, password;

    /**
     * This is the port of the MySQL database connection
     * Default port used for a {@link java.sql.Connection}
     * is 3306.
     * Set with care.
     */
    private int port;

    /**
     * Constructor for a connection which doesn't need the
     * {@link #database} parameter.
     * This allows you to generate new databases.
     * @param host The host connection to the
     *             database.
     * @param port The port of the MySQL database user
     *             for setting up the connection.
     * @param username Username of the account which
     *                 manages the database.
     * @param password The password needed to access the
     *                 connection.
     */
    public DBConnection(String host, int port, String username, String password) {
        this(host, port, "", username, password);
    }

    /**
     * Constructor for a connection which requires an
     * actual database passed as a parameter.
     * This won't allow you to create new databases,
     * so use with care. Generally, a connection of this
     * type can be used for managing an already existing
     * database.
     * @param host The host connection to the
     *             database.
     * @param port The port of the MySQL database user
     *             for setting up the connection.
     * @param database The database name which we'll set
     *                 up the connection for.
     * @param username Username of the account which
     *                 manages the database.
     * @param password The password needed to access the
     *                 connection.
     */
    public DBConnection(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.connect(this.host, this.port, this.database, this.username, this.password);
    }

    /**
     * Method used for connecting towards
     * the database.
     * Called whenever our object is being instantiated.
     */
    public void connect(String host, int port, String database, String username, String password) {
        Logger INFO_LOGGER = Objects.requireNonNull(Util.getConnectionLogger(Level.INFO));
        Logger SEVERE_LOGGER = Objects.requireNonNull(Util.getConnectionLogger(Level.SEVERE));
        Util.log(INFO_LOGGER, "Attempting to initialize a connection to the database.");
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            Util.log(SEVERE_LOGGER, "The class `com.mysql.jdbc.Driver` was not found on your device! Please install this dependency before attempting to utilize the DBManager library.");
            e.printStackTrace();
        }
        try {
            synchronized (this) {
                if (this.connection != null && !(this.connection.isClosed())) {
                    Util.log(INFO_LOGGER, "The connection has already been set. Note that switching connections might cause unexpected errors!");
                }
                this.connection = DriverManager
                        .getConnection(Util.SQL_DEFAULT_URL + host + ":" + port + "/" + database, username, password);
            }
            Util.log(INFO_LOGGER, "Successfully established a connection towards the database. Displaying data:");
            Util.log(INFO_LOGGER, "Host: " + host);
            Util.log(INFO_LOGGER, "Port: " + port);
            Util.log(INFO_LOGGER, "Database: " + database);
            Util.log(INFO_LOGGER, "Username: " + username);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            Util.log(SEVERE_LOGGER, "A connection couldn't be established to the database. Consult the stacktrace above and try to solve the error!");
        }
    }

    /**
     * Returns an instance of the {@link Connection}
     * object used for assuring a safe connection
     * towards the database.
     * @return {@link Connection}
     */
    public Connection getConnection() {
        return this.connection;
    }

    /**
     * Sets the connection inside our {@link DBConnection}
     * object.
     * @param connection The {@link Connection} itself.
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * Returns the host of the
     * {@link Connection} towards the SQL
     * server.
     * @return {@link String}
     */
    public String getHost() {
        return this.host;
    }

    /**
     * Method used for setting the
     * {@link #host} of the {@link Connection}
     * towards the SQL server.
     * @param host The {@link String} host
     *             parameter itself.
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Connects to the root of the dataase, allowing
     * the creation of new databases inside of it.
     * This method pretty much allows the user
     * to tab out from any database at any given timestamp.
     */
    public void connectToRoot() {
        this.database = "";
        this.connect(this.host, this.port, "", this.username, this.password);
    }

    /**
     * Switches the database of the {@link Connection}.
     * Utilized for when a developer decides on using multiple
     * databases for his activities.
     * @param newDatabase The new database name. Beware, this is
     *                    case-sensitive!
     */
    public void switchDatabase(String newDatabase) {
        this.database = newDatabase;
        this.connect(this.host, this.port, newDatabase, this.username, this.password);;
    }

    /**
     * Switches the hostname of the {@link Connection}.
     * Utilized for when a developer decides on using
     * multiple connections and requires the same
     * object.
     * @param newHost The new hostname of the {@link DBConnection}
     *                object.
     */
    public void switchHost(String newHost) {
        this.host = newHost;
        this.connect(newHost, this.port, this.database, this.username, this.password);
    }

    /**
     * Switches the port of the {@link Connection}.
     * Utilized for when a developer decides on using
     * multiple ports and requires the same {@link DBConnection}
     * object.
     * @param newPort The new port of the {@link DBConnection}
     *                object.
     */
    public void switchPort(int newPort) {
        this.port = newPort;
        this.connect(this.host, newPort, this.database, this.username, this.password);
    }

    /**
     * Method used in case you want to switch the
     * user a {@link DBConnection} points to.
     * @param newUsername The new username of the user.
     * @param newPassword The new password of the user.
     */
    public void switchUser(String newUsername, String newPassword) {
        this.username = newUsername;
        this.password = newPassword;
        this.connect(this.host, this.port, this.database, newUsername, newPassword);
    }


}
