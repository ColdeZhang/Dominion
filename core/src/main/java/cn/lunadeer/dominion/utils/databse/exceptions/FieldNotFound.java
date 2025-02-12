package cn.lunadeer.dominion.utils.databse.exceptions;

public class FieldNotFound extends DatabaseException {
    public FieldNotFound(String table, String field) {
        super("Table: " + table + ", Field: " + field + " not found");
    }
}
