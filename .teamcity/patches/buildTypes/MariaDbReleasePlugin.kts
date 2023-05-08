package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.sshAgent
import jetbrains.buildServer.configs.kotlin.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'MariaDbReleasePlugin'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("MariaDbReleasePlugin")) {
    params {
        add {
            text("env.RELEASE_TITLE", "", label = "Release title", display = ParameterDisplay.PROMPT, allowEmpty = true)
        }
        add {
            param("CURRENT_PLUGIN_VERSION", "", "webPopulatedSelect", "method" to "GET", "display" to "prompt", "format" to "json", "readOnly" to "true", "label" to "Current plugin version", "url" to "https://testpluginstorage.blob.core.windows.net/plugins/MariaDB/currentReleaseVersion.json", "enableEditOnError" to "true")
        }
        add {
            param("env.NEXT_DEVELOPMENT_VERSION", "", "webPopulatedSelect", "method" to "GET", "display" to "prompt", "format" to "json", "label" to "Next development version", "url" to "https://testpluginstorage.blob.core.windows.net/plugins/MariaDB/nextDevelopmentVersion.json", "enableEditOnError" to "true")
        }
        add {
            text("env.RELEASE_DESCRIPTION", "", label = "Release description", display = ParameterDisplay.PROMPT, allowEmpty = true)
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
