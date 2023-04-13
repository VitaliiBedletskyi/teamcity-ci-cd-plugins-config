package patches.vcsRoots

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.ui.*
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the vcsRoot with id = 'MariaDBPluginGithubRepository'
accordingly, and delete the patch script.
*/
changeVcsRoot(RelativeId("MariaDBPluginGithubRepository")) {
    val expected = GitVcsRoot({
        id("MariaDBPluginGithubRepository")
        name = "MariaDB_Vsc"
        url = "https://github.com/VitaliiBedletskyi/MariaDB"
        branch = "main"
        authMethod = password {
            userName = "VitaliiBedletskyi"
            password = "credentialsJSON:94c03b5b-2b09-4b69-afbf-6ddc4c5a61d7"
        }
        param("oauthProviderId", "PROJECT_EXT_4")
        param("tokenType", "undefined")
    })

    check(this == expected) {
        "Unexpected VCS root settings"
    }

    (this as GitVcsRoot).apply {
        branchSpec = "+:refs/heads/main"
    }

}