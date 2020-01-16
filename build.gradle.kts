plugins {
    id("org.jetbrains.intellij") version "0.4.15"
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
    testCompile("junit", "junit", "4.12")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2019.3.1"
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

tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes(
        """
      Supported  MultiItem 1.0.0.<br>
      <a href="https://github.com/sanniou/MultiItemKit">https://github.com/sanniou/MultiItemKit</a>"""
    )
}