package me.fmanaila.aoc.day15

import me.fmanaila.aoc.inputLines
import java.util.*
import java.util.stream.IntStream
import kotlin.streams.asSequence
import kotlin.system.measureTimeMillis

fun main() {
    partOne("input/day15.txt")

    println(measureTimeMillis {
        partTwo("input/day15.txt")
    })
}

fun partOne(inputFile: String) {
    val matrix = readMatrix(inputFile)
    findPath(matrix)
}

fun partTwo(inputFile: String) {
    // 3045
    val matrix = readMatrix(inputFile)
    findPath(expandMatrix(matrix))
}

fun expandMatrix(matrix: Array<IntArray>): Array<IntArray> {
    return IntStream.rangeClosed(0, 4).asSequence().flatMap { lineInc ->
        matrix.map { line ->
            line + line.map(1::plus) + line.map(2::plus) + line.map(3::plus) + line.map(4::plus)
        }.map { line ->
            line.map(lineInc::plus).map { if(it > 9) it % 9 else it }.toIntArray()
        }
    }.toList().toTypedArray()
}



fun findPath(matrix: Array<IntArray>) {
    val graph = matrixToGraph(matrix)

    val maxi = matrix.size
    val maxj = matrix[0].size

    val source = graph[(0 to 0)]!!
    source.minFromSource = 0
    val q = PriorityQueue<Node>(compareBy(Node::minFromSource))
    q.add(source)

    while(q.isNotEmpty()) {
        val node = q.poll()!!
        println("At: $node")
        node.getNeighbors(maxi, maxj, graph)
            .forEach { neighbor ->
                println("Neighbor $neighbor. Route through current node: ${0L + node.minFromSource + neighbor.weight}")
                if(0L + node.minFromSource + neighbor.weight < neighbor.minFromSource) {
                    neighbor.minFromSource = node.minFromSource + neighbor.weight
                    q.add(neighbor) // add to q to be reevaluated
                }
            }
    }

    println(graph[(maxi - 1 to maxj - 1)])
}

fun matrixToGraph(matrix: Array<IntArray>): Map<Pair<Int, Int>, Node> {
    return matrix.flatMapIndexed { i, line ->
        line.mapIndexed { j, weight ->
            Node(i, j, weight)
        }
    }.associateBy { node ->
        (node.i to node.j)
    }
}

fun readMatrix(inputFile: String): Array<IntArray> {
    return inputLines(inputFile).asSequence()
        .map { line ->
            line.asSequence().map(Char::toString).map(String::toInt).toList().toIntArray()
        }
        .toList()
        .toTypedArray()
}

data class Node(val i: Int, val j: Int, val weight: Int, var minFromSource: Int = Int.MAX_VALUE) {

    fun getNeighbors(maxi: Int, maxj: Int, graph: Map<Pair<Int, Int>, Node>) : List<Node> {
        val neighbors = mutableListOf<Node>()
        if(i > 0) {
            // neighbor up
            neighbors.add(graph[(i - 1 to j)]!!)
        }
        if(i < maxi - 1) {
            // neighbor down
            neighbors.add(graph[(i + 1 to j)]!!)
        }
        if(j > 0) {
            // neighbor left
            neighbors.add(graph[(i to j - 1)]!!)
        }
        if(j < maxj - 1) {
            // neighbor right
            neighbors.add(graph[(i to j + 1)]!!)
        }
        return neighbors
    }

}




