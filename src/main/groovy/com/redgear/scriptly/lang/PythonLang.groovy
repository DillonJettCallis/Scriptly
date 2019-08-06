package com.redgear.scriptly.lang

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
