package me.fmanaila.aoc.day22

import me.fmanaila.aoc.inputLines
import kotlin.math.max
import kotlin.math.min

fun main() {
    check(partOne("examples/day22.txt") == 590784L)
    println("Part One: ${partOne("input/day22.txt")}") // 642125

    check(partTwo("examples/day22-2.txt") == 2758514936282235L)
    println("Part Two: ${partTwo("input/day22.txt")}") // 1235164413198198
}

private fun partOne(inputFile: String): Long {
    println("------------ Part ONE $inputFile -------------")
    val operations = readOperations(inputFile).filter(::isInInitializationRegion)
    operations.forEach { println(it) }
    return executeOperations(operations)
}

private fun partTwo(inputFile: String): Long {
    println("------------ Part TWO $inputFile -------------")
    val operations = readOperations(inputFile)
    operations.forEach { println(it) }
    return executeOperations(operations)
}

private fun executeOperations(operations: List<Operation>): Long {
    return operations.fold(Space()) { space, operation ->
        operation.apply(space)
    }.size()
}

private fun isInInitializationRegion(op: Operation): Boolean {
    val cuboid = op.cuboid
    return cuboid.minx >= -50 && cuboid.maxx <= 50 &&
            cuboid.miny >= -50 && cuboid.maxy <= 50 &&
            cuboid.minz >= -50 && cuboid.maxz <= 50
}

private fun readOperations(inputFile: String) = inputLines(inputFile).asSequence().map { line ->
    val splitLine = line.split(" ")
    val op = splitLine[0]
    val dimensions = StringBuilder(splitLine[1])
    val xPair = parsePair(dimensions.substring(dimensions.indexOf("=") + 1, dimensions.indexOf(",")))
    dimensions.delete(0, dimensions.indexOf(",") + 1)
    val yPair = parsePair(dimensions.substring(dimensions.indexOf("=") + 1, dimensions.indexOf(",")))
    dimensions.delete(0, dimensions.indexOf(",") + 1)
    val zPair = parsePair(dimensions.substring(dimensions.indexOf("=") + 1))
    val cuboid = Cuboid(
        xPair.first, xPair.second,
        yPair.first, yPair.second,
        zPair.first, zPair.second
    )
    if (op == "on") TurnOn(cuboid) else TurnOff(cuboid)
}.toList()

// substring = 123..456
private fun parsePair(substring: String): Pair<Int, Int> {
    val listOfInts = substring.split("..").map { it.toInt() }.toList()
    return listOfInts[0] to listOfInts[1]
}

private class Cuboid(val minx: Int, val maxx: Int, val miny: Int, val maxy: Int, val minz: Int, val maxz: Int) {
    /**
     * If [other] overlaps with this cuboid, will return a collection of cuboids (subset of this) that
     * do not intersect with [other] cuboid.
     */
    fun doesNotOverlapWith(other: Cuboid): Collection<Cuboid> {
        if (minx > other.maxx || maxx < other.minx ||
            miny > other.maxy || maxy < other.miny ||
            minz > other.maxz || maxz < other.minz) {
            // does not overlap at all
            return listOf(this)
        }
        if (minx >= other.minx && maxx <= other.maxx &&
            miny >= other.miny && maxy <= other.maxy &&
            minz >= other.minz && maxz <= other.maxz) {
            // this is fully within other
            return emptyList()
        }
        val pieces = mutableListOf<Cuboid>()
        // slice top
        if (maxy > other.maxy) {
            pieces.add(Cuboid(minx, maxx, other.maxy + 1, maxy, minz, maxz))
        }
        // slice bottom
        if (miny < other.miny) {
            pieces.add(Cuboid(minx, maxx, miny, other.miny - 1, minz, maxz))
        }
        // slice front
        if (maxz > other.maxz) {
            pieces.add(Cuboid(minx, maxx,
                    miny = max(miny, other.miny),
                    maxy = min(maxy, other.maxy),
                    other.maxz + 1,
                    maxz)
            )
        }
        // slice back
        if (minz < other.minz) {
            pieces.add(Cuboid(minx, maxx,
                miny = max(miny, other.miny),
                maxy = min(maxy, other.maxy),
                minz,
                other.minz - 1)
            )
        }
        // slice right
        if (maxx > other.maxx) {
            pieces.add(Cuboid(
                minx = other.maxx + 1,
                maxx = maxx,
                miny = max(miny, other.miny),
                maxy = min(maxy, other.maxy),
                minz = max(minz, other.minz),
                maxz = min(maxz, other.maxz)
            ))
        }
        // slice left
        if (minx < other.minx) {
            pieces.add(Cuboid(
                minx = minx,
                maxx = other.minx - 1,
                miny = max(miny, other.miny),
                maxy = min(maxy, other.maxy),
                minz = max(minz, other.minz),
                maxz = min(maxz, other.maxz)
            ))
        }
        return pieces
    }

    fun size(): Long {
        return 1L * (maxx - minx + 1) * (maxy - miny + 1) * (maxz - minz + 1)
    }

    override fun toString(): String {
        return "Cuboid(x=$minx..$maxx, y=$miny..$maxy, z=$minz..$maxz)"
    }
}

private class Space(val cuboids: List<Cuboid> = emptyList()) {
    fun turnOn(cuboid: Cuboid): Space {
        val newCuboids = cuboids.fold(listOf(cuboid)) { cuboidsToAdd, existingCuboid ->
            // cuboidsToAdd will never intersect
            cuboidsToAdd.flatMap { it.doesNotOverlapWith(existingCuboid) }
        }
        return Space(cuboids + newCuboids)
    }

    fun turnOff(cuboid: Cuboid): Space {
        val remainingCuboids = cuboids.flatMap { existingCuboid ->
            existingCuboid.doesNotOverlapWith(cuboid)
        }
        return Space(remainingCuboids)
    }

    fun size(): Long {
        return cuboids.sumOf { it.size() }
    }
}

private abstract class Operation(val cuboid: Cuboid) {
    abstract fun apply(space: Space): Space
}

private class TurnOn(cuboid: Cuboid) : Operation(cuboid) {
    override fun apply(space: Space): Space {
        val newSpace = space.turnOn(cuboid)
        println("Turned on ${newSpace.size() - space.size()} cubes")
        return newSpace
    }

    override fun toString(): String {
        return "On($cuboid)"
    }
}

private class TurnOff(cuboid: Cuboid) : Operation(cuboid) {
    override fun apply(space: Space): Space {
        val newSpace = space.turnOff(cuboid)
        println("Turned off ${space.size() - newSpace.size()} cubes")
        return newSpace
    }

    override fun toString(): String {
        return "Off($cuboid)"
    }
}