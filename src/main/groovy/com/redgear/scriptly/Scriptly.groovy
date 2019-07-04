package com.redgear.scriptly

import com.fasterxml.jackson.databind.ObjectMapper
import com.redgear.scriptly.config.Config
import com.redgear.scriptly.lang.*
import com.redgear.scriptly.repo.Repository
import com.redgear.scriptly.repo.impl.AetherRepo

/**
 * Created by LordBlackHole on 9/2/2016.
 */
class Scriptly {


    public static void main(String[] args) {

        Config config = loadConfig()

        Repository repo = new AetherRepo(config)

        if (args.length < 2) {
            throw new Exception("Expecting at least two arguments, language and source file")
        }

        def lang = args[0]
        def source = args[1]

        Language language

        if (lang == 'java') {
            language = new JavaLang()
        } else if (lang == 'scala') {
            language = new ScalaLang()
        } else if (lang == 'clojure') {
            language = new ClojureLang()
        } else if (lang == 'ruby' || lang == 'jruby') {
            language = new RubyLang(lang)
        } else if (lang == 'python' || lang == 'jython') {
            language = new PythonLang(lang)
        } else if (lang == 'kotlin') {
            language = new KotlinLang()
        } else {
            language = new GenericLang(lang)
        }



        def file = new File(source)

        language.exec(file, repo, args.toList().subList(2, args.length))

    }

    private static Config loadConfig() {

        def configFile = new File(System.getProperty('user.home') + '/.scriptly/config.json')

        if(configFile.exists()) {
            return new ObjectMapper().readValue(configFile, Config.class)
        } else {
            configFile.parentFile.mkdirs()
            Config config = new Config()
            new ObjectMapper().writeValue(configFile, config)
            return config
        }
    }


}
