#!
exec scriptly scala "$0" "$@"
!#

/*
org.scala-lang:scala-compiler:2.13.0
org.slf4j:slf4j-simple:1.7.21
 */

import org.slf4j.LoggerFactory

val log = LoggerFactory.getLogger("root")

log.info("Log from Scala: {}", args)

