package patches.templates

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.Template
import jetbrains.buildServer.configs.kotlin.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, create a template with id = 'PluginsBuildTemplate'
in the project with id = 'HackoladePlugins_Project', and delete the patch script.
*/
create(RelativeId("HackoladePlugins_Project"), Template({
    id("PluginsBuildTemplate")
    name = "Plugins Build Template"
}))

