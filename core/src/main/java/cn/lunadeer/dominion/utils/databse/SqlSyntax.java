package cn.lunadeer.dominion.utils.databse;

import cn.lunadeer.dominion.utils.databse.exceptions.QueryException;

import java.sql.ResultSet;

public interface SqlSyntax {
    ResultSet execute() throws QueryException;
}
