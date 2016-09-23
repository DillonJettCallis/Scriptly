package com.redgear.scriptly.server.lang

import com.redgear.scriptly.server.repo.Repository
import org.apache.commons.lang3.SystemUtils

import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

/**
 * Created by LordBlackHole on 9/2/2016.
 */
class ScalaLang implements Language {

    @Override
    Closure exec(File source, Repository repo) {
        def deps = parse(source, repo)

        def all = deps.deps

        String pathSeperator = ':'

        if(SystemUtils.IS_OS_WINDOWS) {
            pathSeperator = ';'
        }

        def newPath = all.collect{it.toString()}.join(pathSeperator)

        def urls = all.collect {it.toURI().toURL()}

        def loader = new URLClassLoader (urls as URL[], Thread.currentThread().contextClassLoader.parent)

        def manager = new ScriptEngineManager(loader)

        ScriptEngine engine = manager.getEngineByName('scala')

        if(engine == null){
            throw new Exception('Could not find Scala. Is it included on the class path?')
        }

        engine.settings().classpath().value_$eq(newPath)

        return { List<String> args ->
            def bind = engine.createBindings()

            bind.put('args', args as String[])

            engine.setBindings(bind, ScriptContext.ENGINE_SCOPE)

            deps.source.withReader {
                engine.eval(it)
            }
        }
    }

}
