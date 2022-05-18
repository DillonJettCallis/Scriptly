package com.redgear.scriptly.lang

import groovy.transform.CompileStatic

@CompileStatic
class RenjinLang extends GenericLang {

  RenjinLang(String lang) {
    super(lang)
  }

  String commentStart() {
    return '"'
  }

  String commentEnd() {
    return '"'
  }

}
