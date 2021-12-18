package me.fmanaila.aoc.day5

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class Point(val x: Int, val y: Int) {
    override fun toString(): String = "($x, $y)"
}

class Line(val p1: Point, val p2: Point) {

    fun covers(p: Point): Boolean {
        if(this.isHorizontal() && p1.x == p.x) {
            //   ----- p1 ------ p -------- p2 -----
            //  or
            //   ----- p2 ------ p -------- p1 -----
            return (p1.y <= p.y && p.y <= p2.y) || (p2.y <= p.y && p.y <= p1.y)
        } else if(this.isVertical() && p1.y == p.y) {
            // line is vertical
            return (p1.x <= p.x && p.x <= p2.x) || (p2.x <= p.x && p.x <= p1.x)
        } else if(this.isDiagonal() && abs(p1.x - p.x) == abs(p1.y - p.y) && abs(p2.x - p.x) == abs(p2.y - p.y)) {
            return min(p1.x, p2.x) <= p.x && p.x <= max(p1.x, p2.x) &&
                    min(p1.y, p2.y) <= p.y && p.y <= max(p1.y, p2.y)
        }
        return false
    }

    fun isHorizontal(): Boolean = p1.x == p2.x

    fun isVertical(): Boolean = p1.y == p2.y

    fun isDiagonal(): Boolean = abs(p1.x - p2.x) == abs(p1.y - p2.y)

    override fun toString(): String = "$p1 -> $p2"
}