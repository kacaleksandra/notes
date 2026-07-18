pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    // TODO: There are some problems with repositories not being resolved. Report to Fluttrr.
    repositoriesMode = RepositoriesMode.PREFER_SETTINGS // was FAIL_ON_PROJECT_REPOS, but it doesn't work with Flutter
    repositories {
        google()
        mavenCentral()
        val flutterStorageUrl = System.getenv("FLUTTER_STORAGE_BASE_URL") ?: "https://storage.googleapis.com"
        maven("$flutterStorageUrl/download.flutter.io")
    }
}

rootProject.name = "notes"
include(":app")

// Replace "flutter_module" with whatever package_name you supplied when you ran:
// `$ flutter create -t module [package_name]
val filePath = settingsDir.parentFile.toString() + "/flutter_module/.android/include_flutter.groovy"
apply(from = File(filePath))
