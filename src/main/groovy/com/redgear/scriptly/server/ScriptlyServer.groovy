package com.redgear.scriptly.server

import com.fasterxml.jackson.databind.ObjectMapper
import com.redgear.scriptly.common.RequestMessage
import com.redgear.scriptly.server.config.Config
import com.redgear.scriptly.server.lang.ClojureLang
import com.redgear.scriptly.server.lang.GenericLang
import com.redgear.scriptly.server.lang.JavaLang
import com.redgear.scriptly.server.lang.Language
import com.redgear.scriptly.server.lang.PythonLang
import com.redgear.scriptly.server.lang.RubyLang
import com.redgear.scriptly.server.lang.ScalaLang
import com.redgear.scriptly.server.repo.Repository
import com.redgear.scriptly.server.repo.impl.AetherRepo
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.codec.digest.Sha2Crypt
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Created by ft4 on 9/23/2016.
 */
@SpringBootApplication
@RestController
class ScriptlyServer {

    private static final Logger log = LoggerFactory.getLogger(ScriptlyServer.class)

    @Autowired
    private Repository repo

    private final Map<String, Closure> cachedScripts = [:]

    public static void main(String[] args) {
        SpringApplication.run(ScriptlyServer.class)
    }

    @RequestMapping('/')
    void runScript(@RequestBody RequestMessage request, OutputStream out) {
        log.info('Received Request')

        def lang = request.lang
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
        } else {
            language = new GenericLang(lang)
        }


        def file = new File(request.source)



        def sha = Base64.encoder.encodeToString(DigestUtils.sha256(file.newInputStream()))

        def exec
        if(cachedScripts.containsKey(sha)) {
            exec = cachedScripts.get(sha)
        } else {
            exec = language.exec(file, repo)
            cachedScripts.put(sha, exec)
        }

        synchronized (log) {

            def outBackup = System.out
            def errBackup = System.err

            def printer = new PrintStream(out)

            System.setOut(printer)
            System.setErr(printer)

            exec(request.args)

            System.setOut(outBackup)
            System.setErr(errBackup)
        }
    }

    @Bean
    Config loadConfig() {

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
