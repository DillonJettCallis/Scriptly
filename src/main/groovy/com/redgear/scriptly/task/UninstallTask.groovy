package com.redgear.scriptly.task

import com.redgear.scriptly.Scriptly
import com.redgear.scriptly.config.Config
import com.redgear.scriptly.config.Options
import groovy.transform.CompileStatic

@CompileStatic
class UninstallTask implements Task {
  @Override
  void exec(Config config, Options options) {
    def baseName = options.source

    def baseDir = Scriptly.baseDir()

    def binDir = new File(baseDir, 'bin')
    def batFile = new File(binDir, "${baseName}.bat")
    def bashFile = new File(binDir, baseName)
    def libDir = new File(baseDir, "lib/$baseName")

    batFile.delete()
    bashFile.delete()
    libDir.deleteDir()
  }
}
