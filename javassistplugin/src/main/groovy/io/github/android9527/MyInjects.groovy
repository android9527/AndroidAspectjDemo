package io.github.android9527

import com.java.javassist.AopAgentTransformer
import javassist.CannotCompileException
import javassist.ClassPool
import javassist.CtClass
import javassist.CtField
import javassist.CtMethod
import javassist.NotFoundException
import javassist.expr.ExprEditor
import javassist.expr.MethodCall
import org.gradle.api.Project
import javassist.CtNewMethod;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.println

public class MyInjects {
    //初始化类池
    private final
    static ClassPool pool = ClassPool.getDefault();

/**
 *
 * @param path
 * @param project
 */
    public
    static void inject(String path, Project project) {
        //将当前路径加入类池,不然找不到这个类
        pool.appendClassPath(path);
        //project.android.bootClasspath 加入android.jar，不然找不到android相关的所有类
        pool.appendClassPath(project.android.bootClasspath[0].toString());
        //引入android.os.Bundle包，因为onCreate方法参数有Bundle
        pool.importPackage("android.os.Bundle");

        File dir = new File(path);
        if (dir.isDirectory()) {
            //遍历文件夹
            dir.eachFileRecurse { File file ->
                String filePath = file.absolutePath
                println("filePath = " + filePath)
                if (file.getName().equals("MainActivity.class")) {

                    //获取MainActivity.class
                    CtClass ctClass = pool.getCtClass("io.github.android9527.javassistdemo.MainActivity");
                    println("ctClass = " + ctClass)
                    //解冻
                    if (ctClass.isFrozen())
                        ctClass.defrost()

                    //获取到OnCreate方法
                    String oldName = "onCreate"
                    CtMethod ctMethod = ctClass.getDeclaredMethod(oldName)

                    println("方法名 = " + ctMethod)

                    String insetBeforeStr = """ android.widget.Toast.makeText(this, "插入了Toast代码~", android.widget.Toast.LENGTH_SHORT).show();
                                                """
                    //在方法开头插入代码
                    ctMethod.insertBefore(insetBeforeStr)

//                    //创建属性
//                    CtField e1 = CtField.make("long start;", ctClass)
//                    CtField e2 = CtField.make("long end;", ctClass);
//                    ctClass.addField(e1);
//                    ctClass.addField(e2);

//                    String after = "start = System.currentTimeMillis() - start;"
//                    ctMethod.insertBefore(after)
//
//                    String newName = oldName + "tmp";
//                    /** 重命名老方法 */
//                    ctMethod.setName(newName);
//                    /** 新建一个 方法 名字 跟老方法一样 */
//                    CtMethod newMethod = CtNewMethod.copy(ctMethod, oldName, ctClass, null);
//                    String type = ctMethod.getReturnType().getName();
//                    StringBuilder body = new StringBuilder();
//                    /** 新方法增加计时器 */
//                    body.append("{\n long start = System.currentTimeMillis();\n");
//                    /** 判断老方法返回类型 */
//                    if(!"void".equals(type)){
//                        /** 类似 Object result = newName(args0,args1); */
//                        body.append(type + " result = ");
//                    }
//                    /** 执行老方法 */
//                    body.append( newName + "(\$\$);");
//                    /** 统计执行完成时间 */
//                    body.append("System.out.println(\"Call to method " + oldName +
//                            " took \" +\n (System.currentTimeMillis()-start) + " +
//                            "\" ms.\");\n");
//                    if(!"void".equals(type)){
//                        /** 类似 Object result = newName(args0,args1); */
//                        body.append("return result;\n");
//                    }
//                    body.append("}");
//                    newMethod.setBody(body.toString());
//                    ctClass.addMethod(newMethod);

                    ctClass.writeFile(path)
                    ctClass.detach()//释放
                }
            }
        }
    }

}