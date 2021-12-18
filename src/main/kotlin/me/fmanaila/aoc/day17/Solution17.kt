package me.fmanaila.aoc.day17

import kotlin.math.abs


fun main() {
    partOne(-162)
    partTwo()
}

fun partOne(miny: Int) {
    println(abs(miny) * (abs(miny) - 1) / 2)
}

fun partTwo() {
    val minx = 56
    val maxx = 76
    val miny = -162
    val maxy = -134

    var count = 0
    // brute force
    for(vx in 1..maxx+1) {
        for (vy in miny..abs(miny)+1) { // assumption miny is negative
            if(testVelocity(vx, vy, minx, maxx, miny, maxy)) {
                count++
            }
        }
    }

    println(count)
}

fun testVelocity(initVx: Int, initVy: Int, minx: Int, maxx: Int, miny: Int, maxy: Int): Boolean {
    // assumption target is to the right and down of origin
    var pos = 0 to 0
    var vx = initVx
    var vy = initVy
    do {
        if(isWithinBounds(pos, minx, maxx, miny, maxy)) {
            return true
        }
        pos = nextPos(pos, vx, vy)
        vx = nextVx(vx)
        vy = nextVy(vy)
    } while(canStillMakeIt(pos, vx, vy, minx, maxx, miny, maxy))
    return false
}

fun canStillMakeIt(pos: Pair<Int, Int>, vx: Int, vy: Int, minx: Int, maxx: Int, miny: Int, maxy: Int): Boolean {
    if(pos.first < minx && vx == 0) {
        // stalled horizontally before reaching minx
        return false
    }
    if(pos.first > maxx) {
        // overshot horizontally
        return false
    }
    if(pos.second < miny) {
        // overshot vertically
        return false
    }
    return true
}

fun nextVy(vy: Int): Int {
    return vy - 1
}

fun nextVx(vx: Int): Int {
    return if(vx == 0) 0 else vx-1
}

fun nextPos(pos: Pair<Int, Int>, vx: Int, vy: Int): Pair<Int, Int> {
    return pos.first + vx to pos.second + vy
}

fun isWithinBounds(pos: Pair<Int, Int>, minx: Int, maxx: Int, miny: Int, maxy: Int): Boolean {
    return pos.first >= minx && pos.second <= maxx && pos.second >= miny && pos.second <= maxy
}
