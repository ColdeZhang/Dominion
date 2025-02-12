package cn.lunadeer.dominion.utils.databse.exceptions;

public class QueryException extends DatabaseException {
    public QueryException(String sql, Object... params) {
        super("Query Failed: " + sql, params);
    }
}
