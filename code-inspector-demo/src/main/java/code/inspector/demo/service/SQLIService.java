package code.inspector.demo.service;

import code.inspector.demo.model.User;

import java.util.List;

public interface SQLIService {
    String addUser(String name,int age);
    List<User> selectUser(String name);
}
