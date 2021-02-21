package ro.nexs.db.manager.exception;

public class DifferentArgLengthException extends Exception {

    /**
     * This is the default constructor for the
     * {@link DifferentArgLengthException} which is thrown
     * whenever trying to insert a value and you introduce more arguments
     * than fields or less fields than arguments.
     *
     * @param errorMessage The error message that should be
     *                     displayed.
     */
    public DifferentArgLengthException(String errorMessage) {
        super(errorMessage);
    }
}
