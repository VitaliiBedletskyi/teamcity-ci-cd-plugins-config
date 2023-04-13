package patches.projects

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the root project
accordingly, and delete the patch script.
*/
changeProject(DslContext.projectId) {
    params {
        add {
            param("env.AZURE_STORAGE_ACCOUNT_NAME", "testpluginstorage")
        }
        add {
            param("env.AZURE_STORAGE_ACCOUNT_SERVICE_PRINCIPAL_APP_ID", "6346b2fb-a67c-488c-a68d-cbd60aec1c19")
        }
        add {
            param("env.AZURE_STORAGE_CONTAINER_NAME", "plugins")
        }
        add {
            password("env.AZURE_STORAGE_ACCOUNT_SERVICE_PRINCIPAL_SECRET", "credentialsJSON:2646bf3b-d244-4150-a8f3-5ea7cb24e404", display = ParameterDisplay.HIDDEN, readOnly = true)
        }
    }
}
