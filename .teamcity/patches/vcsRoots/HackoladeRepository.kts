package patches.vcsRoots

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.ui.*
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the vcsRoot with id = 'HackoladeRepository'
accordingly, and delete the patch script.
*/
changeVcsRoot(RelativeId("HackoladeRepository")) {
    val expected = GitVcsRoot({
        id("HackoladeRepository")
        name = "Hackolade Repo"
        url = "git@bitbucket.org:hackolade/binary-studio.git"
        branch = "feature/HCK-2208-improving-plugin-release-flow"
        authMethod = uploadedKey {
            uploadedKey = "Hackolade Repo"
        }
    })

    check(this == expected) {
        "Unexpected VCS root settings"
    }

    (this as GitVcsRoot).apply {
        branch = "feature/HCK-2208-plugin-publisher"
        branchSpec = "+:refs/heads/feature/HCK-2208*"
    }

}
