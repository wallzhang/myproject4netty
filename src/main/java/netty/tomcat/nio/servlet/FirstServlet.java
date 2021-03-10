package netty.tomcat.nio.servlet;

import netty.tomcat.nio.http.WallRequest;
import netty.tomcat.nio.http.WallResponse;
import netty.tomcat.nio.http.WallServlet;

public class FirstServlet extends WallServlet {
    @Override
    public void doGet(WallRequest request, WallResponse response) throws Exception {
        this.doPost(request, response);
    }

    @Override
    public void doPost(WallRequest request, WallResponse response) throws Exception {
        response.write("this is first servlet from NIO.");
    }
}
