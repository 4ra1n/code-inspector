package code.inspector.demo.web;

import code.inspector.demo.service.SSRFService;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Controller
@RequestMapping("/ssrf")
public class SSRFController {
    private final SSRFService ssrfService;

    public SSRFController(SSRFService ssrfService) {
        this.ssrfService = ssrfService;
    }

    @RequestMapping(path = "/test1")
    @ResponseBody
    public String ssrf1(@RequestParam(name = "url") String data) {
        return ssrfService.ssrf1(data);
    }

    @RequestMapping(path = "/test2")
    @ResponseBody
    public String ssrf2(@RequestParam(name = "url") String data) {
        return ssrfService.ssrf2(data);
    }

    @RequestMapping(path = "/test3")
    @ResponseBody
    public String ssrf3(@RequestParam(name = "host") String host,
                        @RequestParam(name = "port") int port) {
        return ssrfService.ssrf3(host, port);
    }

    @RequestMapping(path = "/test4")
    @ResponseBody
    public String ssrf4(@RequestParam(name = "url") String data) {
        return ssrfService.ssrf4(data);
    }

    @RequestMapping(value = "/one")
    public String One(@RequestParam(value = "url") String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            return connection.getResponseMessage();
        } catch (IOException var3) {
            System.out.println(var3);
            return "Hello";
        }
    }

    @RequestMapping(value = "/four")
    public String Four(@RequestParam(value = "url") String imageUrl) {
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(imageUrl);
            HttpResponse response = client.execute(get);
            return response.toString();
        } catch (IOException var1) {
            System.out.println(var1);
            return "Hello";
        }
    }

    @RequestMapping(value = "five")
    public String Five(@RequestParam(value = "url") String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            InputStream inputStream = url.openStream();
            return String.valueOf(inputStream.read());
        } catch (IOException var1) {
            System.out.println(var1);
            return "Hello";
        }
    }
}
