package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.triggers.VcsTrigger
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'MariaDbPrCheckBuild'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("MariaDbPrCheckBuild")) {
    triggers {
        val trigger1 = find<VcsTrigger> {
            vcs {
                triggerRules = "+:root=MariaDBPluginGithubRepository:**"

                branchFilter = "+:*"
            }
        }
        trigger1.apply {
            quietPeriodMode = VcsTrigger.QuietPeriodMode.USE_DEFAULT
            branchFilter = """
                -:refs/heads/*
                -:refs/heads/main
            """.trimIndent()

        }
    }
}
