"
 org.renjin:renjin-script-engine:3.5-beta76

 org.slf4j:slf4j-simple:1.7.36
"

import(org.slf4j.LoggerFactory)

log <- LoggerFactory$getLogger("root")

log$info("Hello from R: {}", args)
