package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, create a buildType with id = 'MariaDb_PullRequestChecks'
in the project with id = 'MariaDb', and delete the patch script.
*/
create(RelativeId("MariaDb"), BuildType({
    id("MariaDb_PullRequestChecks")
    name = "Pull request checks"

    vcs {
        root(RelativeId("HackoladeRepository"))
        root(RelativeId("MariaDBPluginGithubRepository"), "+:. => ./MariaDB")
    }
}))

