package cn.neil.netty.doubborpc0.metadata;

import lombok.Data;

import java.io.Serializable;

/**
 * @author neil
 * @date 2020-02-17
 **/
@Data
public class Metadata implements Serializable {
    private static final long serialVersionUID = 4380922744156976276L;
    //private static final long serialVersionUID = 42L;

    private Class clazz;

    private String methodName;

    private Class[] parasClass;

    private Object[] paras;

    private Object result;

}
