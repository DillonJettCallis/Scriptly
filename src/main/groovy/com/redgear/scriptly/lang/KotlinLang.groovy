package com.redgear.scriptly.lang

import groovy.transform.CompileStatic

@CompileStatic
class KotlinLang extends GenericLang {

  KotlinLang() {
    super("kotlin")
  }

  ClassLoader buildClassLoader(Language.DepInfo deps) {
    def newPath = deps.deps.collect { it.toString() }.join(File.pathSeparator)

    System.setProperty("kotlin.script.classpath", newPath)

    return super.buildClassLoader(deps)
  }

}
