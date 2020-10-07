plugins {
    kotlin("jvm")
}

repositories {
    google()
    jcenter()
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation("com.android.tools.build:gradle:3.5.0")
    implementation(kotlin("stdlib", "1.3.0"))
}

group = "com.naman14.accord"
version = "0.1"

apply {
    from("https://raw.githubusercontent.com/sky-uk/gradle-maven-plugin/master/gradle-mavenizer.gradle")
}
