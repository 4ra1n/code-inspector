## 新规则

如何自定义并添加新的规则

（1）新的`ClassVisitor`应该继承自`BaseClassVisitor`

除非你明白自己在做什么，否则绝大多数情况代码应该如下

```java
public class NewClassVisitor extends BaseClassVisitor {
    public NewClassVisitor(MethodReference.Handle targetMethod, int targetIndex) {
        super(targetMethod, targetIndex, DesMethodAdapter.class);
    }
}
```

（2）新的`MethodAdapter`应该继承自`ParamTaintMethodAdapter`

除非你明白自己在做什么，否则绝大多数情况代码应该如下

在其中编写你需要`Hook`的`JVM`指令，并可以直接通过`operandStack`和`localVariables`拿到栈帧数据进行分析

```java
public class NewMethodAdapter extends ParamTaintMethodAdapter {
    private final Map<String, Boolean> pass;
    public NewMethodAdapter(int methodArgIndex, Map<String, Boolean> pass, int api, MethodVisitor mv,
                            String owner, int access, String name, String desc) {
        super(methodArgIndex, api, mv, owner, access, name, desc);
        this.pass = pass;
    }
    // 添加你需要Hook的指令以及规则
}
```

（3）注意在`Const`中添加你的新规则

一般新分类应该有`NEW_MODULE`变量来表示是否开启整个模块，其次有一些具体的分类变量，例如以下`RCE`相关

```java
// 表示模块是否开启
String RCE_MODULE = "RCE_MODULE";
// 多个分类
String RCE_RUNTIME_TYPE = "RCE_RUNTIME_TYPE";
String RCE_PROCESS_TYPE = "RCE_PROCESS_TYPE";
String RCE_GROOVY_TYPE = "RCE_GROOVY_TYPE";
String RCE_JNDI_TYPE = "RCE_JNDI_TYPE";
String RCE_SP_EL_TYPE = "RCE_SP_EL_TYPE";
```

这个`Const`变量用于保存具体漏洞和收集漏洞信息

```java
// 例如这里的XMLDecoder反序列化漏洞
if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
    super.visitMethodInsn(opcode, owner, name, desc, itf);
    pass.put(Const.DESERIALIZATION_XML_DECODER, true);
    return;
}
```

例如在`DesCollector`中确认对应的反序列化漏洞信息

```java
// 例如这里的XMLDecoder反序列化漏洞
if (cv.getPass(Const.DESERIALIZATION_XML_DECODER)) {
    ResultInfo resultInfo = new ResultInfo();
    resultInfo.setType(DES);
    resultInfo.setVulName("XMLDecoder readObject");
    resultInfo.getChains().addAll(tempChain);
    results.add(resultInfo);
    Log.info(resultInfo.toString());
}
```

（4）新建`collector`类用于收集扫描结果数据

注意`collect`方法和参数不变，底层使用反射调用

```java
public class NewCollector {
    private static final String NEW = "Your New Type";

    public static void collect(BaseClassVisitor cv, List<String> tempChain, List<ResultInfo> results) {
        if (cv.getPass(Const.YOUR_NEW_VULN)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(NEW);
            resultInfo.setVulName("Your New Vulnerability");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
    }
}
```

(5) 新建`Service`类继承自`BaseService`用于启动分析

`BaseService`类是一切分析的核心，除非你真的明白自己在做什么，否则绝大多数情况代码应该如下

```java
public class NewService extends BaseService {
    public static void start(Map<String, ClassFile> classFileByName,
                             List<SpringController> controllers,
                             Map<MethodReference.Handle, Set<CallGraph>> discoveredCalls) {
        start0(classFileByName,controllers,discoveredCalls,
                NewClassVisitor.class, NewCollector.class);
    }
}
```

(6) 在`Application`中添加新的`Service`即可成功配置

```java
if (globalOptions.getOrDefault(Const.NEW_MODULE, false)) {
    NewService.start(classFileByName, controllers, graphCallMap);
    resultInfoList.addAll(NewService.getResults());
}
```

通过以上的配置，可以使用`API`来扫描，通过`GUI`启动扫描任务的还需要对界面进行编辑
