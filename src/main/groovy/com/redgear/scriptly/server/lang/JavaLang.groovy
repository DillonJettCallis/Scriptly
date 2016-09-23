package com.redgear.scriptly.server.lang

import com.google.common.io.Files
import com.innowhere.relproxy.jproxy.JProxyShell
import com.redgear.scriptly.server.repo.Repository

/**
 * Created by LordBlackHole on 9/2/2016.
 */
class JavaLang implements Language {



    @Override
    Closure exec(File source, Repository repo) {

        def deps = parse(source, repo)


        def dir = deps.source.parentFile

        deps.deps.each {
            Files.copy(it, new File(dir, it.name))
        }

        return {List<String> args -> run(deps.source, args) }
    }

    void run(File source, List<String> args) {
        String [] input = [source.absolutePath] + args

        try {
            JProxyShell.main(input)
        } finally {
            source.parentFile.deleteDir()
        }
    }



}
