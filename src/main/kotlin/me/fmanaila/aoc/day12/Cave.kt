package me.fmanaila.aoc.day12

class Cave(val name: String, val links: MutableList<Cave> = mutableListOf(), var visits: Int = 0) {
    fun isSmall(): Boolean {
        return name == name.lowercase()
    }

    fun isStart() : Boolean {
        return name == "start"
    }

    fun isEnd() : Boolean {
        return name == "end"
    }

    override fun toString(): String {
        return name
    }
}