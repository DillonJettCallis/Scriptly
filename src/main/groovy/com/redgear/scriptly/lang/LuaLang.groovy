package com.redgear.scriptly.lang

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
