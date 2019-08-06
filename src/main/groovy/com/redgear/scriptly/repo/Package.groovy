package com.redgear.scriptly.repo

interface Package {

  File getMain()

  List<File> getDependencies()

}