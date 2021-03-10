package netty.tomcat.bio.http;

public abstract class WallServlet {

    public void service(WallRequest request, WallResponse response) throws Exception{
        if("GET".equalsIgnoreCase(request.getMethod())){
            doGet(request, response);
        }else {
            doPost(request, response);
        }
    }

    public abstract void doGet(WallRequest request, WallResponse response) throws Exception;

    public abstract void doPost(WallRequest request, WallResponse response) throws Exception;
}
