package com.redgear.scriptly.repo

interface Repository {

  List<File> resolvePackages(List<String> deps)

}

class Coordinate {
  String group
  String artifact
  String version
}
