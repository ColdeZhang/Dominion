package cn.lunadeer.dominion.utils.databse.exceptions;

public class DatabaseException extends RuntimeException {

    private String str;

    public DatabaseException(String sql, Object... fields) {
        super(sql);
        str = sql;
        for (Object field : fields) {
            str = str.replaceFirst("\\?", field.toString());
        }
    }

    @Override
    public String getMessage() {
        return str;
    }
}
