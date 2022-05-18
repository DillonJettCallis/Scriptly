/*
org.jetbrains.kotlin:kotlin-scripting-jsr223:1.6.20

org.slf4j:slf4j-simple:1.7.36
*/

import org.slf4j.LoggerFactory

val log = LoggerFactory.getLogger("root")

log.info("Log from Kotlin: {}", args)
