package cn.lunadeer.dominion.utils.databse.syntax;

import cn.lunadeer.dominion.utils.databse.DatabaseManager;
import cn.lunadeer.dominion.utils.databse.Field;
import cn.lunadeer.dominion.utils.databse.SqlSyntax;
import cn.lunadeer.dominion.utils.databse.exceptions.DatabaseException;
import cn.lunadeer.dominion.utils.databse.exceptions.FieldNotFound;
import cn.lunadeer.dominion.utils.databse.exceptions.QueryException;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class InsertRow implements SqlSyntax {

    private String tableName;
    private boolean returnAll = false;
    private final List<Field> fields = new ArrayList<>();
    private boolean onConflictDoNothing = true;
    private Field keyField = null;

    public InsertRow field(Field field) {
        fields.add(field);
        return this;
    }

    public InsertRow returningAll() {
        returnAll = true;
        return this;
    }

    public InsertRow onConflictOverwrite(Field keyField) {
        onConflictDoNothing = false;
        this.keyField = keyField;
        return this;
    }

    public InsertRow onConflictDoNothing(Field keyField) {
        onConflictDoNothing = true;
        this.keyField = keyField;
        return this;
    }

    public InsertRow table(String name) {
        tableName = name;
        return this;
    }

    @Override
    public ResultSet execute() throws DatabaseException {
        boolean exist = false;
        if (keyField != null && keyField.value != null) {
            try (ResultSet rs = DatabaseManager.instance.query("SELECT * FROM " + tableName + " WHERE " + keyField.name + " = ?;", keyField.value)) {
                exist = rs.next();
            } catch (Exception e) {
                throw new FieldNotFound(tableName, keyField.name);
            }
        }
        if (exist && !onConflictDoNothing) {
            StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
            Object[] values = new Object[fields.size() + 1];
            for (Field field : fields) {
                sql.append(field.name).append(" = ?, ");
                values[fields.indexOf(field)] = field.value;
            }
            sql.delete(sql.length() - 2, sql.length());
            sql.append(" WHERE ").append(keyField.name).append(" = ").append(keyField.value);
            return DatabaseManager.instance.query(sql.toString(), values);
        } else if (!exist) {
            StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
            for (Field field : fields) {
                sql.append(field.name).append(", ");
            }
            sql.delete(sql.length() - 2, sql.length());
            sql.append(") VALUES (");
            Object[] values = new Object[fields.size()];
            for (Field field : fields) {
                sql.append("?").append(", ");
                values[fields.indexOf(field)] = field.value;
            }
            sql.delete(sql.length() - 2, sql.length());
            sql.append(")");
            switch (DatabaseManager.instance.getType()) {
                case PGSQL, SQLITE -> {
                    if (returnAll) {
                        sql.append(" RETURNING *");
                    }
                    return DatabaseManager.instance.query(sql.toString(), values);
                }
                case MYSQL -> {
                    if (!returnAll) {
                        return DatabaseManager.instance.query(sql.toString(), values);
                    } else {
                        try (ResultSet res = DatabaseManager.instance.query(sql.toString(), values)) {
                            String query = "SELECT * FROM " + tableName + " WHERE " + keyField.name + " = LAST_INSERT_ID()";
                            return DatabaseManager.instance.query(query);
                        } catch (Exception e) {
                            throw new QueryException("Mysql returning all failed for table " + tableName);
                        }
                    }
                }
                default -> {
                    return null;
                }
            }
        } else {
            return null;
        }
    }
}
