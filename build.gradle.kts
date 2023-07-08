plugins {
    id("mod.base-conventions")
}

group = "com.minepalm"
version = "1.0.0-SNAPSHOT"

dependencies {
    implementation(libs.ignite)
    implementation(libs.mixin)

    paperweight.paperDevBundle("1.19.4-R0.1-SNAPSHOT")
}
