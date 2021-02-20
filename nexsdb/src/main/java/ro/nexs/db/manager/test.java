package ro.nexs.db.manager;

import ro.nexs.db.manager.connection.DBConnection;

public class test {

    public static void main(String[] args) {
        DBConnection dbConnection = Util.dbConnection;
        dbConnection.connectToRoot();
        System.out.println(dbConnection.getDatabase());
    }
}
