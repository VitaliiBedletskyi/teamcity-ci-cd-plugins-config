package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.sshAgent
import jetbrains.buildServer.configs.kotlin.buildSteps.ScriptBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'MariaDbReleasePlugin'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("MariaDbReleasePlugin")) {
    params {
        add {
            param("env.PLUGIN_PATH", "./hackolade-plugin")
        }
        add {
            param("env.NEXT_DEVELOPMENT_VERSION", "", "webPopulatedSelect", "method" to "GET", "display" to "prompt", "format" to "json", "label" to "Next development version", "tagSupport" to "true", "url" to "https://testpluginstorage.blob.core.windows.net/plugins/MariaDB/nextDevelopmentVersion.json", "enableEditOnError" to "true")
        }
        add {
            text("env.RELEASE_DESCRIPTION", "", label = "Release description", display = ParameterDisplay.PROMPT, allowEmpty = true)
        }
        add {
            text("env.RELEASE_TITLE", "", label = "Release title", display = ParameterDisplay.PROMPT, allowEmpty = true)
        }
        add {
            param("env.CURRENT_PLUGIN_VERSION", "", "webPopulatedSelect", "method" to "GET", "display" to "prompt", "format" to "json", "readOnly" to "true", "label" to "Current plugin version", "url" to "https://testpluginstorage.blob.core.windows.net/plugins/MariaDB/currentReleaseVersion.json", "enableEditOnError" to "true")
        }
    }

    vcs {

        check(cleanCheckout == false) {
            "Unexpected option value: cleanCheckout = $cleanCheckout"
        }
        cleanCheckout = true

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
            name = "Build plugin and upload artifact to azure and dockerhub"
            scriptContent = "docker buildx bake -f ./ci-cd/plugins/docker-bake.hcl release"
            dockerImage = "%docker_builder_image%"
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
            dockerRunParameters = "%global_docker_volumes_mounts%"
        }
    }
    steps {
        update<ScriptBuildStep>(1) {
            name = "Check SSH"
            enabled = false
            clearConditions()
            scriptContent = """
                ssh-keyscan github.com >> ~/.ssh/known_hosts
                ssh -T git@github.com
            """.trimIndent()
            dockerImage = ""
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Any
            dockerRunParameters = ""
        }
        insert(2) {
            script {
                name = "Build plugin and upload artifact to azure and dockerhub"
                scriptContent = """
                    #mkdir -p -m 0600 ~/.ssh && ssh-keyscan github.com >> ~/.ssh/known_hosts
                    #ssh -T git@github.com
                    eval ${'$'}(ssh-agent)
                    ssh-add ~/.ssh/id_rsa
                    
                    docker buildx bake -f ./ci-cd/plugins/docker-bake.hcl --set release.ssh=default=${'$'}SSH_AUTH_SOCK release
                """.trimIndent()
                dockerImage = "%docker_builder_image%"
                dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
                dockerRunParameters = "%global_docker_volumes_mounts%"
            }
        }
    }

    features {
        add {
            sshAgent {
                teamcitySshKey = "GeneralTeamcitySSH.pub"
            }
        }
    }
}
