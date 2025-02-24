package cn.lunadeer.dominion.utils.databse.exceptions;

public class TableNotFound extends DatabaseException {
    public TableNotFound(String table) {
        super("Table Not Found: ?", table);
    }
}
