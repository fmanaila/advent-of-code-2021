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
    if(cave.isEnd()) {
        println(currentPath)
        return 1
    }
    if(!canExplore(cave, world, path)) {
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

fun canExplore(cave: Cave, world: World, path: List<Cave>) : Boolean {
    if(cave.isStart() || cave.isEnd()) {
        return cave.visits == 0
    }
    if(cave.isSmall()) {
        // a small cave on the path has been visited more than once
        return if(path.filter(Cave::isSmall).count { it.visits > 1 } > 0) {
            cave.visits == 0
        } else {
            true
        }
    }
    return true
}