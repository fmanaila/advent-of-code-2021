package me.fmanaila.aoc.day19

import me.fmanaila.aoc.inputLines
import kotlin.math.abs

fun main() {
    val scanners = readScanners("input/day19.txt")
    scanners.forEach(::println)

    val locatedScanners: List<LocatedScanner> = locateScanners(scanners)

    partOne(locatedScanners)
    partTwo(locatedScanners)
}

fun partOne(locatedScanners: List<LocatedScanner>) {
    val totalBeacons = locatedScanners.flatMap { it.absoluteBeacons() }.toSet().count()
    println("Total beacons: $totalBeacons")
}

fun partTwo(locatedScanners: List<LocatedScanner>) {
    val maxDistance = distances(locatedScanners.map { it.coordinates })
        .values
        .flatten()
        .maxOf(Distance::manhattan)

    println("Largest Manhattan distance: $maxDistance")
}

fun locateScanners(scanners: List<Scanner>): MutableList<LocatedScanner> {
    val locatedScanners = mutableListOf(
        LocatedScanner(
            coordinates = Coordinates(0, 0, 0),
            rotationToAlign = IDENTITY_ROTATION,
            label = "0",
            beacons = scanners[0].beacons
        )
    )
    val remainingScanners = scanners.subList(1, scanners.size).toMutableList()

    var locatedScannerIndex = 0
    while (locatedScannerIndex < locatedScanners.size) {
        val locatedScanner = locatedScanners[locatedScannerIndex].absolute()
        val remainingScannersIt = remainingScanners.iterator()
        while (remainingScannersIt.hasNext()) {
            val nextScanner = remainingScannersIt.next()
            val locatedNextScanner = tryLocateScanner(locatedScanner, nextScanner)
            if (locatedNextScanner != null) {
                locatedScanners.add(locatedNextScanner)
                remainingScannersIt.remove()
            }
        }
        println("located ${locatedScanners.size.toString().padStart(3)} / ${scanners.size}")
        locatedScannerIndex++
    }

    println("Located scanners: ")
    locatedScanners.forEach(::println)

    if (remainingScanners.isNotEmpty()) {
        println("Scanners not located below:")
        remainingScanners.forEach(::println)
        throw RuntimeException("Could not locate all scanners")
    }
    return locatedScanners
}

fun readScanners(inputString: String) = inputLines(inputString).asSequence()
    .fold(mutableListOf<Scanner>() to mutableListOf<Beacon>()) { (scanners, beacons), line ->
        if (line.startsWith("---") or line.isBlank()) { // new scanner
            if (beacons.size == 0) {
                scanners to beacons
            } else {
                scanners.add(Scanner(scanners.size.toString(), beacons))
                scanners to mutableListOf<Beacon>()
            }
        } else {
            val coords = line.split(",").map(String::toInt)
            beacons.add(Beacon(coords[0], coords[1], coords[2]))
            scanners to beacons
        }
    }.let { (scanners, beacons) ->
        scanners.add(Scanner(scanners.size.toString(), beacons))
        scanners
    }.toList()

fun tryLocateScanner(sourceScanner: Scanner, nextScanner: Scanner): LocatedScanner? {
    for (rotation in ROTATIONS) {
//        println("Trying to locate scanner ${nextScanner.label} using ${sourceScanner.label} and rotation ${rotation.describe()}")
        val rotatedScanner = nextScanner.rotate(rotation)
        val sourceDistances = distances(sourceScanner.beacons)
        val targetDistances = distances(rotatedScanner.beacons)
        // if there is 1 beacon in each scanner with same distance to at least 11 other beacons,
        // as the beacon in the other scanner,
        // then the beacons are the same
        for ((src, sDistances) in sourceDistances) {
            for ((trg, tDistances) in targetDistances) {
                val overlap = overlap(sDistances, tDistances)
                if (overlap >= 11) {
                    // same beacon, located scanner
                    val coordinates = Coordinates(
                        src.x - trg.x,
                        src.y - trg.y,
                        src.z - trg.z
                    )
                    println("Located scanner ${nextScanner.label} using ${sourceScanner.label} at " +
                            "$coordinates using rotation ${rotation.describe()}")
                    return LocatedScanner(
                        coordinates = coordinates,
                        rotationToAlign = rotation,
                        label = nextScanner.label,
                        beacons = nextScanner.beacons
                    )
                }
            }
        }
    }
    return null
}

fun overlap(sDistances: List<Distance>, tDistances: List<Distance>): Int {
    return sDistances.intersect(tDistances.toSet()).size
}

fun distances(coords: List<Coordinates>): Map<Coordinates, List<Distance>> {
    return coords.associateWith { src ->
        coords.mapNotNull { tgt -> if (src !== tgt) src.distanceTo(tgt) else null }
    }
}

data class Distance(val x: Int, val y: Int, val z: Int) {
    fun manhattan() : Int {
        return abs(x) + abs(y) + abs(z)
    }
}

typealias Beacon = Coordinates

data class Coordinates(val x: Int, val y: Int, val z: Int) {
    fun distanceTo(c: Coordinates): Distance {
        return Distance(x - c.x, y - c.y, z - c.z)
    }

    override fun toString(): String {
        return "(${x.toString().padStart(5)}, ${y.toString().padStart(5)}, ${z.toString().padStart(5)})"
    }
}

open class Scanner(val label: String, val beacons: List<Beacon>) {
    fun rotate(rotation: Rotation): Scanner {
        return Scanner(label, beacons.map { rotation.rotate(it) }.toList())
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.appendLine("--- Scanner $label ---")
        beacons.forEach{ sb.appendLine("$it") }
        sb.appendLine()
        return sb.toString()
    }
}

class LocatedScanner(
    val coordinates: Coordinates,
    val rotationToAlign: Rotation,
    label: String,
    beacons: List<Beacon>
) : Scanner(label, beacons) {

    private val translation = Translation { (x, y, z) ->
        Coordinates(coordinates.x + x, coordinates.y + y, coordinates.z + z)
    }

    fun absolute(): Scanner {
        return Scanner("$label @ $coordinates rot ${rotationToAlign.describe()}", absoluteBeacons())
    }

    fun absoluteBeacons(): List<Beacon> {
        return beacons.map(this::absoluteBeacon).toList()
    }

    private fun absoluteBeacon(b: Beacon): Beacon {
        return b
            .let { rotationToAlign.rotate(it) }
            .let { translation.translate(it) }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.appendLine("--- Scanner $label @ $coordinates rot ${rotationToAlign.describe()}---")
        beacons.forEach{ sb.appendLine("$it  abs: ${absoluteBeacon(it)}") }
        sb.appendLine()
        return sb.toString()
    }
}

fun interface Translation {
    fun translate(c: Coordinates): Coordinates
}

fun interface Rotation {
    fun rotate(c: Coordinates): Coordinates

    fun describe(): String {
        val c = rotate(Coordinates(1,2,3))
        return "(1,2,3) -> (${c.x}, ${c.y}, ${c.z})"
    }
}

val IDENTITY_ROTATION = Rotation { Coordinates( it.x,  it.y,  it.z) }
val ROTATIONS = arrayOf(
    Rotation { Coordinates( it.x,  it.y,  it.z) }, // identity
    Rotation { Coordinates(-it.z,  it.y,  it.x) },
    Rotation { Coordinates(-it.x,  it.y, -it.z) },
    Rotation { Coordinates( it.z,  it.y, -it.x) },
    Rotation { Coordinates( it.x, -it.y, -it.z) },
    Rotation { Coordinates(-it.z, -it.y, -it.x) },
    Rotation { Coordinates(-it.x, -it.y,  it.z) },
    Rotation { Coordinates( it.z, -it.y,  it.x) },

    Rotation { Coordinates( it.y,  it.x, -it.z) },
    Rotation { Coordinates(-it.z,  it.x, -it.y) },
    Rotation { Coordinates(-it.y,  it.x,  it.z) },
    Rotation { Coordinates( it.z,  it.x,  it.y) },
    Rotation { Coordinates( it.y, -it.x,  it.z) },
    Rotation { Coordinates(-it.z, -it.x,  it.y) },
    Rotation { Coordinates(-it.y, -it.x, -it.z) },
    Rotation { Coordinates( it.z, -it.x, -it.y) },

    Rotation { Coordinates( it.y,  it.z,  it.x) },
    Rotation { Coordinates( it.x,  it.z, -it.y) },
    Rotation { Coordinates(-it.y,  it.z, -it.x) },
    Rotation { Coordinates(-it.x,  it.z,  it.y) },
    Rotation { Coordinates( it.x, -it.z,  it.y) },
    Rotation { Coordinates(-it.y, -it.z,  it.x) },
    Rotation { Coordinates(-it.x, -it.z, -it.y) },
    Rotation { Coordinates( it.y, -it.z, -it.x) },
)