package com.redgear.scriptly.config

import com.redgear.scriptly.task.Task

class Options {

  // for exec, install and run
  String language
  String source

  // for exec and run
  List<String> args

  // for run
  String deps

  Task task
}
