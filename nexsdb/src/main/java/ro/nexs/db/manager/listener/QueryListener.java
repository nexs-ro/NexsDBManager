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
