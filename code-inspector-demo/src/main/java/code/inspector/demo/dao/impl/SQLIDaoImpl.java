package code.inspector.demo.dao.impl;

import code.inspector.demo.dao.SQLIDao;
import code.inspector.demo.model.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

@Repository
public class SQLIDaoImpl implements SQLIDao {
    private final JdbcTemplate jdbcTemplate;

    public SQLIDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String addUser(String name, int age) {
        try {
            Connection conn = DriverManager.getConnection("", "", "");
            Statement stmt = conn.createStatement();
            stmt.executeQuery("select * from t_user where name=\"" + name + "\"");
            stmt.execute("select * from t_user where name=\"" + name + "\"");
            stmt.executeUpdate("select * from t_user where name=\"" + name + "\"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    @Override
    public List<User> selectUser(String name) {
        List<User> users = jdbcTemplate.query("select * from t_user where name=\"" + name + "\"",
                new BeanPropertyRowMapper(User.class));
        return users;
    }
}
