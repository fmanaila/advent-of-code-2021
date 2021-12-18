package me.fmanaila.aoc.day12

class World() {
    val cavesMap = mutableMapOf<String, Cave>()

    fun addLink(cave1: String, cave2:String) {
        addCave(cave1)
        addCave(cave2)
        cavesMap[cave1]!!.links.add(cavesMap[cave2]!!)
        cavesMap[cave2]!!.links.add(cavesMap[cave1]!!)
    }

    fun addCave(name: String) {
        cavesMap.putIfAbsent(name, Cave(name))
    }

    operator fun get(name: String): Cave? {
        return cavesMap[name]
    }
}