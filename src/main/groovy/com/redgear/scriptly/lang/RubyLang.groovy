package com.redgear.scriptly.lang

import groovy.transform.CompileStatic

@CompileStatic
class RubyLang extends GenericLang {

  RubyLang(String lang) {
    super(lang)
  }

  String commentStart() {
    return '=begin'
  }

  String commentEnd() {
    return '=end'
  }
}
