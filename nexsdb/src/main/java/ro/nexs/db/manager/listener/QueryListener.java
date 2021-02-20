package ro.nexs.db.manager.listener;

import ro.nexs.db.manager.response.Query;

import java.util.List;

public class QueryListener<T> {

    private Class<T> type;

    /**
     * This class has to be overwritten in case you
     * want to access the listener methods!
     * <p>
     * Default constructor.
     * <p>
     * Doesn't do anything special; this is the
     * constructor that should be overwritten in case
     * you ever need to add something new to it.
     * <p>
     * Simply call the @Override annotation
     * to do so.
     */
    public QueryListener() {
    }

    /**
     * Method called whenever a query towards the
     * database is complete.
     */
    public void onQueryComplete(Query query) {
    }

    /**
     * Method called whenever a query is sent towards
     * the database, but not yet completed.
     *
     * @param query The {@link Query} object itself.
     */
    public void onQuerySent(Query query) {
    }

    /**
     * Method called whenever there hasn't been
     * any data found within our query.
     */
    public void onNoDataFound() {
    }

    /**
     * This method is called only when a single
     * result has been received from a query.
     *
     * @param result The result typedef itself.
     */
    public void onSingleResultReceived(T result) {
    }

    /**
     * This method is called only when multiple
     * results have been received from within a query.
     *
     * @param results The results' typedef itself.
     */
    public void onMultipleResultReceived(List<T> results) {
    }
}
