## 如何构建方法调用链

通过`Controller`传递的所有参数都认为是污点（以此为开头进行分析）

例如以下`rce1`方法的`data`参数，首先会查找该方法中所有和`data`有关的方法调用，认为是下一步调用链

注意：我会记录所有调用方法的污点参数位置，并且在下一个链的方法中设置对应位置参数是污点以继续分析

```java
@RequestMapping("/rce1")
public String rce1(String data) {
    return rceService.rce1(data);
}
```

实际上调用的`Service`是一个接口

我已经做了处理，可以通过接口找到它的实现，并且认为每一个实现都是一个新的方法调用链

```java
public interface RCEService {
    String rce1(String data);
}
```

由于我记录了污点`data`的参数位置，在`ServiceImpl`类的`rce1`方法中设置对应的参数是污点

注意：并不一定同名，这里命名`data`只是恰好

```java
@Service
public class RCEServiceImpl implements RCEService {
    @Override
    public String rce1(String data) {
        try {
            // 此时污点data被当成参数传入了exec方法
            // 认为这里是漏洞
            Runtime.getRuntime().exec(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ok";
    }
}
```

通过扫描可以发现这样一条漏洞链：

```text
[info] [18:40:43] Runtime exec RCE
	code/inspector/demo/web/RCEController.rce1
	code/inspector/demo/service/RCEService.rce1
	code/inspector/demo/service/impl/RCEServiceImpl.rce1
```