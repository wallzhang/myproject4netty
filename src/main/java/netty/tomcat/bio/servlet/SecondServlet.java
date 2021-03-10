package netty.tomcat.bio.servlet;

import netty.tomcat.bio.http.WallRequest;
import netty.tomcat.bio.http.WallResponse;
import netty.tomcat.bio.http.WallServlet;

public class SecondServlet extends WallServlet {
    @Override
    public void doGet(WallRequest request, WallResponse response) throws Exception {
        this.doPost(request, response);
    }

    @Override
    public void doPost(WallRequest request, WallResponse response) throws Exception {
        response.write("this is second servlet from BIO.");
    }
}
