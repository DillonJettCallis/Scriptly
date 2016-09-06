package com.redgear.scriptly.lang

import com.redgear.scriptly.repo.Repository
import org.apache.commons.lang3.StringUtils

import static org.apache.commons.lang3.StringUtils.isBlank

/**
 * Created by LordBlackHole on 9/2/2016.
 */
trait Language {

    abstract void exec(File source, Repository repo, List<String> args)

    String commentStart() {
        return '/*'
    }

    String commentEnd() {
        return '*/'
    }

    DepInfo parse(File source, Repository repo) {
        def dir = File.createTempDir('scriptly', '')
        def copied = new File(dir, source.name)

        def start = commentStart()
        def startIndex = start.length()

        def end = commentEnd()
        def endIndex = end.length()

        def result = new DepInfo()

        result.source = copied


        source.withReader { reader ->
            copied.withWriter { writer ->

                def nextLine = reader.readLine()

                while(isBlank(nextLine)) {
                    nextLine = reader.readLine()
                }

                nextLine = skipSheBang(nextLine, reader, '#!', '!#')
                nextLine = skipSheBang(nextLine, reader, '::#!', '::!#')


                if(nextLine.trim().startsWith(start)) {
                    def builder = new StringBuilder()

                    nextLine = nextLine.substring(nextLine.indexOf(start) + startIndex)

                    while (!nextLine.contains(end)) {
                        builder.append(nextLine)
                        builder.append('\n')
                        nextLine = reader.readLine()
                    }

                    def index = nextLine.indexOf(end)
                    builder.append(nextLine.substring(0, index))

                    result.deps.addAll(downloadDeps(builder.toString(), repo))

                    nextLine = nextLine.substring(index + endIndex)
                }

                writer << nextLine
                writer << reader
            }

        }

        return result
    }

    String skipSheBang(String nextLine, Reader reader, String start, String end) {
        if(nextLine.trim().startsWith(start)) {

            while(!nextLine.contains(end)) {
                nextLine = reader.readLine()
            }

            nextLine = nextLine.substring(nextLine.indexOf(end) + end.length())

            while(isBlank(nextLine)) {
                nextLine = reader.readLine()
            }
        }

        return nextLine
    }


    List<File> downloadDeps(String deps, Repository repo) {

        def separate = deps.trim().split(/\s+/)

        return separate.collect { mod ->

            def split = mod.trim().split(':')

            def data = repo.resolvePackage(split[0], split[1], split[2])

            data.dependencies + data.main
        }.flatten()
    }

    static class DepInfo {

        File source

        Set<File> deps = []

    }

}