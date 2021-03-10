package netty.tomcat.nio;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.EventExecutorGroup;
import netty.tomcat.nio.http.WallRequest;
import netty.tomcat.nio.http.WallResponse;
import netty.tomcat.nio.http.WallServlet;

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
        // Boss 线程
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // Worker 线程
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        // 2.创建netty服务端对象
        ServerBootstrap server = new ServerBootstrap();

        try {

            // 3.配置服务端参数
            server.group(bossGroup,workerGroup)
                    // 配置主线程的处理逻辑
                    .channel(NioServerSocketChannel.class)
                    // 子线程的回调处理，Handler
                    .childHandler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel client) throws Exception {
                            // 处理回调的逻辑

                            // 链式编程，责任链模式
                            // 处理响应结果的封装
                            client.pipeline().addLast(new HttpResponseEncoder());
                            // 用户请求过来，要解码
                            client.pipeline().addLast(new HttpRequestDecoder());
                            // 用户自己的业务逻辑
                            client.pipeline().addLast(new WallTomcatHandler());
                        }
                    })
                    // 配置主线程分配的最大线程数
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);

            // 启动服务
            ChannelFuture f = server.bind(this.port).sync();

            System.out.println("nio tomcat已启动，监听端口是：" + this.port);
            f.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }



    }

    private void init() {
        try {
            String WEB_INF = this.getClass().getResource("/").getPath();
            FileInputStream fis = new FileInputStream(WEB_INF + "web-nio.properties");

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

    public class WallTomcatHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if(msg instanceof HttpRequest){
                HttpRequest req = (HttpRequest) msg;

                WallRequest request = new WallRequest(ctx,req);
                WallResponse response = new WallResponse(ctx,req);

                String url = request.getUrl();

                if(servletMapping.containsKey(url)){
                    servletMapping.get(url).service(request,response);
                }else {
                    response.write("404 - not found!!");
                }
            }
        }
    }
}
