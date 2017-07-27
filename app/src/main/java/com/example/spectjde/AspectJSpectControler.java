package com.example.spectjde;

import android.util.Log;

import com.example.androidaspectjdemo.BuildConfig;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AspectJSpectControler {
    private static final String TAG = AspectJSpectControler.class.getSimpleName();

    @Around(value = "execution(* com.example..*.*(..))")
    public Object weavePatchLogic(ProceedingJoinPoint joinPoint) throws Throwable {
        if (BuildConfig.DEBUG) { //debug    状态下计算方法耗时
            long startT = System.currentTimeMillis();
            Object proceed = joinPoint.proceed();
            long consume = System.currentTimeMillis() - startT;
            Log.d(TAG, "AspectJSpectControler: " + consume + " ms " + joinPoint.getSignature());
            return proceed;
        }
        return joinPoint.proceed();
    }
}