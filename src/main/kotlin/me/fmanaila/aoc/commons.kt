package me.fmanaila.aoc

fun inputLines(file: String): Iterator<String> = ClassLoader.getSystemResourceAsStream(file).bufferedReader().readLines().iterator()
