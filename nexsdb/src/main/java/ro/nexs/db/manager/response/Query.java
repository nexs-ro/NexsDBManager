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
