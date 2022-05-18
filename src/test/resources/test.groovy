/*

org.apache.groovy:groovy-jsr223:4.0.2

org.slf4j:slf4j-simple:1.7.36

 */

import org.slf4j.LoggerFactory

def log = LoggerFactory.getLogger('root')

log.info('Log from Groovy: {}', args)
