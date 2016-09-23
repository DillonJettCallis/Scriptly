package com.redgear.scriptly.server.lang

import com.redgear.scriptly.server.repo.Repository

import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

/**
 * Created by LordBlackHole on 9/4/2016.
 */
class GenericLang implements Language {

    String lang

    GenericLang(String lang) {
        this.lang = lang
    }

    @Override
    Closure exec(File source, Repository repo) {
        runWithEngine(source, repo, lang)
    }

    Closure runWithEngine(File source, Repository repo, String engineName) {
        def deps = parse(source, repo)

        def urls = deps.deps.collect {
            it.toURI().toURL()
        }

        def loader = new URLClassLoader (urls as URL[], Thread.currentThread().contextClassLoader.parent)

        def manager = new ScriptEngineManager(loader)

        ScriptEngine engine = manager.getEngineByName(engineName)

        if(engine == null){
            throw new Exception("Failed to load Script Engine. Either this language is not supported or the script is missing the correct dependency. ")
        }

        return {List<String> args ->
            def bind = engine.createBindings()

            bind.put('args', args as String[])

            engine.setBindings(bind, ScriptContext.ENGINE_SCOPE)

            deps.source.withReader {
                engine.eval(it)
            }
        }
    }

}
