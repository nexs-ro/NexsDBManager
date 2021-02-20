package ro.nexs.db.manager.exception;

public class ConnectionNotFoundException extends Exception {

    /**
     * Exception which is thrown whenever the
     * {@link ro.nexs.db.manager.connection.DBConnection} object
     * was not found.
     *
     * @param errorMessage The error message itself.
     */
    public ConnectionNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
