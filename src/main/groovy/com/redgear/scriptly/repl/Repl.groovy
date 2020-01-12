package com.redgear.scriptly.repl

import javax.script.ScriptEngine

interface Repl {

  Object nextLine(String raw)

}

class GenericRepl implements Repl {

  private final ScriptEngine engine

  GenericRepl(ScriptEngine engine) {
    this.engine = engine
  }

  @Override
  Object nextLine(String raw) {
    return engine.eval(raw)
  }
}
