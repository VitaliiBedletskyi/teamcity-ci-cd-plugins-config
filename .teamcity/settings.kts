import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.nodeJS
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

    template(Build)

    features {
        githubConnection {
            id = "PROJECT_EXT_2"
            displayName = "GitHub.com"
            clientId = "cb10c5eb655b55614a2e"
            clientSecret = "credentialsJSON:6d2e09f0-6472-411a-97ff-2bb6f4cf06f1"
        }
    }

    subProject(MariaDB)
}

object Build : Template({
    name = "Build"

    steps {
        nodeJS {
            name = "Instal dependecies"
            id = "RUNNER_4"
            shellScript = "npm ci"
            dockerImage = "node:16"
        }
    }
})


object MariaDB : Project({
    name = "MariaDB"

    vcsRoot(MariaDB_HttpsGithubComVitaliiBedletskyiMariaDBGitRefsHeadsRelease)

    buildType(MariaDB_Build)
})

object MariaDB_Build : BuildType({
    name = "Build"

    vcs {
        root(MariaDB_HttpsGithubComVitaliiBedletskyiMariaDBGitRefsHeadsRelease)
    }

    steps {
        nodeJS {
            name = "Instal dependecies"
            shellScript = "npm ci"
            dockerImage = "node:16"
        }
        nodeJS {
            name = "Run Linter"
            shellScript = "npm run lint"
            dockerImage = "node:16"
        }
        nodeJS {
            name = "Package plugin"
            shellScript = "npm run package"
            dockerImage = "node:16"
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

object MariaDB_HttpsGithubComVitaliiBedletskyiMariaDBGitRefsHeadsRelease : GitVcsRoot({
    name = "https://github.com/VitaliiBedletskyi/MariaDB.git#refs/heads/release"
    url = "https://github.com/VitaliiBedletskyi/MariaDB.git"
    branch = "refs/heads/release"
    branchSpec = "refs/heads/*"
})
