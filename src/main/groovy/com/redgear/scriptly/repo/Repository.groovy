package com.redgear.scriptly.repo

interface Repository {

  List<File> resolvePackages(List<Coordinate> deps)

}

class Coordinate {
  String group
  String artifact
  String version
}
