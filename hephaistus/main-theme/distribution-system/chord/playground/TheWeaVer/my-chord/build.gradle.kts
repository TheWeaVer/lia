plugins {
    kotlin("jvm") version "1.9.20"
}

group = "com.lia.hephaistus.chord.theweaver"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}
