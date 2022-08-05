package com.github.atternatt.powercommit.services

import com.intellij.openapi.project.Project
import com.github.atternatt.powercommit.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
