package code.inspector.core.data;

import code.inspector.core.Const;
import code.inspector.core.asm.base.BaseClassVisitor;
import code.inspector.log.Log;
import code.inspector.model.ResultInfo;

import java.util.List;

public class SqlCollector {
    private static final String SQL = "SQL Injection";
    public static void collect(BaseClassVisitor cv, List<String> tempChain, List<ResultInfo> results) {
        if (cv.getPass(Const.SQL_EXECUTE)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(SQL);
            resultInfo.setVulName("Statement execute");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
        if (cv.getPass(Const.SQL_EXECUTE_UPDATE)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(SQL);
            resultInfo.setVulName("Statement executeUpdate");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
        if (cv.getPass(Const.SQL_EXECUTE_QUERY)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(SQL);
            resultInfo.setVulName("Statement executeQuery");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
        if (cv.getPass(Const.SQL_JDBC_TEMPLATE_EXECUTE)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(SQL);
            resultInfo.setVulName("JdbcTemplate execute");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
        if (cv.getPass(Const.SQL_JDBC_TEMPLATE_UPDATE)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(SQL);
            resultInfo.setVulName("JdbcTemplate update");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
        if (cv.getPass(Const.SQL_JDBC_TEMPLATE_QUERY_ANY)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(SQL);
            resultInfo.setVulName("JdbcTemplate queryAnyMethod");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
    }
}
