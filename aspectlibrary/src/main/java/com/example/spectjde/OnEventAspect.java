package com.example.spectjde;

import android.util.Log;

import com.example.spectjde.annotation.OnEvent;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;


@Aspect
public class OnEventAspect {

    private static final String TAG = OnEventAspect.class.getSimpleName();

    @Around("execution(!synthetic * *(..)) && onEventMethod()")
    public void doAsyncMethod(final ProceedingJoinPoint joinPoint) throws Throwable {
        eventMethod(joinPoint);
    }

    @Pointcut("@within(com.example.spectjde.annotation.OnEvent)||@annotation(com.example.spectjde.annotation.OnEvent)")
    public void onEventMethod() {
    }

    public void eventMethod(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        OnEvent event = method.getAnnotation(OnEvent.class);

        if (event != null) {
            String value = event.value();

            Log.e(TAG, "收到事件" + value);
        }
        joinPoint.proceed();
    }
}
