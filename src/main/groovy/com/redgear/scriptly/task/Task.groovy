package com.redgear.scriptly.task

import com.redgear.scriptly.config.Config
import com.redgear.scriptly.config.Options

interface Task {

  void exec(Config config, Options options)

}
