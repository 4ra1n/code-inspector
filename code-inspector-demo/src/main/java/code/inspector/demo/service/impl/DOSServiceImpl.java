package code.inspector.demo.service.impl;

import code.inspector.demo.service.DOSService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class DOSServiceImpl implements DOSService {
    @Override
    public String dos1(String a, String b) {
        try {
            Pattern.matches(a, b);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @Override
    public String dos2(int a) {
        try {
            byte[] test = new byte[a];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @Override
    public String dos3(int a) {
        try {
            List<Object> list = new ArrayList<>(a);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @Override
    public String dos4(int a) {
        try {
            for (int i = 0; i < a; i++) {
                System.out.println(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ok";
    }

}
