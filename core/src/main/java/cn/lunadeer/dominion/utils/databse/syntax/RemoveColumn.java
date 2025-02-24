package cn.lunadeer.dominion.utils.databse.syntax;

import cn.lunadeer.dominion.utils.databse.DatabaseManager;
import cn.lunadeer.dominion.utils.databse.SqlSyntax;
import cn.lunadeer.dominion.utils.databse.exceptions.QueryException;

import java.sql.ResultSet;

import static cn.lunadeer.dominion.utils.databse.Common.assertFieldExist;

public class RemoveColumn implements SqlSyntax {

    private boolean ifExists = false;

    private String tableName;

    private final String fieldName;

    public RemoveColumn(String fieldName) {
        this.fieldName = fieldName;
    }

    public RemoveColumn IfExists() {
        ifExists = true;
        return this;
    }

    public RemoveColumn table(String name) {
        tableName = name;
        return this;
    }

    @Override
    public ResultSet execute() throws QueryException {
        if (ifExists) {
            try {
                assertFieldExist(tableName, fieldName);
            } catch (QueryException e) {
                return null;
            }
        }
        String sql = "ALTER TABLE " + tableName + " DROP COLUMN " +
                fieldName;
        return DatabaseManager.instance.query(sql);
    }
}
