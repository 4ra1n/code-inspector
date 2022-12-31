package code.inspector.api;

import code.inspector.model.ResultInfo;

import java.util.List;

public class Test {

    public static void testRCE() {
        CodeInspector inspector = new CodeInspectorImpl();
        List<ResultInfo> results = inspector.analyzeRCE(
                "C:\\Users\\20235\\Documents\\code-inspector.jar",
                false, false, false);
        System.out.println(results.size());
    }

    public static void testSSRF() {
        CodeInspector inspector = new CodeInspectorImpl();
        List<ResultInfo> results = inspector.analyzeSSRF(
                "C:\\Users\\20235\\Documents\\code-inspector.jar",
                false, false, false);
        System.out.println(results.size());
    }

    public static void testDoS() {
        CodeInspector inspector = new CodeInspectorImpl();
        List<ResultInfo> results = inspector.analyzeDoS(
                "C:\\Users\\20235\\Documents\\code-inspector.jar",
                false, false, false);
        System.out.println(results.size());
    }

    public static void testDeserialization() {
        CodeInspector inspector = new CodeInspectorImpl();
        List<ResultInfo> results = inspector.analyzeDeserialization(
                "C:\\Users\\20235\\Documents\\code-inspector.jar",
                false, false, false);
        System.out.println(results.size());
    }

    public static void testSQLInjection() {
        CodeInspector inspector = new CodeInspectorImpl();
        List<ResultInfo> results = inspector.analyzeSQLInjection(
                "C:\\Users\\20235\\Documents\\code-inspector.jar",
                false, false, false);
        System.out.println(results.size());
    }

    public static void testRedirect() {
        CodeInspector inspector = new CodeInspectorImpl();
        List<ResultInfo> results = inspector.analyzeRedirect(
                "C:\\Users\\20235\\Documents\\code-inspector.jar",
                false, false, false);
        System.out.println(results.size());
    }

    public static void testAll() {
        CodeInspector inspector = new CodeInspectorImpl();
        List<ResultInfo> results = inspector.analyzeAll(
                "C:\\Users\\20235\\Documents\\code-inspector.jar",
                false, false, false);
        System.out.println(results.size());
    }

    public static void testTarget(){
        CodeInspector inspector = new CodeInspectorImpl();
        List<ResultInfo> results = inspector.analyzeRCE(
                "D:\\JavaCode\\code-inspector\\bin\\code-inspector-demo-0.1-beta.jar",
                false, false, false);
        for(ResultInfo r:results){
            System.out.println(r.getType()+":"+r.getVulName());
        }
    }

    public static void main(String[] args) {
        testTarget();
    }
}
