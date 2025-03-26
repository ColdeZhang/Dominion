package cn.lunadeer.dominion.utils.databse.syntax.Table;

import cn.lunadeer.dominion.utils.databse.DatabaseManager;
import cn.lunadeer.dominion.utils.databse.syntax.Syntax;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class Create implements Syntax {

    protected String tableName;
    protected List<Column> columns = new ArrayList<>();

    public static Create create() {
        return switch (DatabaseManager.instance.getType()) {
            case PGSQL, SQLITE -> new pgsql_sqlite_impl();
            case MYSQL -> new mysql_impl();
            default -> throw new UnsupportedOperationException(
                    "Database type: " + DatabaseManager.instance.getType() + " not supported with CREATE"
            );
        };
    }

    private Create() {
    }

    public Create table(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public Create column(Column column) {
        this.columns.add(column);
        return this;
    }

    public void execute() throws SQLException {

    }

    @Override
    public String getSql() {
        StringBuilder sql = new StringBuilder("CREATE TABLE " + tableName + " IF NOT EXISTS (");
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            sql.append(column.getSql());
            if (i < columns.size() - 1) {
                sql.append(", ");
            }
        }
        sql.append(");");
        return sql.toString();
    }

    private static class pgsql_sqlite_impl extends Create {
        @Override
        public String getSql() {
            return super.getSql();
        }
    }

    private static class mysql_impl extends Create {
        @Override
        public String getSql() {
            return super.getSql() + " ENGINE=InnoDB;";
        }
    }
}
