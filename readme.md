#Scriptly

##Goals

To be able to run any JVM language script 
* ... with Maven dependencies,
* ... without needing to install those languages
* ... and to work the same on both *nix and Windows. 


##How it works
Scriptly is pretty simple actually, so simple I can't believe no one has tried it before. 
You execute the Scriptly main method passing in the name of the language and a file. 

Example: "scriptly scala C:\myScalaScript.scala someArgForScript"

Scriptly will read in the file. 


* If your file starts with a #!, the first line will be skipped. 
* After that if the first line starts with 'rem' ....
* Then Scriptly will look for a comment. This will depend on the language, but by default it will try to find a C 
style block comment begining with /* and ending with */. (Please note that any extra '\*' are not allowed)
* If that comment is found Scriptly will read it and it will expect it to contain a list of Maven dependencies
delimited by whitespace in Gradle short style, ie: groupId:artifactId:version. 
For example: "org.slf4j:slf4j-api:1.7.21 org.slf4j:slf4j-simple:1.7.21"
* Scriptly will then take that list and resolve all those dependencies, using your local maven repo + central + 
other repos you can configure in a json file located in ~/.scriptly/config.json (Which will be generated on first run)
* Using those dependencies, Scriptly will take the rest of your file and run it through the ScriptEngine of the correct
language, passing any extra arguments you provided in a Java String array variable named "args". 


##Language implementations

* Java is implemented using a library called RelProxy, which is included in the Scriptly distro. 
The Java support is fairly limited, as Java wasn't made for scripting and neither was RelProxy. 
You can only have one class, it must be named properly according to Java rules (ie: class Script must be inside 
class Script.java) and it must have a public static void main(String[] args) method. Still, it's better than nothing
 and at least is an option if you only know Java and don't want to learn something else. (Although I would still
 recommend trying Groovy if you want to do more.)
 
* Groovy works with no special casing at all. Just include the Groovy library in your dependencies.
 
* Scala has a small amount of special casing, as it's ScriptEngine isn't 100% compliant with js223. This shouldn't 
effect users except that it makes the Scala scripts more likely to break in the future with Scala updates. Other than 
that warning, just include the scala compiler on your dependencies. 

* Clojure is special cased because there is no official Clojure jsr223 implementation. That shouldn't matter much
unless the Clojure script api ever changes. The only important note is that Clojure users a String instead of a comment
to hold it's maven dependencies. Just use a single double-quote string at the top of your file. 
 
* Javascript requires no special casing at all. If you're on Java 8+ nashorn will be used and before that, rhino. 
You can in fact use 'nashorn', 'rhino' or 'ecmascript' as language names. 

* Ruby only has special casing for comments. Put JRuby in your dependencies, call it using language 'ruby' or 'jruby'
and the parser will look for the comment using "=being" and "=end" at the top of your file. Single line comments are 
NOT supported. Your arguments will be available in a global Java String array named 'args' so you'll have to use "$args.to_a"
to convert it to a Ruby array. 

* Python like Ruby only has special casing for comments. Just include the Jython standalone, call with 'python' or 'jython' and 
put your dependencies in a triple-single-quoted string. Double quoted strings are NOT supported. 
Example: '''org.python:jython-standalone:2.7.0'''

* Kotlin is NOT supported yet, only because Kotlin doesn't support jsr223. If/when they add that, Scriptly should
instantly work with it. There may be unofficial implementations of jsr223 for Kotlin and anyone is free to try those. 

* Ceylon: Same as Kotlin. 

* Any language with a jar that fully supports jsr223 should work, with the note that you
must used Java style comments even if that language doesn't. 




##Why do I have to include my language's compiler/language?

* That way, you can use whatever version you want. If I provided Scala 2.11 and you wanted to use 2.12
you'd be out of luck. This way, you can just use whatever Scala version you want. You could use a different
scala version in one script than another one right beside it. Or even in a different language. 




