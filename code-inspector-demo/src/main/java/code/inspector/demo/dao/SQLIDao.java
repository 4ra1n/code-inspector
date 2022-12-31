package code.inspector.demo.dao;

import code.inspector.demo.model.User;

import java.util.List;

public interface SQLIDao {
    String addUser(String name, int age);

    List<User> selectUser(String name);
}
