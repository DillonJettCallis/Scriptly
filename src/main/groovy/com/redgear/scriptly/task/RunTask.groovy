package com.redgear.scriptly.task

import com.redgear.scriptly.config.Config
import com.redgear.scriptly.config.Options
import com.redgear.scriptly.lang.GenericLang
import groovy.transform.CompileStatic

@CompileStatic
class RunTask implements Task {
  @Override
  void exec(Config config, Options options) {
    def src = new File(options.source).text
    def deps = new File(options.deps).listFiles().findAll { it.isFile() && it.name.endsWith('.jar') }.toSet()
    def lang = GenericLang.lookup(options.language)

    lang.run(src, deps, options.args as String[])
  }
}
