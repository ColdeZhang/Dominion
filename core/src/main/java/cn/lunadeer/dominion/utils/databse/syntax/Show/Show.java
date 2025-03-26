package cn.lunadeer.dominion.utils.databse.syntax.Show;

import cn.lunadeer.dominion.utils.databse.syntax.Syntax;

public class Show implements Syntax {

    public static Show show() {
        return new Show();
    }

    protected Show() {
    }

    public Tables tables() {
        return Tables.showTables();
    }

    public Columns columns() {
        return Columns.showColumns();
    }

    @Override
    public String getSql() {
        return "";
    }
}
