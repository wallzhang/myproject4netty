package netty.rpc.protocol;


import lombok.Data;

import java.io.Serializable;

/**
* @Description: 协议类
* @Author: zhang.zw
* @Date: 2021/3/10
*/

@Data
public class InvokerProtocol implements Serializable {

    /**
     * 类名
     */
    private String className;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 形参列表
     */
    private Class<?>[] params;

    /**
     * 实参列表
     */
    private Object[] values;
}
