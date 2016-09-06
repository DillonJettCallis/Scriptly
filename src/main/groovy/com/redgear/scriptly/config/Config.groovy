package com.redgear.scriptly.config

import java.nio.file.Paths

class Config {

    File localCache = Paths.get(System.getProperty('user.home'), '/.m2/repository').toFile()

    List<Repo> repos = [
            new Repo(name: 'central', uri: URI.create('https://repo1.maven.org/maven2/')),
            new Repo(name: 'jcenter', uri: URI.create('http://jcenter.bintray.com/')),
            new Repo(name: 'clojars', uri: URI.create('http://clojars.org/repo/'))
    ]

}

class Repo {

    String name

    URI uri

}