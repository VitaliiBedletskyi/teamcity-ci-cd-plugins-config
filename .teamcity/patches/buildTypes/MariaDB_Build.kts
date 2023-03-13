package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, create a buildType with id = 'MariaDB_Build'
in the project with id = 'HackoladePlugins_Project_MariaDB', and delete the patch script.
*/
create(RelativeId("HackoladePlugins_Project_MariaDB"), BuildType({
    templates(RelativeId("HackoladePlugins_Project_Build"))
    id("MariaDB_Build")
    name = "Build"
}))

