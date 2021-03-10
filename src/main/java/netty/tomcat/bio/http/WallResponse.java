package netty.tomcat.bio.http;

import java.io.OutputStream;

public class WallResponse {

    private OutputStream out;

    public WallResponse(OutputStream out) {
        this.out = out;
    }

    public void write(String s) throws Exception{
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 200 ok\n")
                .append("Content-Type: text/html;\n")
                .append("\r\n")
                .append(s);
        out.write(sb.toString().getBytes());
    }
}
