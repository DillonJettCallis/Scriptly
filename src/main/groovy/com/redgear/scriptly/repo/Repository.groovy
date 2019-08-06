package com.redgear.scriptly.repo

interface Repository {

  Package resolvePackage(String group, String artifact, String version)

}
