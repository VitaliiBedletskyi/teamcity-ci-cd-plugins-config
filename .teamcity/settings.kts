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

    cleanup {
        baseRule {
            preventDependencyCleanup = false
        }
    }

    subProject(HackoladePlugins)
}

object HackoladePlugins : Project({
    name = "Hackolade Plugins"
    description = "Contains all plugins projects"

    template(PluginBuildTemplate)

    vcsRoot(PluginRepo)

    buildType(PluginBuild)
})

object PluginRepo : GitVcsRoot({
    name = DslContext.getParameter("repoName")
    url = DslContext.getParameter("repoHttpUrl")
    branch = DslContext.getParameter("mainBranch")
    branchSpec = "refs/heads/*"
})

object PluginBuildTemplate : Template({
    name = "Build"

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

object PluginBuild : BuildType({
    name = "Build"
    templates(PluginBuildTemplate)
})
