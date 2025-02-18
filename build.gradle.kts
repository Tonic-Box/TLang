plugins {
    id("java")
}

group = "com.tonic"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    //lombok
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    testCompileOnly("org.projectlombok:lombok:1.18.32")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.32")

    //ASM
    implementation("org.ow2.asm:asm:9.6")
    implementation("org.ow2.asm:asm-tree:9.6")
    implementation("org.ow2.asm:asm-util:9.6")
    implementation("org.ow2.asm:asm-commons:9.6")

    //google
    implementation("com.google.guava:guava:32.0.1-android")

    implementation("org.antlr:antlr4-runtime:4.13.1")
}

tasks {
    register("CompileTLangGrammar", Exec::class) {
        setWorkingDir("src\\main\\antlr\\")
        commandLine("cmd", "/c", "java -jar antlr.jar TLang.g4"
                + " -o ..\\java\\com\\tonic\\model\\antlr"
                + " -visitor -no-listener"
        )
        doLast {
            println("Executed!")
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<JavaExec>("runApp") {
    group = "application"
    description = "Runs the Main class after tests pass"
    // Ensure tests are executed before running the app
    dependsOn(tasks.test)
    mainClass.set("com.tonic.demo.Main")
    classpath = sourceSets["main"].runtimeClasspath
}