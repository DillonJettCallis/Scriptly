package com.redgear.scriptly.lang

import groovy.transform.CompileStatic

@CompileStatic
class LuaLang extends GenericLang {

  LuaLang(String lang) {
    super(lang)
  }

  String commentStart() {
    return "--[["
  }

  String commentEnd() {
    return "]]"
  }

}
