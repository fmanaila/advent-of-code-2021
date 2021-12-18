package me.fmanaila.aoc.day10

fun isOpenBracket(c: Char) : Boolean {
    return MATCHING_BRACKETS.containsKey(c)
}

fun isClosedBracket(c: Char) : Boolean {
    return MATCHING_BRACKETS.containsValue(c)
}

val MATCHING_BRACKETS = mapOf(
    '(' to ')',
    '[' to ']',
    '{' to '}',
    '<' to '>'
)