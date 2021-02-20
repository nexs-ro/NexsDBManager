package ro.nexs.db.manager.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Query {

    /**
     * The database in which the query has
     * been executed.
     */
    private String database;

    /**
     * The query that has been actually executed via
     * SQL language. This is a stringified version of
     * the sent-out code.
     */
    private String query;

    /**
     * This is the Unique Identifier of the query
     * in case a developer ever needs to store them.
     */
    private UUID identifier;

    /**
     * Default consructor for the {@link Query} object.
     * Requires the query text itself as well as the
     * database which has been used for the query.
     * @param query The query text.
     * @param database The database itself.
     */
    public Query(String query, String database) {
        this.query = query;
        this.database = database;
        this.identifier = UUID.randomUUID();
    }

    /**
     * Constructor without any arguments
     * for the {@link Query} object.
     * Instantiates the 2 private fields with
     * empty string values that can later be replaced.
     */
    public Query() {
        this("", "");
    }
}
