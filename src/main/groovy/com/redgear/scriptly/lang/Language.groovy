package com.redgear.scriptly.lang


import com.redgear.scriptly.repo.Repository
import groovy.transform.CompileStatic

import static org.apache.commons.lang3.StringUtils.isBlank

@CompileStatic
trait Language {

  abstract String name

  void exec(File source, Repository repo, String[] args) {
    def info = parse(source, repo)

    run(info.source, info.deps as Set<File>, args)
  }

  abstract void run(String src, Set<File> deps, String[] args)

  static Language lookup(String lang) {
    if (lang == 'ruby' || lang == 'jruby') {
      return new RubyLang(lang)
    } else if (lang == 'python' || lang == 'jython') {
      return new PythonLang(lang)
    } else if (lang == 'kotlin') {
      return new KotlinLang()
    } else if (lang == 'lua' || lang == 'luaj') {
      return new LuaLang(lang)
    } else if(lang == 'Renjin') {
      return new RenjinLang(lang)
    } else {
      return new GenericLang(lang)
    }
  }

  String commentStart() {
    return '/*'
  }

  String commentEnd() {
    return '*/'
  }

  DepInfo parse(File source, Repository repo) {
    def scriptBuilder = new StringBuilder()

    def start = commentStart()
    def startIndex = start.length()

    def end = commentEnd()
    def endIndex = end.length()

    def result = new DepInfo()

    source.withReader { reader ->
        def nextLine = reader.readLine()

        while (isBlank(nextLine)) {
          nextLine = reader.readLine()
        }

        nextLine = skipSheBang(nextLine, reader, '#!', '!#')
        nextLine = skipSheBang(nextLine, reader, '::#!', '::!#')


        if (nextLine.trim().startsWith(start)) {
          def builder = new StringBuilder()

          nextLine = nextLine.substring(nextLine.indexOf(start) + startIndex)

          while (!nextLine.contains(end)) {
            builder << nextLine
            builder << '\n'
            nextLine = reader.readLine()
          }

          def index = nextLine.indexOf(end)
          builder << nextLine.substring(0, index)

          result.deps.addAll(downloadDeps(builder.toString(), repo))

          nextLine = nextLine.substring(index + endIndex)
        }

      scriptBuilder << nextLine
      scriptBuilder << reader.text
    }

    result.source = scriptBuilder.toString()

    return result
  }

  String skipSheBang(String nextLine, Reader reader, String start, String end) {
    if (nextLine.trim().startsWith(start)) {

      while (!nextLine.contains(end)) {
        nextLine = reader.readLine()
      }

      nextLine = nextLine.substring(nextLine.indexOf(end) + end.length())

      while (isBlank(nextLine)) {
        nextLine = reader.readLine()
      }
    }

    return nextLine
  }


  List<File> downloadDeps(String deps, Repository repo) {

    def separate = deps.trim().split(/\s+/)

    def coords = separate.collect { it.trim() }

    repo.resolvePackages(coords)
  }

  static class DepInfo {

    String source

    Set<File> deps = []

  }

}
