package com.redgear.scriptly.lang

import groovy.transform.CompileStatic

@CompileStatic
class KotlinLang extends GenericLang {

  KotlinLang() {
    super("kotlin")
  }

  @Override
  ClassLoader buildClassLoader(Set<File> deps) {
    def newPath = deps.collect { it.toString() }.join(File.pathSeparator)

    System.setProperty("kotlin.script.classpath", newPath)

    return super.buildClassLoader(deps)
  }

}
