package com.example.spectjde;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AspectJSpectControler
{
    private static final String TAG = AspectJSpectControler.class.getSimpleName();

    @Around(value = "execution(* com.example..*.*(..))")
    public Object weavePatchLogic(ProceedingJoinPoint joinPoint) throws Throwable
    {
        long startT = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long consume = System.currentTimeMillis() - startT;
        Log.e(TAG, "AspectJSpectControler: " + consume + " ms " + joinPoint.getSignature());
        return proceed;
    }
}