package netty.tomcat.nio.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.io.OutputStream;

public class WallResponse {

    private ChannelHandlerContext ctx;
    private HttpRequest req;

    public WallResponse(ChannelHandlerContext ctx, HttpRequest req) {
        this.ctx = ctx;
        this.req = req;
    }

    public void write(String s) throws Exception{

        if(null == s || 0 == s.length()){
            return;
        }
        try {
            FullHttpResponse response = new DefaultFullHttpResponse(
                    // 设置HTTP版本为1.1
                    HttpVersion.HTTP_1_1,
                    // 设置返回HTTP状态码为200
                    HttpResponseStatus.OK,
                    // 统一输出格式为UTF-8
                    Unpooled.wrappedBuffer(s.getBytes("UTF-8")));

            response.headers().set("Content-Type","text/html");
            ctx.write(response);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            ctx.flush();
            ctx.close();
        }

    }
}
