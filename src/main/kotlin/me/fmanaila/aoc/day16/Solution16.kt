package me.fmanaila.aoc.day16

import me.fmanaila.aoc.inputLines
import java.lang.UnsupportedOperationException
import java.util.stream.IntStream
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.streams.toList

fun main() {
    partOne("input/day16.txt")
    println("---------------------")
    partTwo("input/day16.txt")
}

fun partOne(inputFile: String) {
    inputLines(inputFile).forEach { line ->
        val binaryString = toBinary(line)
        println(binaryString.toMutableList().parsePackets().sumOf(::sumOfVersions))
    }
}

fun partTwo(inputFile: String) {
    inputLines(inputFile).forEach { line ->
        val binaryString = toBinary(line)
        // assuming there's only one root package
        println(binaryString.toMutableList().parsePacket().value())
    }
}

fun sumOfVersions(p : Packet) : Int {
    if(p is LiteralPacket) {
        return p.version
    }
    if(p is OperatorPacket) {
        return p.subPackets.sumOf(::sumOfVersions) + p.version
    }
    throw UnsupportedOperationException("Unknown packet type")
}

private fun MutableList<Char>.parsePackets(): List<Packet> {
    val packets = mutableListOf<Packet>()
    // assuming that having a 1 in the remaining input means there are more packets
    while(this.contains('1')) {
        packets.add(this.parsePacket())
    }
    return packets
}

private fun MutableList<Char>.parsePacket(): Packet {
    val version = this.parseThreeDigitNumber()
    val type = this.parseThreeDigitNumber()

    if(type == 4) {
        return LiteralPacket(version, this.parseLiteral())
    }
    val packetConstructor = when(type) {
        0 -> ::SumPacket
        1 -> ::ProductPacket
        2 -> ::MinPacket
        3 -> ::MaxPacket
        5 -> ::GtPacket
        6 -> ::LtPacket
        7 -> ::EqPacket
        else -> {
            throw UnsupportedOperationException("Invalid packet type=$type")
        }
    }
    return when (val lengthTypeId = this.removeFirst()) {
        '0' -> {
            // 15 bits = total length in bits of the sub-packets
            val lengthOfSubPackets = this.removeFirst(15).binaryToLong().toInt()
            val subPackets = this.removeFirst(lengthOfSubPackets).toMutableList().parsePackets()
            packetConstructor(version, subPackets)
        }
        '1' -> {
            // 11 bits = number of sub-packets immediately contained
            val numberOfSubPackets = this.removeFirst(11).binaryToLong().toInt()
            val subPackets = IntStream.rangeClosed(1, numberOfSubPackets).mapToObj {
                this.parsePacket()
            }.toList()
            packetConstructor(version, subPackets)
        }
        else -> {
            throw UnsupportedOperationException("Invalid lengthTypeId=$lengthTypeId")
        }
    }
}

private fun <T> MutableList<T>.removeFirst(n: Int) : List<T> {
    if(n == 0) return emptyList()
    return IntStream.rangeClosed(1, n).mapToObj {
        this.removeFirst()
    }.toList()
}

private fun List<Char>.binaryToLong() : Long {
    return this.reversed().foldIndexed(0L) { index, acc, c ->
        acc + c.digitToInt() * 2.0.pow(index).roundToLong()
    }
}

private fun MutableList<Char>.parseThreeDigitNumber(): Int {
    return this.removeFirst(3).binaryToLong().toInt()
}

private fun MutableList<Char>.parseLiteral(): Long {
    val res = mutableListOf<Char>()
    do {
        val control = this.removeFirst()
        res.addAll(this.removeFirst(4))
    } while(control == '1')
    return res.binaryToLong()
}

abstract class Packet(val version: Int) {
    abstract fun value(): Long
}

class LiteralPacket(version: Int, private val value: Long) : Packet(version) {
    override fun value(): Long {
        return this.value
    }

    override fun toString(): String {
        return "$value"
    }
}

abstract class OperatorPacket(version: Int, val subPackets: List<Packet>) : Packet(version)

class SumPacket(version: Int, subPackets: List<Packet>) : OperatorPacket(version, subPackets) {
    override fun value(): Long {
        return subPackets.sumOf(Packet::value)
    }

    override fun toString(): String {
        return "(" + subPackets.joinToString(" + ") + ")"
    }
}

class ProductPacket(version: Int, subPackets: List<Packet>) : OperatorPacket(version, subPackets) {
    override fun value(): Long {
        return subPackets.fold(1) { acc, p -> acc * p.value() }
    }

    override fun toString(): String {
        return "(" + subPackets.joinToString(" * ") + ")"
    }
}

class MinPacket(version: Int, subPackets: List<Packet>) : OperatorPacket(version, subPackets) {
    override fun value(): Long {
        return subPackets.minOf(Packet::value)
    }

    override fun toString(): String {
        return "min(" + subPackets.joinToString() + ")"
    }
}

class MaxPacket(version: Int, subPackets: List<Packet>) : OperatorPacket(version, subPackets) {
    override fun value(): Long {
        return subPackets.maxOf(Packet::value)
    }

    override fun toString(): String {
        return "max(" + subPackets.joinToString() + ")"
    }
}

class GtPacket(version: Int, subPackets: List<Packet>) : OperatorPacket(version, subPackets) {
    override fun value(): Long {
        return if (subPackets[0].value() > subPackets[1].value()) 1 else 0
    }

    override fun toString(): String {
        return "(" + subPackets.joinToString(" > ") + ")"
    }
}

class LtPacket(version: Int, subPackets: List<Packet>) : OperatorPacket(version, subPackets) {
    override fun value(): Long {
        return if (subPackets[0].value() < subPackets[1].value()) 1 else 0
    }

    override fun toString(): String {
        return "(" + subPackets.joinToString(" < ") + ")"
    }
}

class EqPacket(version: Int, subPackets: List<Packet>) : OperatorPacket(version, subPackets) {
    override fun value(): Long {
        return if (subPackets[0].value() == subPackets[1].value()) 1 else 0
    }

    override fun toString(): String {
        return "(" + subPackets.joinToString(" == ") + ")"
    }
}

fun toBinary(input: String): String {
    return input.map(HEX_TO_BIN::getValue).joinToString("")
}

val HEX_TO_BIN = mapOf<Char, String>(
    '0' to "0000",
    '1' to "0001",
    '2' to "0010",
    '3' to "0011",
    '4' to "0100",
    '5' to "0101",
    '6' to "0110",
    '7' to "0111",
    '8' to "1000",
    '9' to "1001",
    'A' to "1010",
    'B' to "1011",
    'C' to "1100",
    'D' to "1101",
    'E' to "1110",
    'F' to "1111",
)

