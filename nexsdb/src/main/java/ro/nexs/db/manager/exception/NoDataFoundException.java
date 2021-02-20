package ro.nexs.db.manager.exception;

public class NoDataFoundException extends Exception {

    /**
     * Returns a {@link NoDataFoundException} in case
     * there hasn't been any data found inside of the
     * table.
     *
     * @param errorMessage The error message which shall be displayed
     *                     to the console.
     */
    public NoDataFoundException(String errorMessage) {
        super(errorMessage);
    }
}
