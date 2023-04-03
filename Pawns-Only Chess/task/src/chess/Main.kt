package chess

import kotlin.math.abs

var count = 0
var capturedW = 0
var capturedB = 0
val regex = Regex("[a-h][1-8][a-h][1-8]")
fun convert(coordinates: String): MutableList<Int> {
    fun chToInt(letter: Char): Int {
        return when (letter) {
            'a' -> 0
            'b' -> 1
            'c' -> 2
            'd' -> 3
            'e' -> 4
            'f' -> 5
            'g' -> 6
            'h' -> 7
            else -> Int.MAX_VALUE
        }
    }

    fun intToInt(digit: Char): Int {
        return when (digit) {
            '8' -> 0
            '7' -> 1
            '6' -> 2
            '5' -> 3
            '4' -> 4
            '3' -> 5
            '2' -> 6
            '1' -> 7
            else -> Int.MAX_VALUE
        }
    }

    val a = chToInt(coordinates[0])
    val b = intToInt(coordinates[1])
    val c = chToInt(coordinates[2])
    val d = intToInt(coordinates[3])

    return mutableListOf(b, a, d, c)
}
fun swap(list: MutableList<MutableList<String>>, i: Int, j: Int, a: Int, b: Int) {
    list[i][j] = list[a][b].also { list[a][b] = list[i][j] }
}
fun capture(list: MutableList<MutableList<String>>, i: Int, j: Int, a: Int, b: Int) {
    list[a][b] = list[i][j]
    list[i][j] = "   "
}
fun captureEnPassant(list: MutableList<MutableList<String>>, i: Int, j: Int, a: Int, b: Int) {
    if (list[i][j] == " W ") {
        list[a][b] = list[i][j].also { list[i][j] = list[a][b] }
        list[a+1][b] = "   "
    } else {
        list[a][b] = list[i][j].also { list[i][j] = list[a][b] }
        list[a-1][b] = "   "
    }
}
fun isPossibleMoveW(list: MutableList<MutableList<String>>): Boolean {
    var res = false
    for (i in 0..7) {
        for (j in 0..7) {
            if (list[i][j] == " W ") {
                when(j) {
                    in 1..6 -> { if (list[i-1][j] == "   " || list[i-1][j+1] == " B " || list[i-1][j-1] == " B ") res = true; break }
                    0 -> { if (list[i-1][j] == "   " || list[i-1][j+1] == " B ") res = true; break }
                    7 -> { if (list[i-1][j] == "   " || list[i-1][j-1] == " B ") res = true; break }
                }
            }
        }
    }
    return res
}
fun isPossibleMoveB(list: MutableList<MutableList<String>>): Boolean {
    var res = false
    for (i in 0..7) {
        for (j in 0..7) {
            if (list[i][j] == " B ") {
                when (j) {
                    in 1..6 -> { if (list[i+1][j] == "   " || list[i+1][j+1] == " W "  || list[i+1][j-1] == " W ") res = true; break }
                    0 -> { if (list[i+1][j] == "   " || list[i+1][j+1] == " W ") res = true; break }
                    7 -> { if (list[i+1][j] == "   " || list[i+1][j-1] == " W ") res = true; break }
                }
            }
        }
    }
    return res
}
fun isReachedLast(list: MutableList<MutableList<String>>,a: Int): Boolean {
    val emptyList = MutableList(8) {"   "}
    return  list[a] != emptyList
}
fun main() {
    val chessField = MutableList(8) {MutableList(8) {"   "}}
    for (i in 0..7) {
        chessField[1][i] = " B "
        chessField[6][i] = " W "
    }
    val isPassant = MutableList(2) { MutableList(8) {false} }
    val cleanList = MutableList(8) {false}
    fun printGameField() {
        println("  +---+---+---+---+---+---+---+---+")
        for (i in 0..7){
            println((8-i).toString() + " |"+chessField[i].joinToString("|") +"|")
            println("  +---+---+---+---+---+---+---+---+")
        }
        println("    a   b   c   d   e   f   g   h  ")
    }
    println("Pawns-Only Chess")
    println("First Player's name:")
    val player1Name = readLine()
    println("Second Player's name:")
    val player2Name = readLine()
    printGameField()
    while (true) {
        if (count %2 == 0) {
            if(isReachedLast(chessField,7) || capturedW == 8) { println("Black Wins!\nBye!"); break }
            if(!isPossibleMoveW(chessField)) { println("Stalemate!\nBye!"); break }
            println("$player1Name's turn:")
            val nextInput = readln()
            val startPosition = nextInput[0].toString()+nextInput[1].toString()
            val ccL = convert(nextInput) //convertedCoordinatesList
            when {
                nextInput == "exit" -> {
                    println("Bye!"); break
                }
                !regex.matches(nextInput) -> {
                    println("Invalid Input"); continue
                }
                chessField[ccL[0]][ccL[1]] != " W " -> println("No white pawn at $startPosition")
                chessField[ccL[0]][ccL[1]] == " W " &&
                        chessField[ccL[0]-1][ccL[1]] == "   " && (ccL[0] == 6 && ccL[0] - ccL[2] in 1..2 && ccL[1] == ccL[3])||
                        (ccL[0] - ccL[2] == 1 && ccL[1] == ccL[3]) && chessField[ccL[2]][ccL[3]] == "   " -> {
                    swap(chessField,ccL[0],ccL[1],ccL[2],ccL[3])
                    printGameField()
                    count++
                    isPassant[1].clear()
                    isPassant[1].addAll(cleanList)
                    if (ccL[0] == 6 && ccL[2] == 4) isPassant[0][ccL[1]] = true
                }
                chessField[ccL[0]][ccL[1]] == " W " && chessField[ccL[2]][ccL[3]] == " B " && ccL[0] - ccL[2] == 1 &&
                        abs(ccL[1] - ccL[3]) == 1 -> {
                    capture(chessField,ccL[0],ccL[1],ccL[2],ccL[3])
                    printGameField()
                    capturedB++
                    count++
                    isPassant[1].clear()
                    isPassant[1].addAll(cleanList)
                }
                chessField[ccL[0]][ccL[1]] == " W " && ccL[0] == 3 && chessField[ccL[2]+1][ccL[3]] == " B " &&
                        isPassant[1][ccL[3]] -> {
                    captureEnPassant(chessField,ccL[0],ccL[1],ccL[2],ccL[3])
                    printGameField()
                    capturedB++
                    count++
                    isPassant[1].clear()
                    isPassant[1].addAll(cleanList)
                }
                else -> println("Invalid input")
            }
        } else {
            if(isReachedLast(chessField,0) || capturedB == 8) { println("White Wins!\nBye!"); break }
            if(!isPossibleMoveB(chessField)) { println("Stalemate!\nBye!"); break }
            println("$player2Name's turn:")
            val nextInput = readln()
            val startPosition = nextInput[0].toString()+nextInput[1].toString()
            val ccL = convert(nextInput) //convertedCoordinatesList
            when {
                nextInput == "exit" -> {
                    println("Bye!"); break
                }
                !regex.matches(nextInput) -> {
                    println("Invalid Input"); continue
                }
                chessField[ccL[0]][ccL[1]] != " B " -> println("No black pawn at $startPosition")
                chessField[ccL[0]][ccL[1]] == " B " && chessField[ccL[0]+1][ccL[1]] == "   " &&
                        (ccL[0] == 1 && ccL[2] - ccL[0] in 1..2 && ccL[1] == ccL[3])||
                        (ccL[2] - ccL[0] == 1 && ccL[1] == ccL[3]) && chessField[ccL[2]][ccL[3]] == "   " -> {
                    swap(chessField, ccL[0], ccL[1], ccL[2], ccL[3])
                    printGameField()
                    count++
                    isPassant[0].clear()
                    isPassant[0].addAll(cleanList)
                    if (ccL[0] == 1 && ccL[2] == 3) isPassant[1][ccL[1]] = true
                }
                chessField[ccL[0]][ccL[1]] == " B " && chessField[ccL[2]][ccL[3]] == " W " && ccL[0] - ccL[2] == -1 &&
                        abs(ccL[1] - ccL[3]) == 1 -> {
                    capture(chessField,ccL[0],ccL[1],ccL[2],ccL[3])
                    printGameField()
                    capturedW++
                    count++
                    isPassant[0].clear()
                    isPassant[0].addAll(cleanList)
                }
                chessField[ccL[0]][ccL[1]] == " B " && ccL[0] == 4 && chessField[ccL[2]-1][ccL[3]] == " W " &&
                        isPassant[0][ccL[3]] -> {
                    captureEnPassant(chessField,ccL[0],ccL[1],ccL[2],ccL[3])
                    printGameField()
                    capturedW++
                    count++
                    isPassant[0].clear()
                    isPassant[0].addAll(cleanList)
                }
                else -> println("Invalid Input")
            }
        }
    }
}
