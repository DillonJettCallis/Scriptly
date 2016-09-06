package com.redgear.scriptly.lang

import com.redgear.scriptly.repo.Repository

/**
 * Created by LordBlackHole on 9/2/2016.
 */
class ClojureLang implements Language {

    @Override
    void exec(File source, Repository repo, List<String> args) {
        def deps = parse(source, repo)

        def urls = deps.deps.collect {
            it.toURI().toURL()
        }

        def loader = new URLClassLoader (urls as URL[], Thread.currentThread().contextClassLoader.parent)

        def oldLoader = Thread.currentThread().getContextClassLoader()

        try {
            Thread.currentThread().setContextClassLoader(loader)

            def Clojure = loader.loadClass('clojure.java.api.Clojure')

            def var = Clojure.getMethod('var', Object.class)

            def eval = var.invoke(null, 'clojure.core/eval')

            def read = Clojure.getMethod('read', String.class)

            def argArray = args.collect{'"' + it + '"'}.toString()

            eval.invoke(read.invoke(null, "(def args ${argArray})".toString()))

            eval.invoke(read.invoke(null, """(load-file "${deps.source.toString().replace('\\', '\\\\')}"))""".toString()))

        } catch (ClassNotFoundException e) {
          throw new Exception('Could not find Clojure. Is it included on the class path?', e)
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader)
        }
    }

    @Override
    String commentStart() {
        return '"'
    }

    @Override
    String commentEnd() {
        return '"'
    }

}
