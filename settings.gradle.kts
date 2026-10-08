pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    
    resolutionStrategy {
        eachPlugin {
            when {
                requested.id.id == "com.android.application" || 
                requested.id.id == "com.android.library" -> {
                    val module = "com.android.tools.build:gradle:8.2.2"
                    useModule(module)
                }
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "FinanzasPersonalesGT"
include(":app")
