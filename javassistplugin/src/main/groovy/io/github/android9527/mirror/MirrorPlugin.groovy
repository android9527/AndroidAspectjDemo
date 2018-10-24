package io.github.android9527.mirror

import org.gradle.api.Plugin
import org.gradle.api.Project

public class MirrorPlugin implements Plugin<Project> {
    void apply(Project project) {

        Properties properties = new Properties()
        File localProps = project.rootProject.file("mirror.properties")
        if (localProps.exists()) {

            properties.load(localProps.newDataInputStream())
            String mirrorPackage = properties.getProperty("mirror.package")

            if (mirrorPackage != null) {
                MirrorLogger.setMirrorLogger(project.logger)
                project.android.registerTransform(new MirrorTransform(project, mirrorPackage))
            }
        }
    }

}