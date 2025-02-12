package cn.lunadeer.dominion.utils.databse.syntax;

import cn.lunadeer.dominion.utils.databse.DatabaseManager;
import cn.lunadeer.dominion.utils.databse.Field;
import cn.lunadeer.dominion.utils.databse.SqlSyntax;
import cn.lunadeer.dominion.utils.databse.exceptions.QueryException;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UpdateRow implements SqlSyntax {

    private String tableName;
    private final List<Field> fields = new ArrayList<>();
    private String where;
    private Object[] whereArgs;
    private boolean returnAll = false;
    private Field keyField = null;

    public UpdateRow table(String name) {
        tableName = name;
        return this;
    }

    public UpdateRow field(Field field) {
        fields.add(field);
        return this;
    }

    public UpdateRow returningAll(Field keyField) {
        returnAll = true;
        this.keyField = keyField;
        return this;
    }

    public UpdateRow where(String conditions, Object... args) {
        where = conditions;
        whereArgs = args;
        return this;
    }

    @Override
    public ResultSet execute() throws QueryException {
        StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
        Object[] args = new Object[fields.size() + whereArgs.length];
        for (Field field : fields) {
            sql.append(field.name).append(" = ?, ");
            args[fields.indexOf(field)] = field.value;
        }
        System.arraycopy(whereArgs, 0, args, fields.size(), whereArgs.length);
        sql.delete(sql.length() - 2, sql.length());
        if (where != null) {
            sql.append(" WHERE ").append(where);
        }
        switch (DatabaseManager.instance.getType()) {
            case PGSQL, SQLITE -> {
                if (returnAll) {
                    sql.append(" RETURNING *;");
                }
                return DatabaseManager.instance.query(sql.toString(), args);
            }
            case MYSQL -> {
                if (!returnAll) {
                    return DatabaseManager.instance.query(sql.toString(), args);
                } else {
                    try (ResultSet res = DatabaseManager.instance.query(sql.toString(), args)) {
                        return DatabaseManager.instance.query("SELECT * FROM " + tableName + " WHERE " + keyField.name + " = ?;", keyField.value);
                    } catch (Exception e) {
                        throw new QueryException("Mysql returning all failed for table " + tableName);
                    }
                }
            }
            default -> {
                return null;
            }
        }
    }
}
