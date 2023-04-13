import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.buildSteps.ScriptBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.projectFeatures.buildReportTab
import jetbrains.buildServer.configs.kotlin.projectFeatures.githubConnection
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.ui.add
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

    vcsRoot(MariaDBPluginGithubRepository)
    vcsRoot(HackoladeRepository)

    params {
        param("env.AZURE_STORAGE_ACCOUNT_NAME", "testpluginstorage")
        password("env.GITHUB_ACCESS_TOKEN", "credentialsJSON:4c079522-f111-400c-8eb1-0fe4620bd5bc", display = ParameterDisplay.HIDDEN, readOnly = true)
        password("env.AZURE_STORAGE_ACCOUNT_SERVICE_PRINCIPAL_SECRET", "credentialsJSON:2646bf3b-d244-4150-a8f3-5ea7cb24e404", display = ParameterDisplay.HIDDEN, readOnly = true)
        param("env.AZURE_STORAGE_ACCOUNT_SERVICE_PRINCIPAL_APP_ID", "6346b2fb-a67c-488c-a68d-cbd60aec1c19")
        param("env.AZURE_STORAGE_ACCOUNT_SERVICE_PRINCIPAL_TENANT_ID", "680b5bc4-6ffc-4ebb-beb5-16043b6e6893")
        param("env.AZURE_STORAGE_CONTAINER_NAME", "plugins")
        text("buildx_builder_instance_name", "hck-forge", label = "Docker buildx builder instance", readOnly = true, allowEmpty = false)
        param("docker_builder_image", "bigorn0/builder:18.04")
    }

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

    subProject(MariaDb)
})

object MariaDb : Project({
    name = "MariaDB"

    params {
        param("env.PLUGIN_PATH", "./%system.teamcity.projectName%")
        param("env.PLUGIN_NAME", "%system.teamcity.projectName%")
    }

    buildType(MariaDbPrCheckBuild)
})

object MariaDBPluginGithubRepository : GitVcsRoot({
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

object HackoladeRepository : GitVcsRoot({
    name = "Hackolade Repo"
    url = "git@bitbucket.org:hackolade/binary-studio.git"
    branch = "feature/HCK-2208-improving-plugin-release-flow"
    authMethod = uploadedKey {
        uploadedKey = "Hackolade Repo"
    }
})

object MariaDbPrCheckBuild : BuildType({
    name = "Pull request checks"

    artifactRules = "+:./release/%system.teamcity.projectName%-* => %system.teamcity.projectName%.zip"

    params {
        param("env.GIT_COMMIT_HASH", "%build.vcs.number.MariaDBPluginGithubRepository%")
        param("env.TEAMCITY_BUILD_ID", "%teamcity.build.id%")
    }

    vcs {
        root(HackoladeRepository)
        root(MariaDBPluginGithubRepository, "+:. => ./MariaDB")
    }

    features {
        pullRequests {
            vcsRootExtId = "MariaDBPluginGithubRepository"
            provider = github {
                authType = vcsRoot()
                filterTargetBranch = "+:refs/heads/main"
                filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER
            }
        }
        commitStatusPublisher {
            vcsRootExtId = "MariaDBPluginGithubRepository"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "credentialsJSON:94c03b5b-2b09-4b69-afbf-6ddc4c5a61d7"
                }
            }
            param("github_oauth_user", "VitaliiBedletskyi")
        }
    }

    triggers {
        vcs {
            triggerRules = "+:root=MariaDBPluginGithubRepository:**"
            branchFilter = "+:*"
        }
    }


    steps {
        script {
            name = "Start BuildKit"
            scriptContent = """
                if [ "${'$'}(docker buildx ls | grep %buildx_builder_instance_name% 2>/dev/null || true)" = "" ]; then
                	docker buildx create --name %buildx_builder_instance_name% \
                		--use --driver docker-container --driver-opt image=moby/buildkit:master
                fi
                
                docker buildx use --default %buildx_builder_instance_name%
                docker buildx inspect --bootstrap %buildx_builder_instance_name%
            """.trimIndent()
        }
        script {
            name = "Show FS structure"
            scriptContent = "find ./ci-cd | sed -e \"s/[^-][^\\/]*\\// |/g\" -e \"s/|\\([^ ]\\)/|-\\1/\""
        }
        script {
            name = "Run eslint and build plugin"
            scriptContent = "docker buildx bake -f ./ci-cd/plugins/docker-bake.hcl"
            dockerImage = "%docker_builder_image%"
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
        }
    }
})
