package com.redgear.scriptly.lang

import com.redgear.scriptly.repo.Repository
import groovy.transform.CompileStatic

class ScalaLang extends GenericLang {

  ScalaLang() {
    super('scala')
  }

  @Override
  @CompileStatic
  void run(File source, Repository repo, String engineName, List<String> args) {
    def deps = parse(source, repo)

    def loader = buildClassLoader(deps)

    def oldLoader = Thread.currentThread().getContextClassLoader()

    try {
      Thread.currentThread().setContextClassLoader(loader)

      def newPath = deps.deps.collect { it.toString() }.join(File.pathSeparator)

      if (hasClass(loader, 'scala.tools.nsc.interpreter.shell.ReplReporterImpl')) {
        runScala13(deps.source, loader, newPath, args)
      } else {
        runScala12(deps.source, loader, newPath, args)
      }

    } finally {
      Thread.currentThread().setContextClassLoader(oldLoader)
    }
  }

  @CompileStatic
  private static boolean hasClass(ClassLoader loader, String clazz) {
    try {
      loader.loadClass(clazz)
      return true
    } catch (ClassNotFoundException ignored) {
      return false
    }
  }

  private static void runScala12(String src, ClassLoader loader, String newPath, List<String> args) {
    def settings = loader.loadClass("scala.tools.nsc.Settings").newInstance([] as Object[])
    settings.classpath().value_$eq(newPath)

    def writer = new StringWriter()
    def output = new PrintWriter(writer)

    def main = loader.loadClass("scala.tools.nsc.interpreter.IMain").newInstance([settings, output] as Object[])

    main.interpret("val args = Array( ${ args.collect { '"' + it + '"' }.join(", ") } );")
    def result = main.interpret(src)

    output.close()

    if ("$result" == 'Error') {
      println(writer)
    }
  }

  private static void runScala13(String src, ClassLoader loader, String newPath, List<String> args) {
    def settings = loader.loadClass("scala.tools.nsc.Settings").newInstance([] as Object[])
    settings.classpath().value_$eq(newPath)

    def writer = new StringWriter()
    def output = new PrintWriter(writer)

    def reporter = loader.loadClass("scala.tools.nsc.interpreter.shell.ReplReporterImpl").newInstance([settings, output] as Object[])

    def main = loader.loadClass("scala.tools.nsc.interpreter.IMain").newInstance([settings, reporter] as Object[])

    main.interpret("val args = Array( ${ args.collect { '"' + it + '"' }.join(", ") } );")
    def result = main.interpret(src)

    output.close()

    if ("$result" == 'Error') {
      println(writer)
    }
  }

}
