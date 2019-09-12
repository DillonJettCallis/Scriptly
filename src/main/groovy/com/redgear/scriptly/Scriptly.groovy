package com.redgear.scriptly

import com.fasterxml.jackson.databind.ObjectMapper
import com.redgear.scriptly.config.Config
import com.redgear.scriptly.config.Options
import com.redgear.scriptly.task.ExecuteTask
import com.redgear.scriptly.task.InstallTask
import com.redgear.scriptly.task.RunTask
import groovy.transform.CompileStatic
import net.sourceforge.argparse4j.ArgumentParsers

@CompileStatic
class Scriptly {

  /** Used with ArgParse as key. **/
  private static final String taskKey = 'task'

  static void main(String[] args) {

    Config config = loadConfig()

    def options = parseOptions(args)

    options.task.exec(config, options)
  }

  static File baseDir() {
    def home = System.getProperty("SCRIPTLY_HOME")
    if (home) {
      return new File(home)
    } else {
      return new File(System.getProperty('user.home') + '/.scriptly')
    }
  }

  private static Config loadConfig() {

    def configFile = new File(baseDir(), 'config.json')

    if (configFile.exists()) {
      return new ObjectMapper().readValue(configFile, Config.class)
    } else {
      configFile.parentFile.mkdirs()
      Config config = new Config()
      new ObjectMapper().writeValue(configFile, config)
      return config
    }
  }

  private static Options parseOptions(String[] args) {
    def parser = ArgumentParsers.newFor('scriptly').build()

    def commandParsers = parser.addSubparsers().metavar('command')

    def execTask = commandParsers.addParser('exec').setDefault(taskKey, new ExecuteTask()).help('execute a script')
    execTask.addArgument('language').required(true).help('language name')
    execTask.addArgument('source').required(true).help('source file')
    execTask.addArgument('args').nargs('*').help('script arguments')

    def runTask = commandParsers.addParser('run').setDefault(taskKey, new RunTask()).help('run a script with dependencies')
    runTask.addArgument('language').required(true).help('language name')
    runTask.addArgument('source').required(true).help('source file')
    runTask.addArgument('deps').required(true).help('directory containing all dependencies (must be in .jar files)')
    runTask.addArgument('args').nargs('*').help('script arguments')

    def installTask = commandParsers.addParser('install').setDefault(taskKey, new InstallTask()).help("install a script and it's dependencies")
    installTask.addArgument('language').required(true).help('language name')
    installTask.addArgument('source').required(true).help('source file')

    return parser.parseArgsOrFail(args).attrs as Options
  }
}
