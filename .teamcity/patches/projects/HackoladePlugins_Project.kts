package patches.projects

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the project with id = 'HackoladePlugins_Project'
accordingly, and delete the patch script.
*/
changeProject(RelativeId("HackoladePlugins_Project")) {
    params {
        add {
            param("env.BUILD_BRANCH", "%teamcity.build.vcs.branch.MariaDBPluginGithubRepository%")
        }
    }
}
