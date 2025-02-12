package cn.lunadeer.dominion.utils.databse.syntax;

import cn.lunadeer.dominion.utils.databse.DatabaseManager;
import cn.lunadeer.dominion.utils.databse.SqlSyntax;
import cn.lunadeer.dominion.utils.databse.exceptions.QueryException;

import java.sql.ResultSet;

public class DeleteRow implements SqlSyntax {

    private String tableName;
    private String where;
    private Object[] whereArgs;

    public DeleteRow table(String name) {
        tableName = name;
        return this;
    }

    public DeleteRow where(String conditions, Object... args) {
        where = conditions;
        whereArgs = args;
        return this;
    }

    @Override
    public ResultSet execute() throws QueryException {
        StringBuilder sql = new StringBuilder("DELETE FROM " + tableName);
        if (where != null) {
            sql.append(" WHERE ").append(where);
        }
        return DatabaseManager.instance.query(sql.toString(), whereArgs);
    }
}
