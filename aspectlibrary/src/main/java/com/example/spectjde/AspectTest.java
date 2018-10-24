package com.example.spectjde;

import android.util.Log;
import android.view.View;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class AspectTest {

    private static final String TAG = "AspectTestAspectTest";

//    @Before("execution(* android.app.Activity.on**(..))")
//    public void onActivityMethodBefore(JoinPoint joinPoint) throws Throwable {
//        String key = joinPoint.getSignature().toString();
//        Log.e(TAG, "onActivityMethodBefore: " + key);
//    }
//
//    @After("execution(* android.app.Activity.on**(..))")
//    public void onActivityMethodAfter(JoinPoint joinPoint) throws Throwable {
//        String key = joinPoint.getSignature().toString();
//        Log.e(TAG, "onActivityMethodAfter: " + key);
//    }
    @Around("execution(* android.app.Activity.on**(..))")
    public void onActivityMethodAfter(ProceedingJoinPoint joinPoint) throws Throwable {
        String key = joinPoint.getSignature().toString();
        Log.e(TAG, "onActivityMethodBefore: " + key);
        joinPoint.proceed();
        Log.e(TAG, "onActivityMethodAfter: " + key);
    }

    private View mLastView;
    private boolean canDoubleClick = false;

    @Before("@annotation(com.example.spectjde.annotation.DoubleClick)")
    public void beforeEnableDoubleClick(JoinPoint joinPoint) throws Throwable {
        canDoubleClick = true;
    }

    @Around("execution(* android.view.View.OnClickListener.onClick(..))  && target(Object) && this(Object)")
    public void onDoubleClickListener(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] objects = joinPoint.getArgs();
        View view = objects.length == 0 ? null : (View) objects[0];
        if (view != mLastView || canDoubleClick || !NoDoubleClickUtils.isDoubleClick()) {
            joinPoint.proceed();
            canDoubleClick = false;
        }
        mLastView = view;
    }

}