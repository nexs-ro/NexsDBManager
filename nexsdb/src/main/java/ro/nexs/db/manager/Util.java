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

package ro.nexs.db.manager;

import ro.nexs.db.manager.connection.DBConnection;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {

    /**
     * The default {@link java.net.URL} any single
     * {@link java.sql.Connection} object utilizes
     * for connecting to a MySQL server.
     */
    public final static String SQL_DEFAULT_URL = "jdbc:mysql://";

    /**
     * This is the {@link Logger} instance used
     * for logging data from the {@link DBConnection} class.
     * Utilized in case exceptions happen/so on and so forth.
     */
    private final static Logger CONNECTION_LOGGER_SEVERE = Logger.getLogger(DBConnection.class.getName());

    /**
     * This is the {@link Logger} instance used
     * for logging data from the {@link DBConnection} class.
     * Utilized in for general messaging.
     */
    private final static Logger CONNECTION_LOGGER_INFO = Logger.getLogger(DBConnection.class.getName());

    static {
        CONNECTION_LOGGER_SEVERE.setLevel(Level.SEVERE);
        CONNECTION_LOGGER_INFO.setLevel(Level.INFO);
    }

    /**
     * Returns an instance of the {@link #CONNECTION_LOGGER_SEVERE}
     * from the {@link Util} class.
     * Method allows easy access to the logger utilized
     * in the {@link DBConnection} class.
     * @return {@link Logger}
     */
    public static Logger getConnectionLogger(Level level) {
        switch (level.getName()) {
            case "INFO":
                return CONNECTION_LOGGER_INFO;
            case "SEVERE":
                return CONNECTION_LOGGER_SEVERE;
            default:
                return null;
        }
    }

    /**
     * Utility method used for logging a message
     * towards the console / IO interface.
     * Used for beautifying the code.
     * @param logger The {@link Logger} object
     *               required to log a message towards
     *               the console.
     * @param message The message itself.
     */
    public static void log(Logger logger, String message) {
        logger.log(logger.getLevel(), message);
    }
}
