package netty.tomcat.bio.http;

import java.io.IOException;
import java.io.InputStream;

public class WallRequest {

    private String method;

    private String url;

    public WallRequest(InputStream is) {
        try {
            // 拿到HTTP协议的具体内容
            String content = "";
            byte[] buff = new byte[1024];
            int len = 0;
            if((len = is.read(buff)) != -1){
                content = new String(buff, 0, len);
            }
            String line = content.split("\\n")[0];
            String[] arr = line.split("\\s");

            this.method = arr[0];
            this.url = arr[1].split("\\?")[0];

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUrl(){
        return url;
    }

    public String getMethod(){
        return method;
    }
}
