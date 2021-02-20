package ro.nexs.db.manager.exception;

public class DatabaseCreationException extends Exception {

    /**
     * Returns a {@link DatabaseCreationException} in case
     * there hasn't been any data found inside of the
     * table.
     *
     * @param errorMessage The error message which shall be displayed
     *                     to the console.
     */
    public DatabaseCreationException(String errorMessage) {
        super(errorMessage);
    }
}
