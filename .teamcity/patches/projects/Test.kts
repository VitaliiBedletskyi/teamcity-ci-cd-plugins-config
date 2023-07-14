package patches.projects

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the project with id = 'Test'
accordingly, and delete the patch script.
*/
changeProject(RelativeId("Test")) {
    params {
        remove {
            text("env.RELEASE_TITILE", "", label = "Release titile", display = ParameterDisplay.PROMPT, allowEmpty = true)
        }
        add {
            text("env.RELEASE_TITLE", "", label = "Release titile", display = ParameterDisplay.PROMPT, allowEmpty = true)
        }
    }
}
