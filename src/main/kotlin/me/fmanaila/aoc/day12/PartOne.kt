package me.fmanaila.aoc.day12

import me.fmanaila.aoc.inputLines

fun main() {
    val world = World()
    inputLines("input/day12.txt").asSequence()
        .forEach { line ->
            val linkArr = line.trim().split("-")
            world.addLink(linkArr[0].trim(), linkArr[1].trim())
        }
    println(world.cavesMap.keys)
    print(explore(world["start"]!!, world, emptyList()))
}

private fun explore(cave: Cave, world: World, path: List<Cave>) : Int {
    val currentPath = path + cave
    if(cave.name.equals("end")) {
        println(currentPath)
        return 1
    }
    if(cave.isSmall() && cave.visits > 0) {
        return 0
    }
    cave.visits++
    var numberOfPaths = 0;
    for(next in cave.links) {
        numberOfPaths += explore(next, world, currentPath)
    }
    cave.visits--
    return numberOfPaths
}

