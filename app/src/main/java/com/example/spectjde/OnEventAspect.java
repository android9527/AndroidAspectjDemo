package com.example.spectjde;

import android.util.Log;

import com.example.spectjde.annotation.OnEvent;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


@Aspect
public class OnEventAspect {

    private static final String TAG = OnEventAspect.class.getSimpleName();

    @Around("execution(!synthetic * *(..)) && onAsyncMethod()")
    public void doAsyncMethod(final ProceedingJoinPoint joinPoint) throws Throwable {
        asyncMethod(joinPoint);
    }

    @Pointcut("@within(com.example.spectjde.annotation.OnEvent)||@annotation(com.example.spectjde.annotation.OnEvent)")
    public void onAsyncMethod() {
    }

    private void asyncMethod(final ProceedingJoinPoint joinPoint) throws Throwable {
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
