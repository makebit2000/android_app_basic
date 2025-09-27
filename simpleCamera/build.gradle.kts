// 根 build.gradle.kts
// 不要放 repositories，这里只处理通用插件和配置
plugins {
    // 根项目不应用 Android/Kotlin 插件
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
