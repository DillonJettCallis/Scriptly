package com.redgear.scriptly.lang

import groovy.transform.CompileStatic

@CompileStatic
class PythonLang extends GenericLang {

  PythonLang(String lang) {
    super(lang)
  }

  String commentStart() {
    return "'''"
  }

  String commentEnd() {
    return "'''"
  }

}
