import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.DslContext
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.Template
import jetbrains.buildServer.configs.kotlin.buildSteps.nodeJS
import jetbrains.buildServer.configs.kotlin.projectFeatures.githubConnection
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

object HackoladePlugins : Project({
    template(PluginBuildTemplate)

    features {
        githubConnection {
            id = "PROJECT_EXT_2"
            displayName = "GitHub.com"
            clientId = "cb10c5eb655b55614a2e"
            clientSecret = "credentialsJSON:6d2e09f0-6472-411a-97ff-2bb6f4cf06f1"
        }
    }

    subProject(Plugin)
})

object Plugin : Project({
    name = DslContext.projectName

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
    templates(PluginBuildTemplate)
    name = "Build"
})