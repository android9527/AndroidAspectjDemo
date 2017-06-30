package com.example.spectjde;

import android.util.Log;
import android.view.View;

import com.example.androidaspectjdemo.NoDoubleClickUtils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class AspectTest {

    private static final String TAG = "AspectTest";
    private boolean canDoubleClick = false;

    @Before("execution(* android.app.Activity.on**(..))")
    public void onActivityMethodBefore(JoinPoint joinPoint) throws Throwable {
        String key = joinPoint.getSignature().toString();
        Log.d(TAG, "onActivityMethodBefore: " + key);
    }

//    @Around("execution(* android.view.View.OnClickListener.onClick(..))")
//    public void onClickListener(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//        Log.e(TAG, "OnClick");
//        if (!NoDoubleClickUtils.isDoubleClick()) {
//            proceedingJoinPoint.proceed();
//        }
//    }

    @Before("@annotation(com.example.spectjde.annotation.DoubleClick)")
    public void beforeEnableDoubleClick(JoinPoint joinPoint) throws Throwable {
        canDoubleClick = true;
    }

    private View mLastView;

    @Around("execution(* android.view.View.OnClickListener.onClick(..))  && target(Object) && this(Object)")
    public void OnClickListener(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] objects = joinPoint.getArgs();
        View view = objects.length == 0 ? null : (View) objects[0];
//        Log.e(TAG, "OnClick:" + view);
        if (view != mLastView || canDoubleClick || !NoDoubleClickUtils.isDoubleClick()) {
            joinPoint.proceed();
            canDoubleClick = false;
        }
        mLastView = view;
    }

}