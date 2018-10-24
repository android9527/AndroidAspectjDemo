package com.example.proxy;

import com.example.proxy.annotation.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class LogProxy implements InvocationHandler
{
    private Object src;

    private LogProxy(Object src)
    {
        this.src = src;
    }

    /**
     * 提供一个静态的方法返回代理对象
     */
    public static Object factory(Object src)
    {
        Object proxyedObj = //生成被代理类的接口的子类
                Proxy.newProxyInstance(
                        LogProxy.class.getClassLoader(),
                        src.getClass().getInterfaces(),
                        new LogProxy(src));
        return proxyedObj;
    }

    /**
     * 该方法负责集中处理动态代理类上的所有方法调用。第一个参数既是，第二个参数是
     * 调用处理器根据这三个参数进行预处理或分派到委托类实例上发射执行
     *
     * @param proxy  代理类实例
     * @param method 被调用的方法对象
     * @param args   方法调用参数
     * @return Object
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable
    {

        long start = System.currentTimeMillis();
        Thread.sleep(1000);
        Object object = method.invoke(src, args);
        System.out.println(method.getName() + "() 耗时： " + (System.currentTimeMillis() - start));
        return object;

/*        //如果该方法带有Log注解，先执行Logger操作再执行该方法
        if (method.isAnnotationPresent(Log.class))
        {
            long start = System.currentTimeMillis();
            Thread.sleep(1000);
            Object object = method.invoke(src, args);
            System.out.println(method.getName() + "() 耗时： " + (System.currentTimeMillis() - start));

            return object;
        } else
        {
            return method.invoke(src, args);
        }*/
    }

}