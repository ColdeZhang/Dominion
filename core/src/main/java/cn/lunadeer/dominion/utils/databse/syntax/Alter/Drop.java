package cn.lunadeer.dominion.utils.databse.syntax.Alter;

import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.databse.DatabaseManager;
import cn.lunadeer.dominion.utils.databse.FIelds.Field;
import cn.lunadeer.dominion.utils.databse.syntax.Show.Show;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Drop extends Alter {

    private Field<?> column;

    public Drop column(Field<?> column) {
        this.column = column;
        return this;
    }

    @Override
    public String getSql() {
        return "ALTER TABLE " + tableName + " DROP COLUMN " + column.getName();
    }

    public void execute() throws SQLException {
        try (Connection conn = DatabaseManager.instance.getConnection()) {
            if (!Show.show().columns().from(tableName).execute().containsKey(column.getName())) {
                return;
            }
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(getSql());
        } catch (SQLException e) {
            XLogger.error("SQL: " + getSql());
            XLogger.error(e);
            throw new SQLException("Error executing delete statement: " + e.getMessage(), e);
        }
    }
}
