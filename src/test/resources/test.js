
/*
 org.openjdk.nashorn:nashorn-core:15.4

 org.slf4j:slf4j-simple:1.7.36

 */


var LoggerFactory = Java.type('org.slf4j.LoggerFactory');

var log = LoggerFactory.getLogger('root');


log.info('A log from javascript: {}', args)






