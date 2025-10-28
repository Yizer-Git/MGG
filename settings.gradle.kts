pluginManagement {
    repositories {
        // Google 官方仓库
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        // 阿里云插件仓库镜像
        maven {
            url = uri("https://maven.aliyun.com/repository/gradle-plugin")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        // 阿里云公共仓库镜像（包含 Maven Central & JCenter 同步）
        maven {
            url = uri("https://maven.aliyun.com/repository/public")
        }
        // 阿里云 Google 镜像，解决 Google 仓库访问慢的问题
        maven {
            url = uri("https://maven.aliyun.com/repository/google")
        }
        mavenCentral()
    }
}

rootProject.name = "MGG"
include(":app")
include(":backend")
 
