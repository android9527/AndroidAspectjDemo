package io.github.android9527.mirror

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import javassist.ClassPath
import javassist.ClassPool
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

/**
 * Created by lizhaoxuan on 2017/12/20.
 */

class MirrorTransform extends Transform {

    public Project project
    public static ClassPool pool
    public static List<DirectoryInput> directoryInputList = new LinkedList<>()
    public static List<String> jarPathList = new LinkedList<>()
    public static List<ClassPath> classPathList = new LinkedList<>()

    public static boolean isDebug = false

    public static String mirrorPackage

    MirrorTransform(Project project, String mirrorPackage) {
        this.project = project
        MirrorTransform.pool = new ClassPool()
        MirrorTransform.pool.appendSystemPath()
        MirrorTransform.mirrorPackage = mirrorPackage
    }

    @Override
    String getName() {
        return "MirrorTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        MirrorLogger.log("Mirror start")

        isDebug = transformInvocation.context.path.toLowerCase().endsWith("debug")

        Collection<TransformInput> inputs = transformInvocation.getInputs()

        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider()

        inputs.each { TransformInput input ->

            input.jarInputs.each { JarInput jarInput ->
                copyJar(jarInput, outputProvider)
            }

            input.directoryInputs.each { DirectoryInput directoryInput ->
                directoryInputList.add(directoryInput)
            }
        }

        if (isDebug) {
            MirrorLogger.log("Mirror working")

            //project.android.bootClasspath 加入android.jar，否则找不到android相关的所有类
            appendClassPath(project.android.bootClasspath[0].toString())
            MirrorUtils.importBaseClass(pool)

            processJar(project)
        }

        processClassFile(outputProvider, project)

        pool.clearImportedPackages()

        for (ClassPath classPath : classPathList) {
            pool.removeClassPath(classPath)
        }
        classPathList.clear()
        directoryInputList.clear()
        jarPathList.clear()

        MirrorLogger.log("Mirror End")
    }

    public static void appendClassPath(String path) {
        ClassPath classPath = pool.appendClassPath(path)
        if (classPath != null) {
            classPathList.add(classPath)
        }
    }

    private static void copyJar(JarInput jarInput, TransformOutputProvider outputProvider) {
        def jarName = jarInput.getName()
        if (jarName.endsWith(".jar")) {
            jarName = jarName.substring(0, jarName.length() - 4)
        }
        def dest = outputProvider.getContentLocation(jarName, jarInput.contentTypes, jarInput.scopes, Format.JAR)

        FileUtils.copyFile(jarInput.file, dest)
        if (jarInput.file.getName().startsWith("android") || jarInput.file.getName().startsWith("support")) {
            return
        }
        appendClassPath(dest.getAbsolutePath())
        jarPathList.add(dest.getAbsolutePath())
    }

    private static void processJar(Project project) {
        for (String jarPath : jarPathList) {
            MirrorInject.injectJar(jarPath, project)
        }
    }

    private static void processClassFile(TransformOutputProvider outputProvider, Project project) {
        for (DirectoryInput directoryInput : directoryInputList) {

            if (isDebug) {
                MirrorInject.injectDir(directoryInput.file.absolutePath, project)
            }

            def dest = outputProvider.getContentLocation(directoryInput.name,
                    directoryInput.contentTypes, directoryInput.scopes,
                    Format.DIRECTORY)

            FileUtils.copyDirectory(directoryInput.file, dest)
        }
    }

}
