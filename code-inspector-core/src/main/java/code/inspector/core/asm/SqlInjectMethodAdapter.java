package code.inspector.core.asm;

import code.inspector.core.Application;
import code.inspector.core.Const;
import code.inspector.core.Taint;
import code.inspector.core.asm.base.ParamTaintMethodAdapter;
import org.objectweb.asm.MethodVisitor;

import java.util.Map;

public class SqlInjectMethodAdapter extends ParamTaintMethodAdapter {
    private final Map<String, Boolean> pass;

    public SqlInjectMethodAdapter(int methodArgIndex, Map<String, Boolean> pass, int api, MethodVisitor mv,
                                  String owner, int access, String name, String desc) {
        super(methodArgIndex, api, mv, owner, access, name, desc);
        this.pass = pass;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        boolean buildSqlCondition = owner.equals("java/lang/StringBuilder") &&
                name.equals("append") &&
                desc.equals("(Ljava/lang/String;)Ljava/lang/StringBuilder;");

        boolean toStringCondition = owner.equals("java/lang/StringBuilder") &&
                name.equals("toString") &&
                desc.equals("()Ljava/lang/String;");

        boolean jdbcTUCondition = Application.globalOptions.getOrDefault(
                Const.SQL_JDBC_TEMPLATE_UPDATE, false) &&
                owner.equals("org/springframework/jdbc/core/JdbcTemplate") &&
                name.equals("update");

        boolean jdbcTECondition = Application.globalOptions.getOrDefault(
                Const.SQL_JDBC_TEMPLATE_EXECUTE, false) &&
                owner.equals("org/springframework/jdbc/core/JdbcTemplate") &&
                name.equals("execute");

        boolean jdbcTwoParamCondition = Application.globalOptions.getOrDefault(
                Const.SQL_JDBC_TEMPLATE_QUERY_ANY, false) &&
                owner.equals("org/springframework/jdbc/core/JdbcTemplate") &&
                (name.equals("query") || name.equals("queryForStream") || name.equals("queryForList") ||
                        name.equals("queryForMap") || name.equals("queryForObject"));

        boolean stmtEQCondition = Application.globalOptions.getOrDefault(
                Const.SQL_EXECUTE_QUERY, false) &&
                owner.equals("java/sql/Statement") && name.equals("executeQuery");

        boolean stmtEUCondition = Application.globalOptions.getOrDefault(
                Const.SQL_EXECUTE_UPDATE, false) &&
                owner.equals("java/sql/Statement") && name.equals("executeUpdate");

        boolean stmtECondition = Application.globalOptions.getOrDefault(
                Const.SQL_EXECUTE, false) &&
                owner.equals("java/sql/Statement") && name.equals("execute");

        if (buildSqlCondition) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT) ||
                    operandStack.get(1).contains(Taint.PARAM_TAINT) ||
                    operandStack.get(0).contains(Taint.BUILD_STRING) ||
                    operandStack.get(1).contains(Taint.BUILD_STRING)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, Taint.BUILD_STRING);
                return;
            }
        }

        if (toStringCondition) {
            if (operandStack.get(0).contains(Taint.BUILD_STRING)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, Taint.TO_STRING);
                return;
            }
        }

        if (jdbcTUCondition) {
            if (operandStack.get(0).contains(Taint.TO_STRING) ||
                    operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                pass.put(Const.SQL_JDBC_TEMPLATE_UPDATE, true);
                return;
            }
        }

        if (jdbcTECondition) {
            if (operandStack.get(0).contains(Taint.TO_STRING) ||
                    operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                pass.put(Const.SQL_JDBC_TEMPLATE_EXECUTE, true);
                return;
            }
        }

        if (jdbcTwoParamCondition) {
            if (operandStack.get(1).contains(Taint.TO_STRING) ||
                    operandStack.get(1).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                pass.put(Const.SQL_JDBC_TEMPLATE_QUERY_ANY, true);
                return;
            }
        }

        if (stmtEQCondition) {
            if (operandStack.get(0).contains(Taint.TO_STRING) ||
                    operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                pass.put(Const.SQL_EXECUTE_QUERY, true);
                return;
            }
        }

        if (stmtECondition) {
            if (operandStack.get(0).contains(Taint.TO_STRING) ||
                    operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                pass.put(Const.SQL_EXECUTE, true);
                return;
            }
        }

        if (stmtEUCondition) {
            if (operandStack.get(0).contains(Taint.TO_STRING) ||
                    operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                pass.put(Const.SQL_EXECUTE_UPDATE, true);
                return;
            }
        }

        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }
}
