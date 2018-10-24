package io.github.android9527.mirror

import javassist.CtConstructor
import javassist.CtMethod
import javassist.expr.Expr
import javassist.expr.ExprEditor
import javassist.expr.MethodCall

/**
 * Created by michaelzhong on 2018/1/26.
 */

public class MirrorAnrDetect {

    public static void detectFileOperate(CtMethod ctMethod) {

        ctMethod.instrument(new ExprEditor() {

            private String logDetect(Expr expr, String longName) {
                MirrorLogger.log("Mirror-->" + longName + "(" + expr.getFileName() + ":" + expr.getLineNumber() + ")")
                return "if(android.os.Looper.myLooper()==android.os.Looper.getMainLooper()){\nandroid.util.Log.e(\"MirrorDetect\", \"May cause ANR in " + longName + "(" + expr.getFileName() + ":" + expr.getLineNumber() + ")\");\n}"
            }

            public void edit(MethodCall arg) {
                if (arg.getClassName().startsWith("java.io.")
                        && (arg.getMethodName().equals("read") || arg.getMethodName().equals("write"))) {
                    String longName = arg.where().getLongName()
                    arg.replace(logDetect(arg, longName.substring(0, longName.indexOf("("))) + " \$_ = \$proceed(\$\$);")
                }
            }

        })
    }

    public static void detectFileOperate(CtConstructor ctConstructor) {

        ctConstructor.instrument(new ExprEditor() {

            private String logDetect(Expr expr, String longName) {
                return "if(android.os.Looper.myLooper()==android.os.Looper.getMainLooper()){\nandroid.util.Log.e(\"MirrorDetect\", \"May cause ANR in " + longName + "(" + expr.getFileName() + ":" + expr.getLineNumber() + ")\");\n}"
            }

            public void edit(MethodCall arg) {
                if (arg.getClassName().startsWith("java.io.")
                        && (arg.getMethodName().equals("read") || arg.getMethodName().equals("write"))) {
                    String longName = arg.where().getLongName()
                    arg.replace(logDetect(arg, longName.substring(0, longName.indexOf("("))) + " \$_ = \$proceed(\$\$);")
                }
            }

        })
    }

}
