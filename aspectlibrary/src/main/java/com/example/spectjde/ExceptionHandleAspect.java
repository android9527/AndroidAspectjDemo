package com.example.spectjde;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 异常处理
 */
@Aspect
public class ExceptionHandleAspect
{
    private static final String TAG = "ExceptionHandleAspect";

    /**
     * 截获空指针异常
     *
     * @param e
     */
    @Pointcut("handler(java.lang.NullPointerException)&&args(e)")
    public void handle(NullPointerException e)
    {
    }

    /**
     * 在catch代码执行之前做一些处理
     *
     * @param joinPoint
     * @param e         异常参数
     */
    @Before(value = "handle(e)", argNames = "e")
    public void handleBefore(JoinPoint joinPoint, NullPointerException e)
    {
        Log.e(TAG, joinPoint.getSignature().toLongString() + " handleBefore() :" + e.toString());//汇总处理
    }
}