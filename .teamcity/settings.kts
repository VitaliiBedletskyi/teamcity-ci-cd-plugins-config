import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.nodeJS
import jetbrains.buildServer.configs.kotlin.projectFeatures.buildReportTab
import jetbrains.buildServer.configs.kotlin.projectFeatures.githubConnection
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2022.10"

project {
    description = "Contains all other projects"

    features {
        buildReportTab {
            id = "PROJECT_EXT_1"
            title = "Code Coverage"
            startPage = "coverage.zip!index.html"
        }
        githubConnection {
            id = "PROJECT_EXT_4"
            displayName = "GitHub.com"
            clientId = "cb10c5eb655b55614a2e"
            clientSecret = "credentialsJSON:130e3fd5-c108-4f2e-a1dc-1eb90230ade4"
        }
    }

    subProject(HackoladePlugins_Project)
    subProject(Hackolade)
}


object Hackolade : Project({
    name = "Hackolade"
})


object HackoladePlugins_Project : Project({
    name = "Hackolade Plugins"

    template(PluginsBuildTemplate)

    subProject(MariaDB)
})

object PluginsBuildTemplate : Template({
    name = "Plugins Build Template"

    steps {
        nodeJS {
            name = "Install dependencies"
            id = "RUNNER_1"
            shellScript = "npm ci"
            dockerImage = "node:l16"
        }
        nodeJS {
            name = "Run Linter"
            id = "RUNNER_2"
            shellScript = "npm run lint"
            dockerImage = "node:16"
        }
        nodeJS {
            name = "Package plugin"
            id = "RUNNER_3"
            shellScript = "npm run package"
            dockerImage = "node:16"
        }
    }
})


object MariaDB : Project({
    name = "MariaDB"

    vcsRoot(MariaDB_MariaDBVsc)

    buildType(MaiaDBBuild)
})

object MaiaDBBuild : BuildType({
    templates(PluginsBuildTemplate)
    name = "Build"

    vcs {
        root(MariaDB_MariaDBVsc)
    }
})

object MariaDB_MariaDBVsc : GitVcsRoot({
    name = "MariaDB_Vsc"
    url = "https://github.com/VitaliiBedletskyi/MariaDB"
    branch = "release"
    authMethod = password {
        userName = "VitaliiBedletskyi"
        password = "credentialsJSON:94c03b5b-2b09-4b69-afbf-6ddc4c5a61d7"
    }
    param("oauthProviderId", "PROJECT_EXT_4")
    param("tokenType", "undefined")
})
