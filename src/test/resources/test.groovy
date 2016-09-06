/*

org.codehaus.groovy:groovy-all:2.3.11

org.slf4j:slf4j-simple:1.7.21


 */

import org.slf4j.LoggerFactory

def log = LoggerFactory.getLogger('root')



log.info('Log from Groovy: {}', args)