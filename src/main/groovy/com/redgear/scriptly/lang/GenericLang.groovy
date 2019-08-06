package com.redgear.scriptly.lang

import com.redgear.scriptly.repo.Repository

import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

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
      throw new Exception("Failed to load Script Engine. Either this language is not supported or the script is missing the correct dependency. ")
    }

    runWithEngine(engine, deps, args)
  }

  ClassLoader buildClassLoader(Language.DepInfo deps) {
    def urls = deps.deps.collect {
      it.toURI().toURL()
    }

    return new URLClassLoader(urls as URL[], Thread.currentThread().contextClassLoader.parent)
  }

  void runWithEngine(ScriptEngine engine, Language.DepInfo deps, List<String> args) {
    def bind = engine.createBindings()

    bind.put('args', args as String[])

    engine.setBindings(bind, ScriptContext.ENGINE_SCOPE)

    engine.eval(deps.source)
  }

}
