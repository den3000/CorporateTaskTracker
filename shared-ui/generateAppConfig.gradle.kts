import java.util.Properties

tasks.register("generateAppConfig") {
    // 1. Читаем local.properties
    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { localProperties.load(it) }
    }

    // 2. Цепочка приоритетов: терминал (-P) -> local.properties -> "" (дефолт)
    val serverIp = (project.findProperty("SERVER_IP") as? String)
        ?: localProperties.getProperty("SERVER_IP")
        ?: ""

    // 3. Кеширование (чтобы не пересобирать без необходимости)
    inputs.property("serverIp", serverIp)

    val outputDir = project.layout.buildDirectory.dir("generated/appconfig/kotlin")
    outputs.dir(outputDir)

    doLast {
        val file = File(outputDir.get().asFile, "ru/den/writes/code/config/AppConfig.kt")
        file.parentFile.mkdirs()
        file.writeText("""
            package ru.den.writes.code.config

            object AppConfig {
                // Если сюда придет пустая строка, ваш NetworkMonitor 
                // сам подставит 10.0.2.2 или 127.0.0.1
                const val SERVER_IP: String = "$serverIp"
            }
        """.trimIndent())
    }
}
