plugins {
    id("org.jetbrains.intellij") version "0.5.0"
    java
    kotlin("jvm") version "1.3.61"
}

/*sourceSets {
    getByName("main").resources.srcDirs("src/main/resources")
}*/
group = "com.sanniou.plugin"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("compiler-embeddable"))
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2019.2"
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

configure<org.jetbrains.intellij.IntelliJPluginExtension> {
    version = "2019.2"
    updateSinceUntilBuild = false
    pluginName = "MultiItemTemplates"
}

tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes(
            """
      Supported  MultiItem 1.0.0.<br>
      <a href="https://github.com/sanniou/MultiItemKit">https://github.com/sanniou/MultiItemKit</a>"""
    )
    pluginDescription("""
     An intellij idea plugin to generate DataItem Templates.<br/><br/>
     P.S. MultiItem: An Android library binding View by DataItem
      <a href="https://github.com/sanniou/MultiItemKit">https://github.com/sanniou/MultiItemKit</a>
    """.trimIndent())
    sinceBuild("192.*")
    pluginId("com.sannniou.multiitem.plugin.templates")

}