package com.redgear.scriptly.lang


import groovy.transform.CompileStatic

import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

@CompileStatic
class GenericLang implements Language {

  String name

  GenericLang(String name) {
    this.name = name
  }

  @Override
  void run(String src, Set<File> deps, String[] args) {
    def loader = buildClassLoader(deps)

    def engine = loadEngine(loader)

    runWithEngine(src, engine, loader, args)
  }

  ScriptEngine loadEngine(ClassLoader loader) {
    def manager = new ScriptEngineManager(loader)

    def engine = manager.getEngineByName(name)

    if (engine == null) {
      def languages = manager.engineFactories*.names.flatten()

      throw new Exception("Failed to load Script Engine. Either '$name' language is not supported or the script is missing the correct dependency.\n\nKnown languages are: $languages")
    }

    return engine
  }

  ClassLoader buildClassLoader(Set<File> deps) {
    def urls = deps.collect {
      it.toURI().toURL()
    }

    return new URLClassLoader(urls as URL[], Thread.currentThread().contextClassLoader.parent)
  }

  void runWithEngine(String source, ScriptEngine engine, ClassLoader loader, String[] args) {
    def oldLoader = Thread.currentThread().getContextClassLoader()

    try {
      Thread.currentThread().setContextClassLoader(loader)

      def bind = engine.getBindings(ScriptContext.ENGINE_SCOPE)

      bind.put('args', args)

      engine.eval(source)
    } finally {
      Thread.currentThread().setContextClassLoader(oldLoader)
    }
  }
}
