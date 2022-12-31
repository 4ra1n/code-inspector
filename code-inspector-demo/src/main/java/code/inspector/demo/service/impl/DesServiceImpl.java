package code.inspector.demo.service.impl;

import code.inspector.demo.model.Obj;
import code.inspector.demo.model.User;
import code.inspector.demo.service.DesService;
import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.beans.XMLDecoder;
import java.io.*;

@Service
public class DesServiceImpl implements DesService {
    @Override
    public String jdk(Obj obj) {
        try {
            InputStream is = new ByteArrayInputStream(obj.getData().getBytes());
            ObjectInputStream ois = new ObjectInputStream(is);
            Object o = ois.readObject();
            return o.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "ok";
    }

    @Override
    public String fastjson(Obj obj) {
        JSON.parse(obj.getData());
        JSON.parseArray(obj.getData());
        JSON.parseObject(obj.getData());
        return "ok";
    }

    @Override
    public String yaml(Obj obj) {
        Yaml yaml = new Yaml();
        yaml.load(obj.getData());
        return "ok";
    }

    @Override
    public String jackson(Obj obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enableDefaultTyping();
            mapper.readValue(obj.getData(), User.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "ok";
    }

    @Override
    public String hessian(Obj obj) {
        try {
            InputStream is = new ByteArrayInputStream(obj.getData().getBytes());
            Hessian2Input in = new Hessian2Input(is);
            Object o = in.readObject();
            return o.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "ok";
    }

    @Override
    public String xml(Obj obj) {
        File file = new File(obj.getData());
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BufferedInputStream bis = new BufferedInputStream(fis);
        XMLDecoder xmlDecoder = new XMLDecoder(bis);
        xmlDecoder.readObject();
        xmlDecoder.close();
        return "ok";
    }
}
