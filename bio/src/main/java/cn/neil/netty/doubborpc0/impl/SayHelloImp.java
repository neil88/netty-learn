package cn.neil.netty.doubborpc0.impl;

import cn.neil.netty.doubborpc0.interfaces.SayHello;

/**
 * @author neil
 * @date 2020-02-17
 **/

public class SayHelloImp implements SayHello {
    @Override
    public String sayHello(String str) {
        return "say hello: " + str;
    }
}
