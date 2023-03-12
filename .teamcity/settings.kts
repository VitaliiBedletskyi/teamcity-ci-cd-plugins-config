import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.nodeJS
import jetbrains.buildServer.configs.kotlin.projectFeatures.buildReportTab
import jetbrains.buildServer.configs.kotlin.projectFeatures.githubConnection
import jetbrains.buildServer.configs.kotlin.triggers.vcs
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

    subProject(Hackolade)
}


object Hackolade : Project({
    name = "Hackolade"
})


object HackoladePlugins : Project({
    name = "Hackolade Plugins"

    subProject(HackoladePlugins_MariaDB)
})


object HackoladePlugins_MariaDB : Project({
    name = "MariaDB"

    vcsRoot(HackoladePlugins_MariaDB_HttpsGithubComVitaliiBedletskyiMariaDBRefsHeadsRelease)

    buildType(HackoladePlugins_MariaDB_Build)
})

object HackoladePlugins_MariaDB_Build : BuildType({
    name = "Build"

    vcs {
        root(HackoladePlugins_MariaDB_HttpsGithubComVitaliiBedletskyiMariaDBRefsHeadsRelease)
    }

    steps {
        nodeJS {
            name = "Instal dependecies"
            shellScript = "npm ci"
        }
        nodeJS {
            name = "Run Linter"
            shellScript = "npm run lint"
        }
        nodeJS {
            name = "Package plugin"
            shellScript = "npm run package"
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }
})

object HackoladePlugins_MariaDB_HttpsGithubComVitaliiBedletskyiMariaDBRefsHeadsRelease : GitVcsRoot({
    name = "https://github.com/VitaliiBedletskyi/MariaDB#refs/heads/release"
    url = "https://github.com/VitaliiBedletskyi/MariaDB"
    branch = "refs/heads/release"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "VitaliiBedletskyi"
        password = "credentialsJSON:94c03b5b-2b09-4b69-afbf-6ddc4c5a61d7"
    }
    param("oauthProviderId", "PROJECT_EXT_4")
})
