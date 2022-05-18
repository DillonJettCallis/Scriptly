# Scriptly

## Goals

To be able to run any JVM language script 
* ... with Maven dependencies,
* ... without needing to install those languages
* ... and to work the same on both *nix and Windows. 


## How it works
Scriptly is pretty simple actually, so simple I can't believe no one has tried it before. 
You execute the Scriptly main method passing in the name of the language and a file. 

Example: "scriptly exec kotlin C:\myScript.kotlin someArgForScript"

Scriptly will read in the file. 


* If your file starts with a `#!` or `::#!`, then all lines until a corresponding `!#` or `::!#`  will be skipped. 
 This is to allow standalone shell scripts in either bash or cmd. (More info below).
* Then Scriptly will look for a comment. This will depend on the language, but by default it will try to find a Java 
style block comment begining with /* and ending with */. (Please note that any extra '\*' are not allowed)
* If that comment is found Scriptly will read it and it will expect it to contain a list of Maven dependencies
delimited by whitespace in Gradle short style, ie: groupId:artifactId:version. 
For example: `"org.slf4j:slf4j-api:1.7.21 org.slf4j:slf4j-simple:1.7.21"`
* Scriptly will then take that list and resolve all those dependencies, using your local maven repo + central + 
other repos you can configure in a json file located in ~/.scriptly/config.json (Which will be generated on first run)
* Using those dependencies, Scriptly will take the rest of your file and run it through the ScriptEngine of the correct
language, passing any extra arguments you provided in a Java String array variable named "args". 


## Language implementations

* Java is not supported as it doesn't have a standard jsr223 implementation. 
 Non-standard implementations do exist, but use at your own risk.
 Try using Groovy instead, it supports most Java syntax

* Groovy works with no special casing at all. Just include the Groovy library in your dependencies.

* Kotlin is supported, just include the Kotlin jsr223 implementation.

* Scala supposedly has a jsr223 implementation, but it does not seem to actually work. You are welcome to try as in theory
 it shouldn't need any special handling.

* Clojure has not official jsr223 implementation. There are unofficial ones, but you use them at your own risk.

* Javascript requires no special casing at all. Java 8 through 14 included the nashorn engine by default, so you won't
 even need any dependencies but for Java 15+ it is still possible to add an explicit nashorn dependency.

* Ruby only has special casing for comments. Put JRuby in your dependencies, call it using language 'ruby' or 'jruby'
and the parser will look for the comment using `=being` and `=end` at the top of your file. Single line comments are 
NOT supported. Your arguments will be available in a Java String array named 'args' so you'll have to use "args.to_a"
to convert it to a Ruby array. 

* Python like Ruby only has special casing for comments. Just include the Jython standalone, call with 'python' or 'jython' and 
put your dependencies in a triple-single-quoted string. Double quoted strings are NOT supported. 
Example: `'''org.python:jython-standalone:2.7.0'''`

* Lua is supported, though I've only tested with the Luaj implementation, using the language name 'lua' 
 and lua's block comments `--[[` and `]]` should be enough.

* R is supported with the Renjin implementation. The language name is 'Renjin' (yes with a capital 'R') and
 you must use a string instead of comments to hold your dependencies. Other engines may or may not work
 and if they do you'll have to use java style block comments as that is the default.

* Ceylon, Gosu and Golo are NOT supported as they don't have jsr223 implementations. 

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
exec scriptly exec groovy "$0" "$@"
!#

/*
org.apache.groovy:groovy-jsr223:4.0.2
*/

// The rest of the script is now groovy
println 'Hello World'
```

* Windows
```batch 
::#!
@echo off
scriptly exec groovy %~dpnx0 %*
goto :eof
::!#

/*
org.apache.groovy:groovy-jsr223:4.0.2
*/

// The rest of the script is now groovy
println 'Hello World'
```
