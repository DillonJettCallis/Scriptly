package com.redgear.scriptly.lang

import com.redgear.scriptly.repo.Repository
import groovy.transform.CompileStatic

import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

@CompileStatic
class GenericLang implements Language {

  String lang

  GenericLang(String lang) {
    this.lang = lang
  }

  @Override
  void exec(File source, Repository repo, List<String> args) {
    run(source, repo, lang, args)
  }

  void run(File source, Repository repo, String engineName, List<String> args) {
    def deps = parse(source, repo)

    def loader = buildClassLoader(deps)

    def manager = new ScriptEngineManager(loader)

    ScriptEngine engine = manager.getEngineByName(engineName)

    if (engine == null) {
      throw new Exception("Failed to load Script Engine. Either '$lang' language is not supported or the script is missing the correct dependency. ")
    }

    runWithEngine(engine, loader, deps, args)
  }

  ClassLoader buildClassLoader(DepInfo deps) {
    def urls = deps.deps.collect {
      (it as File).toURI().toURL()
    }

    return new URLClassLoader(urls as URL[], Thread.currentThread().contextClassLoader.parent)
  }

  void runWithEngine(ScriptEngine engine, ClassLoader loader, DepInfo deps, List<String> args) {
    def oldLoader = Thread.currentThread().getContextClassLoader()

    try {
      Thread.currentThread().setContextClassLoader(loader)

      def bind = engine.getBindings(ScriptContext.ENGINE_SCOPE)

      bind.put('args', args)

      engine.eval(deps.source)
    } finally {
      Thread.currentThread().setContextClassLoader(oldLoader)
    }
  }
}
