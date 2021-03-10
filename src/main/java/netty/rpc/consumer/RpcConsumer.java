package netty.rpc.consumer;

import netty.rpc.api.IRpcHelloService;
import netty.rpc.provider.RpcHelloServiceImpl;

public class RpcConsumer {
    public static void main(String[] args) {
        IRpcHelloService rpcHello = new RpcHelloServiceImpl();

        System.out.println(rpcHello.hello("wall"));
    }
}
