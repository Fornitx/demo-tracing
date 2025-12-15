pluginManagement {
    plugins {
        id("org.springframework.boot") version settings.extra["spring-boot.version"] as String
        id("io.spring.dependency-management") version settings.extra["spring-dm.version"] as String
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
    }
}

rootProject.name = "demo-tracing"
