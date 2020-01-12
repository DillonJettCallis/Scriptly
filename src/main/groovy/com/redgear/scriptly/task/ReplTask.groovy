package com.redgear.scriptly.task

import com.redgear.scriptly.config.Config
import com.redgear.scriptly.config.Options
import com.redgear.scriptly.lang.GenericLang
import com.redgear.scriptly.repo.impl.AetherRepo
import groovy.transform.CompileStatic

@CompileStatic
class ReplTask implements Task {
  @Override
  void exec(Config config, Options options) {
    def repo = new AetherRepo(config)
    def lang = GenericLang.lookup(options.language)
    def deps = repo.resolvePackages(options.deps.split(';') as List<String>).toSet()

    def repl = lang.repl(deps)
    def scanner = new Scanner(System.in)
    def active = true

    while(active) {
      def next = scanner.nextLine().trim()

      if (next.startsWith(':')) {
        if (next == ':exit' || next == ':quit') {
          active = false
        } else if (next == ':help') {
          println("commands are :exit, :help")
        } else {
          println("Unknown command. Type :help for help or :exit to close")
        }
      } else {
        println(repl.nextLine(next))
      }
    }
  }
}
