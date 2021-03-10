package netty.rpc.registy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.rpc.protocol.InvokerProtocol;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RegistryHandler extends ChannelInboundHandlerAdapter {

    /**
     * 注册中心容器
      */
    private static ConcurrentHashMap<String, Object> registyMap = new ConcurrentHashMap<String, Object>();
    private static List<String> classNames = new ArrayList<String>();

    public RegistryHandler(){

        // 扫描所有需要注册的类
        // netty.rpc.provider
        scannerClass("netty.rpc.provider");
        // 将扫描到的容器放在一个容器内
        doRegister();
    }

    private void doRegister() {
        if(classNames.size() == 0){
            return;
        }
        for (String className : classNames){
            try {
                Class<?> clazz = Class.forName(className);
                Class<?> i = clazz.getInterfaces()[0];
                registyMap.put(i.getName(),clazz.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void scannerClass(String packageName) {
        URL url = this.getClass().getClassLoader().getResource(packageName.replace("\\","/"));
        File dir = new File(url.getPath());

        for(File file : dir.listFiles()){

            // 如果是文件夹递归
            if(file.isDirectory()){
                scannerClass(packageName + "." + file.getName());
            }else {
                classNames.add(packageName + "." + file.getName().replace(".class","").trim());
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Object result = new Object();
        InvokerProtocol request = (InvokerProtocol) msg;

        if(registyMap.containsKey(request.getClassName())){
            // 用反射直接调用provider的方法
            Object provider = registyMap.get(request.getClassName());
            Method method = provider.getClass().getMethod(request.getMethodName(),request.getParams());
            result = method.invoke(provider,request.getValues());
        }
        ctx.write(result);
        ctx.flush();
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
