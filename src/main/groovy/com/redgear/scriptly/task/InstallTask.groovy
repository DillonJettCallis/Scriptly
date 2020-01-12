package com.redgear.scriptly.task

import com.redgear.scriptly.Scriptly
import com.redgear.scriptly.config.Config
import com.redgear.scriptly.config.Options
import com.redgear.scriptly.lang.GenericLang
import com.redgear.scriptly.repo.impl.AetherRepo
import groovy.transform.CompileStatic

@CompileStatic
class InstallTask implements Task {

  @Override
  void exec(Config config, Options options) {
    def srcFile = new File(options.source)

    def baseName = srcFile.name
    def lastDot = baseName.lastIndexOf('.')
    if (lastDot != -1) {
      baseName = baseName.substring(0, lastDot)
    }

    def baseDir = Scriptly.baseDir()

    def binDir = new File(baseDir, 'bin')
    def libDir = new File(baseDir, "lib/$baseName")

    binDir.mkdirs()
    if (libDir.exists()) {
      libDir.deleteDir()
    }
    libDir.mkdirs()

    def lang = GenericLang.lookup(options.language)
    def info = lang.parse(srcFile, new AetherRepo(config))

    info.deps.forEach { File src ->
      src.withInputStream { input ->
        new File(libDir, src.name).withOutputStream { output -> {
          output << input
        }}
      }
    }

    def destFile = new File(libDir, srcFile.name)
    destFile << info.source

    def batFile = new File(binDir, "${baseName}.bat")
    def bashFile = new File(binDir, baseName)
    batFile.delete()
    bashFile.delete()

    batFile << """@echo off\nscriptly run ${lang.name} $destFile $libDir %*"""

    bashFile << """exec scriptly run ${lang.name} $destFile $libDir \$@"""
    bashFile.setExecutable(true, true)
  }
}
