package cn.lunadeer.dominion.utils.databse.syntax;

import cn.lunadeer.dominion.utils.databse.DatabaseManager;
import cn.lunadeer.dominion.utils.databse.DatabaseType;
import cn.lunadeer.dominion.utils.databse.SqlSyntax;
import cn.lunadeer.dominion.utils.databse.TableColumn;
import cn.lunadeer.dominion.utils.databse.exceptions.QueryException;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateTable implements SqlSyntax {

    public static class ForeignKey {
        public ForeignKey(TableColumn tableColumn, String referenceTable, TableColumn referenceTableColumn, boolean cascade) {
            this.tableColumn = tableColumn;
            this.referenceTable = referenceTable;
            this.referenceTableColumn = referenceTableColumn;
            this.cascade = cascade;
        }

        public TableColumn tableColumn;
        public String referenceTable;
        public TableColumn referenceTableColumn;
        public boolean cascade;
    }

    private final List<TableColumn> tableColumns = new ArrayList<>();

    private final List<List<TableColumn>> uniqueFields = new ArrayList<>();

    private final List<ForeignKey> foreignKeys = new ArrayList<>();

    private String tableName;

    private Boolean ifNotExists = false;

    public CreateTable table(String name) {
        tableName = name;
        return this;
    }

    /**
     * 添加多个字段关联作为唯一约束
     *
     * @param tableColumns 字段
     * @return this
     */
    public CreateTable unique(TableColumn... tableColumns) {
        List<TableColumn> unique = new ArrayList<>(Arrays.asList(tableColumns));
        uniqueFields.add(unique);
        return this;
    }

    /**
     * 添加一个外键约束
     *
     * @param key 外键
     * @return this
     */
    public CreateTable foreignKey(ForeignKey key) {
        foreignKeys.add(key);
        return this;
    }

    public CreateTable ifNotExists() {
        ifNotExists = true;
        return this;
    }

    public CreateTable field(TableColumn tableColumn) {
        tableColumns.add(tableColumn);
        return this;
    }

    @Override
    public ResultSet execute() throws QueryException {
        DatabaseType type = DatabaseManager.instance.getType();
        StringBuilder sql = new StringBuilder("CREATE TABLE ");
        if (ifNotExists) {
            sql.append("IF NOT EXISTS ");
        }
        switch (type) {
            case PGSQL -> {
                sql.append(tableName).append(" (");
                for (TableColumn tableColumn : tableColumns) {

                    // Handle auto-increment
                    if (tableColumn.isAutoIncrement) {
                        sql.append(tableColumn.name).append(" ");
                        sql.append(" SERIAL");
                    } else {
                        sql.append(tableColumn.name).append(" ").append(tableColumn.type.getType(type));
                        // Handle other attributes for non-auto-increment columns
                        if (tableColumn.isPrimary) {
                            sql.append(" PRIMARY KEY");
                        }
                        if (tableColumn.isNotNull) {
                            sql.append(" NOT NULL");
                        }
                        if (tableColumn.defaultValue != null) {
                            sql.append(" DEFAULT ").append(tableColumn.defaultValue);
                        }
                    }
                    if (tableColumn.isUnique) {
                        sql.append(" UNIQUE");
                    }
                    sql.append(", ");
                }
                for (List<TableColumn> unique : uniqueFields) {
                    sql.append("UNIQUE (");
                    for (TableColumn tableColumn : unique) {
                        sql.append(tableColumn.name).append(", ");
                    }
                    sql.delete(sql.length() - 2, sql.length());
                    sql.append("), ");
                }
                for (ForeignKey key : foreignKeys) {
                    sql.append("FOREIGN KEY (").append(key.tableColumn.name).append(") REFERENCES ").append(key.referenceTable).append("(").append(key.referenceTableColumn.name).append(")");
                    if (key.cascade) {
                        sql.append(" ON DELETE CASCADE");
                    }
                    sql.append(", ");
                }
                sql.delete(sql.length() - 2, sql.length());
                sql.append(");");
                return DatabaseManager.instance.query(sql.toString());
            }
            case MYSQL -> {
                sql.append(tableName).append(" (");
                for (TableColumn tableColumn : tableColumns) {
                    sql.append(tableColumn.name).append(" ").append(tableColumn.type.getType(type));
                    if (tableColumn.isPrimary) {
                        sql.append(" PRIMARY KEY");
                    }
                    if (tableColumn.isAutoIncrement) {
                        sql.append(" AUTO_INCREMENT");
                    }
                    if (tableColumn.isNotNull) {
                        sql.append(" NOT NULL");
                    }
                    if (tableColumn.isUnique) {
                        sql.append(" UNIQUE");
                    }
                    if (!tableColumn.isAutoIncrement && tableColumn.defaultValue != null) {
                        sql.append(" DEFAULT ").append(tableColumn.defaultValue);
                    }
                    sql.append(", ");
                }
                for (List<TableColumn> unique : uniqueFields) {
                    sql.append("UNIQUE (");
                    for (TableColumn tableColumn : unique) {
                        sql.append(tableColumn.name).append(", ");
                    }
                    sql.delete(sql.length() - 2, sql.length());
                    sql.append("), ");
                }
                for (ForeignKey key : foreignKeys) {
                    sql.append("FOREIGN KEY (").append(key.tableColumn.name).append(") REFERENCES ").append(key.referenceTable).append("(").append(key.referenceTableColumn.name).append(")");
                    if (key.cascade) {
                        sql.append(" ON DELETE CASCADE");
                    }
                    sql.append(", ");
                }
                sql.delete(sql.length() - 2, sql.length());
                sql.append(") ENGINE=InnoDB;");
                return DatabaseManager.instance.query(sql.toString());
            }
            case SQLITE -> {
                sql.append(tableName).append(" (");
                for (TableColumn tableColumn : tableColumns) {
                    sql.append(tableColumn.name).append(" ").append(tableColumn.type.getType(type));
                    if (tableColumn.isPrimary) {
                        sql.append(" PRIMARY KEY");
                        if (tableColumn.isAutoIncrement) {
                            sql.append(" AUTOINCREMENT");
                        }
                    }
                    if (tableColumn.isNotNull) {
                        sql.append(" NOT NULL");
                    }
                    if (tableColumn.isUnique) {
                        sql.append(" UNIQUE");
                    }
                    if (!tableColumn.isAutoIncrement && tableColumn.defaultValue != null) {
                        sql.append(" DEFAULT ").append(tableColumn.defaultValue);
                    }
                    sql.append(", ");
                }
                for (List<TableColumn> unique : uniqueFields) {
                    sql.append("UNIQUE (");
                    for (TableColumn tableColumn : unique) {
                        sql.append(tableColumn.name).append(", ");
                    }
                    sql.delete(sql.length() - 2, sql.length());
                    sql.append("), ");
                }
                for (ForeignKey key : foreignKeys) {
                    sql.append("FOREIGN KEY (").append(key.tableColumn.name).append(") REFERENCES ").append(key.referenceTable).append("(").append(key.referenceTableColumn.name).append(")");
                    if (key.cascade) {
                        sql.append(" ON DELETE CASCADE");
                    }
                    sql.append(", ");
                }
                sql.delete(sql.length() - 2, sql.length());
                sql.append(");");
                return DatabaseManager.instance.query(sql.toString());
            }
            default -> throw new IllegalArgumentException("Unsupported database type: " + type);
        }
    }
}
