package patches.projects

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the project with id = 'MariaDb'
accordingly, and delete the patch script.
*/
changeProject(RelativeId("MariaDb")) {
    params {
        add {
            param("env.GIT_COMMIT_HASH", "%build.vcs.number%")
        }
        add {
            param("env.PLUGIN_PATH", "./%system.teamcity.projectName%")
        }
    }
}
