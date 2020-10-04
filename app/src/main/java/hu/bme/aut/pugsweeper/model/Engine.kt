package hu.bme.aut.pugsweeper.model

import android.util.Log
import kotlin.random.Random

class Engine {
    val UNREVEALED : Short = -3
    val FLAG : Short = -2
    val BOMB : Short = -1
    val EMPTY : Short = 0

    private var scoreGrid = arrayOf<Array<Short>>()
    private var revealedGrid = arrayOf<Array<Short>>()

    private var bombNumber = 3
    private val gridSize = 5
    private var revealedBombs = 0

    private var isFlagMode = false
    private var isEndGame = false

    // Initialize grid with empty fields
    constructor() {
        // Init Grids
        for (i in 0 until gridSize) {
            var gridRow = arrayOf<Short>()
            var revealedRow = arrayOf<Short>()
            for (j in 0 until gridSize) {
                gridRow += EMPTY
                revealedRow += EMPTY
            }
            scoreGrid += gridRow
            revealedGrid += revealedRow
        }

        for (i in 0 until gridSize) {
            Log.d("[GRID INIT]", scoreGrid[i].joinToString(",", "[", "]"));
        }
    }

    // Reset grid with a number of random bombs
    fun resetGrid() {
        calculateBombs();

        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                scoreGrid[i][j] = calculateField(i, j)
                revealedGrid[i][j] = UNREVEALED
            }
        }

        for (i in 0 until gridSize) {
            Log.d("[GRID RESET: GRID]", scoreGrid[i].joinToString(",", "[", "]"))
            Log.d("[GRID RESET: REVEALED]", revealedGrid[i].joinToString(",", "[", "]"))
        }

        isEndGame = false
        revealedBombs = 0

        Log.d("[GAME RESET]", " Game end: $isEndGame Bombs: $revealedBombs")

    }

    // Handle flag and reveal mode
    fun toggleSwitch() {
        if(!isFlagMode) {
            isFlagMode = true;
            return;
        }
        isFlagMode = true;
    }

    fun revealField(x: Int, y: Int){
        if(x < 0 && y < 0 && x >= gridSize && y >= gridSize) {
            return;
        }

        if(scoreGrid[x][y] == BOMB) {
            isEndGame = true;
            return;
        }

        if (scoreGrid[x][y] == EMPTY) {
            revealField(x-1,y-1);
            revealField(x-1,y);
            revealField(x-1,y+1);

            revealField(x,y-1);
            revealField(x,y+1);

            revealField(x+1,y-1);
            revealField(x+1,y);
            revealField(x+1,y+1);
            return;
        }

        revealedGrid[x][y] = scoreGrid[x][y];
    }

    fun flagFiled(x: Int, y: Int) {
        if(scoreGrid[x][y] != BOMB) {
            isEndGame = true;
            return;
        }
        revealedBombs++;
    }

    private fun calculateBombs() {
        while(bombNumber > 0) {
            val bX = (0 until gridSize).random()
            val bY = (0 until gridSize).random()

            if(scoreGrid[bX][bY] != BOMB) {
                scoreGrid[bX][bY] = BOMB
                bombNumber--
            }
        }
    }

    // Calculates a field's value of x,y coordinates
    private fun calculateField(x: Int, y: Int): Short {
        if (scoreGrid[x][y] == BOMB) {
            return BOMB
        }

        var bombsAround = 0;

        if(isMine(x-1, y-1)) { bombsAround++ }
        if(isMine(x-1, y)) { bombsAround++ }
        if(isMine(x-1, y+1)) { bombsAround++ }

        if(isMine(x, y-1)) { bombsAround++ }
        if(isMine(x, y+1)) { bombsAround++ }

        if(isMine(x+1, y-1)) { bombsAround++ }
        if(isMine(x+1, y)) { bombsAround++ }
        if(isMine(x+1, y+1)) { bombsAround++ }

        return bombsAround.toShort()
    }

    private fun isMine(x: Int, y: Int): Boolean{
        if(x >= 0 && y >= 0 && x < gridSize && y < gridSize) {
            if(scoreGrid[x][y] == BOMB) {
                return true
            }
        }
        return false
    }
}