package cn.lunadeer.dominion.utils.databse.exceptions;

public class DatabaseTypeNotSupport extends DatabaseException {
    public DatabaseTypeNotSupport(String type) {
        super("Database type not support: ?", type);
    }
}
