package com.redgear.scriptly

import com.fasterxml.jackson.databind.ObjectMapper
import com.redgear.scriptly.config.Config
import com.redgear.scriptly.config.Options
import com.redgear.scriptly.task.ExecuteTask
import com.redgear.scriptly.task.InstallTask
import com.redgear.scriptly.task.RunTask
import com.redgear.scriptly.task.UninstallTask
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
class Scriptly {

  private static final Logger log = LoggerFactory.getLogger(Scriptly)

  static void main(String[] args) {
    try {
      Config config = loadConfig()

      def options = parseOptions(args)

      options.task.exec(config, options)
    } catch (RuntimeException e) {
      log.error("Unexpected exception: ", e)
      System.exit(1)
    }
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
    if (args.length === 0) {
      stop()
    }

    if (args[0] in helpFlags) {
      stop(helpAllMessage, false)
    }

    switch (args[0]) {
      case 'exec': {
        if (args.length < 3) {
          stop(helpExecMessage)
        }

        if (args[1] in helpFlags) {
          stop(helpExecMessage, false)
        }

        return new Options(task: new ExecuteTask(), language: args[1], source: args[2], args: args[3..args.length - 1])
      }
      case 'run': {
        if (args.length < 4) {
          stop(helpRunMessage)
        }

        if (args[1] in helpFlags) {
          stop(helpRunMessage, false)
        }

        return new Options(task: new RunTask(), language: args[1], source: args[2], deps: args[3], args: args[4..args.length - 1])
      }
      case 'install': {
        if (args.length != 3) {
          stop(helpInstallMessage)
        }

        if (args[1] in helpFlags) {
          stop(helpInstallMessage, false)
        }

        return new Options(task: new InstallTask(), language: args[1], source: args[2])
      }
      case 'uninstall': {
        if (args.length != 2) {
          stop(helpUninstallMessage)
        }

        if (args[1] in helpFlags) {
          stop(helpUninstallMessage, false)
        }

        return new Options(task: new UninstallTask(), source: args[1])
      }
      default:
        stop()
    }
  }

  private static Options stop(String message = helpAllMessage, boolean fail = true) {
    println(message)
    System.exit(fail ? 1 : 0)
    throw new IllegalArgumentException()
  }

  private static final Set<String> helpFlags = ['-h', '--help'].toSet()

  private static final String helpAllMessage = '''usage: scriptly [-h] command ...

positional arguments:
  command
    exec                 execute a script
    run                  run a script with dependencies
    install              install a script and it's dependencies
    uninstall            delete a script that has been installed

named arguments:
  -h, --help             show this help message and exit
'''

  private static final String helpExecMessage = '''usage: scriptly exec [-h] language source [args ...]

positional arguments:
  language               language name
  source                 source file
  args                   script arguments

named arguments:
  -h, --help             show this help message and exit
'''

  private static final String helpRunMessage = '''usage: scriptly run [-h] language source deps [args [args ...]]

positional arguments:
  language               language name
  source                 source file
  deps                   directory containing all dependencies  (must be in
                         .jar files)
  args                   script arguments

named arguments:
  -h, --help             show this help message and exit
'''

  private static final String helpInstallMessage = '''usage: scriptly install [-h] language source

positional arguments:
  language               language name
  source                 source file

named arguments:
  -h, --help             show this help message and exit
'''

  private static final String helpUninstallMessage = '''usage: scriptly uninstall [-h] script

positional arguments:
  script                 script to remove

named arguments:
  -h, --help             show this help message and exit
'''
}
