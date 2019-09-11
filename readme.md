# Scriptly

## Goals

To be able to run any JVM language script 
* ... with Maven dependencies,
* ... without needing to install those languages
* ... and to work the same on both *nix and Windows. 


## How it works
Scriptly is pretty simple actually, so simple I can't believe no one has tried it before. 
You execute the Scriptly main method passing in the name of the language and a file. 

Example: "scriptly kotlin C:\myScript.kotlin someArgForScript"

Scriptly will read in the file. 


* If your file starts with a `#!` or `::#!`, then all lines until a corresponding `!#` or `::!#`  will be skipped. 
 This is to allow standalone shell scripts in either bash or cmd. (More info below).
* Then Scriptly will look for a comment. This will depend on the language, but by default it will try to find a C 
style block comment begining with /* and ending with */. (Please note that any extra '\*' are not allowed)
* If that comment is found Scriptly will read it and it will expect it to contain a list of Maven dependencies
delimited by whitespace in Gradle short style, ie: groupId:artifactId:version. 
For example: `"org.slf4j:slf4j-api:1.7.21 org.slf4j:slf4j-simple:1.7.21"`
* Scriptly will then take that list and resolve all those dependencies, using your local maven repo + central + 
other repos you can configure in a json file located in ~/.scriptly/config.json (Which will be generated on first run)
* Using those dependencies, Scriptly will take the rest of your file and run it through the ScriptEngine of the correct
language, passing any extra arguments you provided in a Java String array variable named "args". 


## Language implementations

* Java is not supported as it don't have a standard jsr223 implementation. 
 Non-standard implementations do exist, but use at your own risk.
 Try using Groovy instead, it supports most Java syntax
 
* Groovy works with no special casing at all. Just include the Groovy library in your dependencies.
 
* Kotlin is supported, just include the Kotlin jsr223 implementation.
 
* Scala has a jsr223 implementation but it's just a thin wrapper around the Scala repl and isn't 
 fully compliant so I've been forced to use the underlying scripting support which has breaking 
 changes between versions. Tested with scala 1.13, 1.12 and 1.11

* Clojure is special cased because there is no official Clojure jsr223 implementation, however it has it's own
 scripting api that works well and is very simple. The only important note is that Clojure users a String 
 instead of a comment to hold it's maven dependencies. Just use a single double-quote string at the top of your file. 
 
* Javascript requires no special casing at all. If you're on Java 8+ nashorn will be used and before that, rhino. 
You can in fact use 'nashorn', 'rhino' or 'ecmascript' as language names. 

* Ruby only has special casing for comments. Put JRuby in your dependencies, call it using language 'ruby' or 'jruby'
and the parser will look for the comment using "=being" and "=end" at the top of your file. Single line comments are 
NOT supported. Your arguments will be available in a global Java String array named 'args' so you'll have to use "$args.to_a"
to convert it to a Ruby array. 

* Python like Ruby only has special casing for comments. Just include the Jython standalone, call with 'python' or 'jython' and 
put your dependencies in a triple-single-quoted string. Double quoted strings are NOT supported. 
Example: '''org.python:jython-standalone:2.7.0'''

* Lua is supported, though I've only tested with the Luaj implementation, using the language name 'lua' 
 and lua's block comments `--[[` and `]]` should be enough.
 
* Ceylon, Gosu and Golo are NOT supported as they don't have jsr223 implantations. 

* Any language with a jar that fully supports jsr223 should work, with the note that you
 must used Java style comments even if that language doesn't. 


## Why do I have to include my language's compiler/language?

* That way, you can use whatever version you want. If I provided Groovy 2.6 and you wanted to use 3.0.0-beta-2
you'd be out of luck. This way, you can just use whatever Groovy version you want. You could use a different
groovy version in one script than another one right beside it. Or even in a different language. 

## Standalone Shell Scripts

Here are some examples on how you can use Scriptly for standalone polyglot shell scripts. 

* *nix
```bash
#!
exec scriptly groovy "$0" "$@"
!#

/*
org.codehaus.groovy:groovy-jsr223:3.0.0-beta-2
*/

// The rest of the script is now groovy
println 'Hello World'
```

* Windows
```batch 
::#!
@echo off
scriptly groovy %~dpnx0 %*
goto :eof
::!#

/*
org.codehaus.groovy:groovy-jsr223:3.0.0-beta-2
*/

// The rest of the script is now groovy
println 'Hello World'
```
