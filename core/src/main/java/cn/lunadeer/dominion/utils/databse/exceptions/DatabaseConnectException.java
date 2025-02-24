package cn.lunadeer.dominion.utils.databse.exceptions;

public class DatabaseConnectException extends DatabaseException {
    public DatabaseConnectException(String reason) {
        super(reason);
    }
}
