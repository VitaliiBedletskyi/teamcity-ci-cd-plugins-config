import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.nodeJS
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
    subProject(Test)
}

object Build : Template({
    name = "Build"

    vcs {
        root(DslContext.projectId)
    }

    steps {
        nodeJS {
            name = "Instal dependecies"
            id = "RUNNER_4"
            shellScript = "npm ci"
            dockerImage = "node:16"
        }
        nodeJS {
            name = "Run Linter"
            id = "RUNNER_5"
            shellScript = "npm run lint"
            dockerImage = "node:16"
        }
        nodeJS {
            name = "Package plugin"
            id = "RUNNER_6"
            shellScript = "npm run package"
            dockerImage = "node:16"
        }
    }
})


object MariaDB : Project({
    name = "MariaDB"

    vcsRoot(MariaDB_Vcs)

    buildType(MariaDB_Build)
})

object MariaDB_Build : BuildType({
    templates(Build)
    name = "Build"
})

object MariaDB_Vcs : GitVcsRoot({
    name = "https://github.com/VitaliiBedletskyi/MariaDB.git#refs/heads/release"
    url = "https://github.com/VitaliiBedletskyi/MariaDB.git"
    branch = "refs/heads/release"
    branchSpec = "refs/heads/*"
})

object Test : Project({
    name = "Test"

    vcsRoot(Test_Vcs)

    buildType(Test_Build)
})

object Test_Build : BuildType({
    templates(Build)
    name = "Build"
})

object Test_Vcs : GitVcsRoot({
    name = "https://github.com/VitaliiBedletskyi/MariaDB.git#refs/heads/release"
    url = "https://github.com/VitaliiBedletskyi/MariaDB.git"
    branch = "refs/heads/release"
    branchSpec = "refs/heads/*"
})
