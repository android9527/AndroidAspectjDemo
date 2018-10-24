package io.github.android9527.mirror

import javassist.CtClass
import javassist.CtConstructor
import javassist.CtMethod
import javassist.Modifier

/**
 * Created by michaelzhong on 2018/1/26.
 */

public class MirrorHelper {

    public static void injectStatistics(CtMethod ctMethod) {
        if (Modifier.isPrivate(ctMethod.getModifiers())) {
            return;
        }
        ctMethod.addLocalVariable("mirrorStart", CtClass.longType)
        ctMethod.insertBefore("mirrorStart = android.os.SystemClock.elapsedRealtime();")

        String afterMethod = "if (android.os.SystemClock.elapsedRealtime() - mirrorStart >= 2) {\nandroid.util.Log.i(\"Mirror\", Thread.currentThread().getName() + \":" + ctMethod.getLongName() + ":\" + (android.os.SystemClock.elapsedRealtime() - mirrorStart));\n}"
        ctMethod.insertAfter(afterMethod)
    }

    public static void injectStatistics(CtConstructor ctConstructor) {
        if (Modifier.isPrivate(ctConstructor.getModifiers())) {
            return;
        }
        ctConstructor.addLocalVariable("mirrorStart", CtClass.longType)
        ctConstructor.insertBefore("mirrorStart = android.os.SystemClock.elapsedRealtime();")

        String afterMethod = "if (android.os.SystemClock.elapsedRealtime() - mirrorStart >= 2) {\nandroid.util.Log.i(\"Mirror\", Thread.currentThread().getName() + \":" + ctConstructor.getLongName() + ":\" + (android.os.SystemClock.elapsedRealtime() - mirrorStart));\n}"
        ctConstructor.insertAfter(afterMethod, true)
    }

}
