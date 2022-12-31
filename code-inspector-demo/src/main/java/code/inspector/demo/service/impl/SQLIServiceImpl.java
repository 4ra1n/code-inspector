package code.inspector.demo.service.impl;

import code.inspector.demo.dao.SQLIDao;
import code.inspector.demo.model.User;
import code.inspector.demo.service.SQLIService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SQLIServiceImpl implements SQLIService {

    private final SQLIDao sqliDao;

    public SQLIServiceImpl(SQLIDao sqliDao) {
        this.sqliDao = sqliDao;
    }

    @Override
    public String addUser(String name, int age) {
        return sqliDao.addUser(name, age);
    }

    @Override
    public List<User> selectUser(String name) {
        return sqliDao.selectUser(name);
    }
}
