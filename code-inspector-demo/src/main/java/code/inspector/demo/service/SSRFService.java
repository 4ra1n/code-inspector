package code.inspector.demo.service;

public interface SSRFService {
    String ssrf1(String data);
    String ssrf2(String data);
    String ssrf3(String host, int port);
    String ssrf4(String url);
}
