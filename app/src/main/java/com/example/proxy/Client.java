package com.example.proxy;

import com.example.proxy.service.UserService;
import com.example.proxy.service.UserServiceImpl;

public class Client
{

    public static void main(String[] args) {
        // 设置这个值，可以把生成的代理类，输出出来。
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");

        //特点： 最先执行最后返回的代理对象的代理方法。如下：先执行Log最后执行业务方法。
        UserService service = new UserServiceImpl();

        service = (UserService)LogProxy.factory(service);
        service.addUser("abel");
        service.remove("name");
    }
}