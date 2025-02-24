package cn.lunadeer.dominion.utils.databse.syntax;


import cn.lunadeer.dominion.utils.databse.DatabaseManager;
import cn.lunadeer.dominion.utils.databse.SqlSyntax;
import cn.lunadeer.dominion.utils.databse.TableColumn;
import cn.lunadeer.dominion.utils.databse.exceptions.FieldNotFound;
import cn.lunadeer.dominion.utils.databse.exceptions.QueryException;

import java.sql.ResultSet;

import static cn.lunadeer.dominion.utils.databse.Common.assertFieldExist;

public class AddColumn implements SqlSyntax {

    private final TableColumn newColumn;

    private boolean ifNotExists = false;

    private String tableName;

    public AddColumn(TableColumn newColumn) {
        this.newColumn = newColumn;
    }

    public AddColumn ifNotExists() {
        ifNotExists = true;
        return this;
    }

    public AddColumn table(String name) {
        tableName = name;
        return this;
    }

    @Override
    public ResultSet execute() throws QueryException {
        if (ifNotExists) {
            boolean exist = true;
            try {
                assertFieldExist(tableName, newColumn.name);
            } catch (FieldNotFound ignored) {
                exist = false;
            }
            if (exist) {
                return null;
            }
        }
        StringBuilder sql = new StringBuilder("ALTER TABLE ");
        sql.append(tableName).append(" ADD COLUMN ");
        sql.append(newColumn.name).append(" ").append(newColumn.type.getType(DatabaseManager.instance.getType()));
        if (newColumn.isNotNull) {
            sql.append(" NOT NULL");
        }
        if (newColumn.isUnique) {
            sql.append(" UNIQUE");
        }
        if (newColumn.defaultValue != null) {
            sql.append(" DEFAULT ").append(newColumn.defaultValue);
        }
        return DatabaseManager.instance.query(sql.toString());
    }
}
