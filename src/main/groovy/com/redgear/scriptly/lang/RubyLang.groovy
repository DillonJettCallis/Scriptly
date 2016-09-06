package com.redgear.scriptly.lang
/**
 * Created by LordBlackHole on 9/4/2016.
 */
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
