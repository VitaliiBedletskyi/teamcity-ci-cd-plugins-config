package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'MariaDbPrCheckBuild'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("MariaDbPrCheckBuild")) {
    params {
        add {
            param("env.PLUGIN_PATH", "./%system.teamcity.projectName%")
        }
    }
}
