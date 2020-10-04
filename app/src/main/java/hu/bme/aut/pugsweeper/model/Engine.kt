package hu.bme.aut.pugsweeper.model

import android.util.Log

class Engine {
    public val FLAG : Short = -2
    public val BOMB : Short = -1
    public val EMPTY : Short = 0

    private var grid = arrayOf<Array<Short>>()

    private var bombNumber = 3
    private val gridSize = 5

    // Initialize grid with empty fields
    fun initGrid() {
        for (i in 0 until gridSize) {
            var gridRow = arrayOf<Short>()
            for (j in 0 until gridSize) {
                gridRow += EMPTY
            }
            grid += gridRow
        }

        for (i in 0 until gridSize) {
            Log.d("[GRID INIT]", grid[i].joinToString(",", "[", "]"));
        }
    }

    // Reset grid with a number of random bombs
    public fun resetGrid() {
        calculateBombs();

        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                grid[i][j] = calculateField(i, j)
            }
        }

        for (i in 0 until gridSize) {
            Log.d("[GRID RESET]", grid[i].joinToString(",", "[", "]"));
        }
    }

    private fun calculateBombs() {
        while(bombNumber > 0) {
            val bX = (0 until gridSize).random()
            val bY = (0 until gridSize).random()

            if(grid[bX][bY] != BOMB) {
                grid[bX][bY] = BOMB
                bombNumber--
            }
        }
    }

    // Calculates a field's value of x,y coordinates
    private fun calculateField(x: Int, y: Int): Short {
        if (grid[x][y] == BOMB) {
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
            if(grid[x][y] == BOMB) {
                return true
            }
        }
        return false
    }

}