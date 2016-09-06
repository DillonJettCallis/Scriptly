package com.redgear.scriptly.lang

/**
 * Created by LordBlackHole on 9/4/2016.
 */
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
