package com.redgear.scriptly.task

import com.redgear.scriptly.config.Config
import com.redgear.scriptly.config.Options
import com.redgear.scriptly.lang.GenericLang
import com.redgear.scriptly.repo.impl.AetherRepo
import groovy.transform.CompileStatic

@CompileStatic
class ExecuteTask implements Task {
  @Override
  void exec(Config config, Options options) {
    def lang = GenericLang.lookup(options.language)
    def file = new File(options.source)

    lang.exec(file, new AetherRepo(config), options.args as String[])
  }
}
