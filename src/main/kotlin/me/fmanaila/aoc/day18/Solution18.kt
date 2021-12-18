package me.fmanaila.aoc.day18

import me.fmanaila.aoc.inputLines
import java.lang.IllegalArgumentException
import java.lang.UnsupportedOperationException
import kotlin.math.floor
import kotlin.math.max

fun main() {
    partOne("input/day18.txt")
    partTwo("input/day18.txt")
}

fun partOne(inputFile: String) {
    val mag = inputLines(inputFile).asSequence().map { line ->
        parseSnailFishNumber(line.toMutableList())
    }.reduce { left, right ->
        left + right
    }.magnitude()

    println(mag)
}

fun partTwo(inputFile: String) {
    // brute force, only 9900 arrangements for 100 inputs :)
    val numbers = inputLines(inputFile).asSequence().map { line ->
        parseSnailFishNumber(line.toMutableList())
    }.toList()

    var maxMag = 0L
    for(i in numbers.indices) {
        for(j in numbers.indices) {
            if(i != j) {
                maxMag = max(maxMag, (numbers[i] + numbers[j]).magnitude())
            }
        }
    }

    println(maxMag)
}

fun parseSnailFishNumber(line: MutableList<Char>): SnailfishNumber {
    val c = line.removeFirst()
    if (c != '[') {
        throw IllegalArgumentException("Invalid input for snailfish number ${line.joinToString("")}")
    }
    val first = parseElement(line)
    line.removeFirst() // ,
    val second = parseElement(line)
    line.removeFirst() // ]
    return SnailfishNumber(first, second)
}

fun parseElement(line: MutableList<Char>): Element {
    val c = line.removeFirst()
    if (c == '[') {
        val first = parseElement(line)
        line.removeFirst() // ,
        val second = parseElement(line)
        line.removeFirst() // ]
        return PairElement(first, second)
    }
    // c is number
    return ValueElement(c.digitToInt())
}

class SnailfishNumber(left: Element, right: Element) : PairElement(left, right) {
    fun reduce() : SnailfishNumber {
        var reduced = this
        var reducedMore = reduced.reduceOnce()
        while(reduced != reducedMore) {
            reduced = reducedMore
            reducedMore = reducedMore.reduceOnce()
        }
        return reducedMore
    }

    private fun reduceOnce() : SnailfishNumber {
        val exploded = this.explode(0)
        if(exploded != null) {
            val (_, replacement, _) = exploded
            if (replacement is PairElement) {
                return SnailfishNumber(replacement.left, replacement.right)
            }
            throw UnsupportedOperationException("Explosion resulted in root not being a pair element $replacement")
        }
        val split = this.split()
        if(split != null) {
            if(split is PairElement) {
                return SnailfishNumber(split.left, split.right)
            }
            throw UnsupportedOperationException("Split resulted in root not being a pair element $split")
        }
        return this
    }

    operator fun plus(other: SnailfishNumber) : SnailfishNumber {
        return SnailfishNumber(
            PairElement(left, right),
            PairElement(other.left, other.right)
        ).reduce()
    }
}


sealed class Element {
    abstract fun magnitude(): Long

    abstract fun addLeft(value: Int) : Element

    abstract fun addRight(value: Int) : Element

    abstract fun split() : Element?
}

class ValueElement(val value: Int) : Element() {

    override fun magnitude(): Long {
        return value.toLong()
    }

    override fun addLeft(value: Int) = add(value)

    override fun addRight(value: Int) = add(value)

    override fun split(): Element? {
        if(value < 10) {
            return null
        }
        val half = value / 2
        return if(value % 2 == 0) {
            PairElement(ValueElement(half), ValueElement(half))
        } else {
            PairElement(ValueElement(half), ValueElement(half + 1))
        }
    }

    fun add(value: Int) = ValueElement(this.value + value)

    override fun toString(): String {
        return "$value"
    }
}

open class PairElement(val left: Element, val right: Element) : Element() {

    override fun addLeft(value: Int) : Element {
        return PairElement(left.addLeft(value), right)
    }

    override fun addRight(value: Int) : Element {
        return PairElement(left, right.addRight(value))
    }

    override fun split() : Element? {
        val newLeft = left.split()
        if(newLeft != null) {
            // left has split
            return PairElement(newLeft, right)
        }
        val newRight = right.split()
        if(newRight != null) {
            // right has split
            return PairElement(left, newRight)
        }
        return null
    }

    fun explode(depth: Int) : Triple<Int?, Element, Int?>? {
        if(depth == 3) {
            if(left is PairElement) {
                // left must explode
                if(left.left !is ValueElement || left.right !is ValueElement) {
                    throw UnsupportedOperationException("Must explode node, but it's not made of values. $left")
                }
                val leftAdd = left.left.value
                val rightAdd = left.right.value
                return Triple(leftAdd, PairElement(ValueElement(0), right.addLeft(rightAdd)), null)
            }
            if(right is PairElement) {
                // right must explode
                if(right.left !is ValueElement || right.right !is ValueElement) {
                    throw UnsupportedOperationException("Must explode node, but it's not made of values. $right")
                }
                val leftAdd = right.left.value
                val rightAdd = right.right.value
                return Triple(null, PairElement(left.addRight(leftAdd), ValueElement(0)), rightAdd)
            }
            return null
        }

        if(left is PairElement) {
            val exploded = left.explode(depth + 1)
            if(exploded != null) {
                val (leftAdd, replacement, rightAdd) = exploded
                val newRight = rightAdd?.let(right::addLeft) ?: right
                return Triple(leftAdd, PairElement(replacement, newRight), null)
            }
        }

        if(right is PairElement) {
            val exploded = right.explode(depth + 1)
            if(exploded != null) {
                val (leftAdd, replacement, rightAdd) = exploded
                val newLeft = leftAdd?.let(left::addRight) ?: left
                return Triple(null, PairElement(newLeft, replacement), rightAdd)
            }
        }

        return null
    }

    override fun magnitude(): Long {
        return 3L * left.magnitude() + 2L * right.magnitude()
    }

    override fun toString(): String {
        return "[$left,$right]"
    }
}