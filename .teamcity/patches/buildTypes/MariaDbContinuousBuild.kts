package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.ScriptBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.VcsTrigger
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'MariaDbContinuousBuild'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("MariaDbContinuousBuild")) {
    check(artifactRules == "+:./release/**/* => %system.teamcity.projectName%.zip") {
        "Unexpected option value: artifactRules = $artifactRules"
    }
    artifactRules = "+:./release/**/* => %env.PLUGIN_NAME%.zip"

    params {
        remove {
            param("env.TEAMCITY_BUILD_ID", "%teamcity.build.id%")
        }
        add {
            password("env.DOCKER_PASSWORD", "credentialsJSON:59be1e15-f0d0-44c3-b683-47cf1674098d", display = ParameterDisplay.HIDDEN)
        }
        add {
            param("env.BUILD_ID", "%teamcity.build.id%")
        }
        add {
            param("env.DOCKER_USERNAME", "bedletskyi")
        }
        add {
            param("env.BUILD_BRANCH", "%teamcity.build.branch%")
        }
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
            name = "Build plugin and upload artifact to azure and dockerhub"
            scriptContent = "docker buildx bake -f ./ci-cd/plugins/docker-bake.hcl publish-azure"
            dockerImage = "%docker_builder_image%"
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
            dockerRunParameters = "-v /var/run/docker.sock:/var/run/docker.sock -v /root/.docker:/root/.docker"
        }
    }
    steps {
        update<ScriptBuildStep>(1) {
            clearConditions()
            scriptContent = """
                PLUGIN_NAME=${'$'}(cd ${'$'}PLUGIN_PATH && npm pkg get name | tr -d '"')
                
                echo "##teamcity[setParameter name='env.PLUGIN_NAME' value='${'$'}PLUGIN_NAME']"
                
                docker login -u ${'$'}DOCKER_USERNAME -p ${'$'}DOCKER_PASSWORD
                docker buildx bake -f ./ci-cd/plugins/docker-bake.hcl publish
            """.trimIndent()
        }
    }

    triggers {
        val trigger1 = find<VcsTrigger> {
            vcs {
                triggerRules = "+:root=MariaDBPluginGithubRepository:**"

                branchFilter = "+:<default>"
            }
        }
        trigger1.apply {
            enabled = false
            branchFilter = """
                +:main
                +:master
            """.trimIndent()

        }
    }
}
