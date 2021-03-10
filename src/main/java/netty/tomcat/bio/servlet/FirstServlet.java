package netty.tomcat.bio.servlet;

import netty.tomcat.bio.http.WallRequest;
import netty.tomcat.bio.http.WallResponse;
import netty.tomcat.bio.http.WallServlet;

public class FirstServlet extends WallServlet {
    @Override
    public void doGet(WallRequest request, WallResponse response) throws Exception {
        this.doPost(request, response);
    }

    @Override
    public void doPost(WallRequest request, WallResponse response) throws Exception {
        response.write("this is first servlet from BIO.");
    }
}
