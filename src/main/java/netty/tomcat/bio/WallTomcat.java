package netty.tomcat.bio;

import netty.tomcat.bio.http.WallRequest;
import netty.tomcat.bio.http.WallResponse;
import netty.tomcat.bio.http.WallServlet;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class WallTomcat {

    private int port = 8080;

    private ServerSocket server;

    private Properties webxml = new Properties();

    private Map<String, WallServlet> servletMapping = new HashMap<String, WallServlet>();

    public static void main(String[] args) {
        new WallTomcat().start();
    }

    private void start() {
        // 1.加载web.properties文件，解析配置
        init();

        // 2. 启动服务socket，等待用户请求
        try {
            server = new ServerSocket(this.port);
            System.out.println("Wall Tomcat 已启动，监听端口:" + this.port);

            while (true){
                Socket client = server.accept();

                // 3.获取请求信息，解析Http协议内容
                process(client);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void process(Socket client) throws Exception {

        InputStream is = client.getInputStream();
        OutputStream out = client.getOutputStream();

        WallRequest request = new WallRequest(is);
        WallResponse response = new WallResponse(out);

        String url = request.getUrl();
        if(servletMapping.containsKey(url)){
            servletMapping.get(url).service(request, response);
        }else {
            System.out.println("404 - Not Found!!");
            response.write("404 - Not Found!!");
        }
        out.flush();
        out.close();

        is.close();
        client.close();
    }

    private void init() {
        try {
            String WEB_INF = this.getClass().getResource("/").getPath();
            FileInputStream fis = new FileInputStream(WEB_INF + "web-bio.properties");

            webxml.load(fis);

            for (Object k: webxml.keySet()) {
                String key = k.toString();
                // 将 servlet.xxx.url 的.url替换，只剩下servlet.xxx 当成 servletName
                if(key.contains(".url")){
                    String servletName = key.replaceAll("\\.url$","");
                    String url = webxml.getProperty(key);

                    // 拿到servlet的全类名
                    String className = webxml.getProperty(servletName + ".className");

                    // 反射创建实例
                    WallServlet obj = (WallServlet) Class.forName(className).newInstance();
                    // 将URL和Servlet建立映射关系
                    servletMapping.put(url, obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
