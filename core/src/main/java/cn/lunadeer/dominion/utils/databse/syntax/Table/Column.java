package cn.lunadeer.dominion.utils.databse.syntax.Table;

import cn.lunadeer.dominion.utils.databse.DatabaseManager;
import cn.lunadeer.dominion.utils.databse.FIelds.Field;
import cn.lunadeer.dominion.utils.databse.syntax.Syntax;

public abstract class Column implements Syntax {

    protected Field<?> column;
    protected boolean primary = false;
    protected boolean serial = false;
    protected boolean notNull = false;
    protected boolean unique = false;
    protected Field<?>[] joinUnique = null;
    protected String foreignTableName = null;
    protected Field<?> foreignColumn = null;
    protected String defaultValue = null;

    public static Column of(Field<?> column) {
        return switch (DatabaseManager.instance.getType()) {
            case PGSQL -> new pgsql_impl(column);
            case SQLITE -> new sqlite_impl(column);
            case MYSQL -> new mysql_impl(column);
            default -> throw new UnsupportedOperationException(
                    "Database type: " + DatabaseManager.instance.getType() + " not supported with COLUMN"
            );
        };
    }

    private Column(Field<?> field) {
        this.column = field;
    }

    public Field<?> getColumn() {
        return column;
    }

    public Column primary() {
        this.primary = true;
        return this;
    }

    public Column serial() {
        this.serial = true;
        return this;
    }

    public Column foreign(String tableName, Field<?> column) {
        this.foreignTableName = tableName;
        this.foreignColumn = column;
        return this;
    }

    public Column notNull() {
        this.notNull = true;
        return this;
    }

    public Column unique() {
        this.unique = true;
        return this;
    }

    public Column unique(Field<?>... fields) {
        this.joinUnique = fields;
        return this;
    }

    public Column defaultSqlVal(String value) {
        this.defaultValue = value;
        return this;
    }

    // Implementation of jdbc methods

    private static class sqlite_impl extends Column {
        public sqlite_impl(Field<?> field) {
            super(field);
        }

        @Override
        public String getSql() {
            StringBuilder sql = new StringBuilder();
            sql.append(column.getName()).append(" ").append(column.getSqlTypeStr());
            if (primary) {
                sql.append(" PRIMARY KEY");
            }
            if (serial) {
                sql.append(" AUTOINCREMENT");
            }
            if (notNull) {
                sql.append(" NOT NULL");
            }
            if (unique) {
                sql.append(" UNIQUE");
            }
            if (defaultValue != null) {
                sql.append(" DEFAULT ").append(defaultValue);
            }
            if (joinUnique != null) {
                sql.append(" UNIQUE (");
                for (Field<?> field : joinUnique) {
                    sql.append(field.getName()).append(", ");
                }
                sql.delete(sql.length() - 2, sql.length());
                sql.append(")");
            }
            if (foreignTableName != null && foreignColumn != null) {
                sql.append(" REFERENCES ").append(foreignTableName).append("(").append(foreignColumn.getName()).append(") ON DELETE CASCADE");
            }
            return sql.toString();
        }
    }

    private static class mysql_impl extends Column {
        public mysql_impl(Field<?> field) {
            super(field);
        }

        @Override
        public String getSql() {
            StringBuilder sql = new StringBuilder();
            sql.append(column.getName()).append(" ").append(column.getSqlTypeStr());
            if (primary) {
                sql.append(" PRIMARY KEY");
            }
            if (serial) {
                sql.append(" AUTO_INCREMENT");
            }
            if (notNull) {
                sql.append(" NOT NULL");
            }
            if (unique) {
                sql.append(" UNIQUE");
            }
            if (defaultValue != null) {
                sql.append(" DEFAULT ").append(defaultValue);
            }
            if (joinUnique != null) {
                sql.append(" UNIQUE (");
                for (Field<?> field : joinUnique) {
                    sql.append(field.getName()).append(", ");
                }
                sql.delete(sql.length() - 2, sql.length());
                sql.append(")");
            }
            if (foreignTableName != null && foreignColumn != null) {
                sql.append(" REFERENCES ").append(foreignTableName).append("(").append(foreignColumn.getName()).append(") ON DELETE CASCADE");
            }
            return sql.toString();
        }
    }

    private static class pgsql_impl extends Column {
        public pgsql_impl(Field<?> column) {
            super(column);
        }

        @Override
        public String getSql() {
            StringBuilder sql = new StringBuilder();
            sql.append(column.getName()).append(" ").append(column.getSqlTypeStr());
            if (primary) {
                sql.append(" PRIMARY KEY");
            }
            if (serial) {
                sql.append(" SERIAL");
            }
            if (notNull) {
                sql.append(" NOT NULL");
            }
            if (unique) {
                sql.append(" UNIQUE");
            }
            if (defaultValue != null) {
                sql.append(" DEFAULT ").append(defaultValue);
            }
            if (joinUnique != null) {
                sql.append(" UNIQUE (");
                for (Field<?> field : joinUnique) {
                    sql.append(field.getName()).append(", ");
                }
                sql.delete(sql.length() - 2, sql.length());
                sql.append(")");
            }
            if (foreignTableName != null && foreignColumn != null) {
                sql.append(" REFERENCES ").append(foreignTableName).append("(").append(foreignColumn.getName()).append(") ON DELETE CASCADE");
            }
            return sql.toString();
        }
    }
}
