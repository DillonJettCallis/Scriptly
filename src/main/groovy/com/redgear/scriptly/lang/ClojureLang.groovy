package com.redgear.scriptly.lang

import com.redgear.scriptly.repl.GenericRepl
import com.redgear.scriptly.repl.Repl

class ClojureLang implements Language {

  String name = 'clojure'

  @Override
  void run(String src, Set<File> deps, String[] args) {

    def urls = deps.collect {
      it.toURI().toURL()
    }

    def loader = new URLClassLoader(urls as URL[], Thread.currentThread().contextClassLoader.parent)

    def oldLoader = Thread.currentThread().getContextClassLoader()

    try {
      Thread.currentThread().setContextClassLoader(loader)

      def (eval, read) = loadEval(loader)

      evaluate(eval, read, src, args)

    } catch (ClassNotFoundException e) {
      throw new Exception('Could not find Clojure. Is it included on the class path?', e)
    } finally {
      Thread.currentThread().setContextClassLoader(oldLoader)
    }
  }

  @Override
  Repl repl(Set<File> deps) {
    def urls = deps.collect {
      it.toURI().toURL()
    }

    def loader = new URLClassLoader(urls as URL[], Thread.currentThread().contextClassLoader.parent)

    def (eval, read) = loadEval(loader)

    return new ClojureRepl(eval, read)
  }

  @Override
  String commentStart() {
    return '"'
  }

  @Override
  String commentEnd() {
    return '"'
  }

  private static List loadEval(ClassLoader loader) {
    try {
      def Clojure = loader.loadClass('clojure.java.api.Clojure')

      def var = Clojure.getMethod('var', Object.class)

      def eval = var.invoke(null, 'clojure.core/eval')

      def read = Clojure.getMethod('read', String.class)

      return [eval, read]
    } catch (ClassNotFoundException e) {
      throw new Exception('Could not find Clojure. Is it included on the class path?', e)
    }
  }

  private void evaluate(def eval, def read, String source, String[] args) {
    def argArray = args.collect { '"' + it + '"' }.join(' ')

    eval.invoke(read.invoke(null, "(def args [${argArray}])".toString()))

    eval.invoke(read.invoke(null, "(do $source)".toString()))
  }

  private static class ClojureRepl implements Repl {

    private final def eval
    private final def read

    ClojureRepl(def eval, def read) {
      this.eval = eval
      this.read = read
    }

    @Override
    Object nextLine(String raw) {
      return eval.invoke(read.invoke(null, "(do $raw)".toString()))
    }
  }

}
