package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.buildSteps.ScriptBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.VcsTrigger
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'MariaDbPrCheckBuild'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("MariaDbPrCheckBuild")) {
    check(artifactRules == "+:./release/**/* => %system.teamcity.projectName%.zip") {
        "Unexpected option value: artifactRules = $artifactRules"
    }
    artifactRules = "+:./release/**/* => %env.PLUGIN_NAME%.zip"

    params {
        add {
            param("env.PLUGIN_PATH", "./hackolade-plugin")
        }
    }

    vcs {
        expectEntry(RelativeId("MariaDBPluginGithubRepository"), "+:. => ./MariaDB")
        root(RelativeId("MariaDBPluginGithubRepository"), "+:. => ./hackolade-plugin")
    }

    expectSteps {
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
            name = "Run eslint and build plugin"
            scriptContent = "docker buildx bake -f ./ci-cd/plugins/docker-bake.hcl"
            dockerImage = "%docker_builder_image%"
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
            dockerRunParameters = "%global_docker_volumes_mounts%"
        }
    }
    steps {
        update<ScriptBuildStep>(1) {
            clearConditions()
            scriptContent = """
                PLUGIN_NAME=${'$'}(cd ${'$'}PLUGIN_PATH && npm pkg get name | tr -d '"')
                echo "##teamcity[setParameter name='env.PLUGIN_NAME' value='${'$'}PLUGIN_NAME']"
                
                echo ${'$'}PLUGIN_PATH
                
                docker buildx bake -f ./ci-cd/plugins/docker-bake.hcl
            """.trimIndent()
        }
    }

    triggers {
        val trigger1 = find<VcsTrigger> {
            vcs {
                triggerRules = "+:root=MariaDBPluginGithubRepository:**"

                branchFilter = "+:*"
            }
        }
        trigger1.apply {
            branchFilter = """
                +:*
                -:<default>
            """.trimIndent()

        }
    }

    features {
        val feature1 = find<PullRequests> {
            pullRequests {
                vcsRootExtId = "MariaDBPluginGithubRepository"
                provider = github {
                    authType = vcsRoot()
                    filterTargetBranch = "+:refs/heads/main"
                    filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER
                }
            }
        }
        feature1.apply {
            provider = github {
                serverUrl = ""
                authType = vcsRoot()
                filterSourceBranch = ""
                filterTargetBranch = ""
                filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER
            }
        }
    }
}
