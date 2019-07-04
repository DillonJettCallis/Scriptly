/*
org.jetbrains.kotlin:kotlin-scripting-jsr223:1.3.40

org.slf4j:slf4j-simple:1.7.21
*/

import org.slf4j.LoggerFactory

val args = bindings["args"] as Array<String>

val log = LoggerFactory.getLogger("root")



log.info("Log from Kotlin: {}", args)