package hu.bme.aut.pugsweeper.model

import android.util.Log
import androidx.databinding.Bindable
import hu.bme.aut.pugsweeper.MainActivity

 object PugSweeperEngine {
    val UNREVEALED : Short = -3
    val FLAG : Short = -2
    val BOMB : Short = -1
    val EMPTY : Short = 0

    var revealedMines = 0
    var gridSize = 0

    private var mineInGame = 0
    private var isFlagMode = false
    private var isEndGame = false
    private var isWin = false

    private var scoreGrid = arrayOf<Array<Short>>()
    private var revealedGrid = arrayOf<Array<Short>>()

     private lateinit var context: MainActivity

    // Initialize grid with empty fields
    fun initGame(_bombNumber: Int, _gridSize: Int, _context: MainActivity) {
        mineInGame = _bombNumber
        gridSize = _gridSize
        context = _context

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

            isEndGame = false
            isWin = false
        }

        for (i in 0 until gridSize) {
            Log.d("[GRID INIT]", scoreGrid[i].joinToString(",", "[", "]"))
        }
    }

    // Reset grid with a number of random bombs
    fun resetGame() {
        isEndGame = false
        isWin = false
        revealedMines = 0

        Log.d("[RESET]", "revealedBombs: $revealedMines, "
                + "bombNumber: $mineInGame, gridSize: $gridSize, "
                + "isEndGame: $isEndGame, isFlagMode: $isFlagMode")
        emptyGrid()
        calculateMines()

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
    }

    // Reveal field when View touch event calls
    fun revealField(x: Int, y: Int) {
        if(x < 0 || y < 0 || x >= gridSize || y >= gridSize) {
            return
        }

        if(revealedGrid[x][y] != UNREVEALED) {
            return
        }

        if(!isFlagMode) {
            if(scoreGrid[x][y] == BOMB) {
                revealedGrid[x][y] = BOMB;
                isEndGame = true
                return
            }

            if (scoreGrid[x][y] == EMPTY) {
                revealedGrid[x][y] = scoreGrid[x][y]
                revealEmptyField(x-1,y-1)
                revealEmptyField(x-1,y)
                revealEmptyField(x-1,y+1)

                revealEmptyField(x,y-1)
                revealEmptyField(x,y+1)

                revealEmptyField(x+1,y-1)
                revealEmptyField(x+1,y)
                revealEmptyField(x+1,y+1)
                return
            }

            revealedGrid[x][y] = scoreGrid[x][y]
        } else {
            // If placing flags is on
            if(scoreGrid[x][y] == BOMB) {
                revealedGrid[x][y] = FLAG
                revealedMines++
                context.updateText()

                if (revealedMines == mineInGame) {
                    isEndGame = true
                    isWin = true
                    Log.d("[FLAG PLACING]", "Game won:")
                }

                Log.d("[FLAG PLACING]", "Flag placed on bomb"
                        + isWin.toString())
            } else {
                revealedGrid[x][y] = FLAG
                isEndGame = true
                Log.d("[FLAG PLACING]", "Flag placend else, game end: "
                        + isEndGame.toString())
            }
        }
    }

    // Public getters and setters
    fun getFlagMode() = isFlagMode

    fun setFlagMode(mode: Boolean){
        isFlagMode = mode
    }

    fun getRevealedFields() = revealedGrid

    fun getEndGame() = isEndGame

    fun getWin() = isWin

    private fun emptyGrid() {
        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                scoreGrid[i][j] = EMPTY
            }
        }
    }

    private fun calculateMines() {
        var bombsToPlace = mineInGame
        while(bombsToPlace > 0) {
            val bX = (0 until gridSize).random()
            val bY = (0 until gridSize).random()

            if(scoreGrid[bX][bY] != BOMB) {
                scoreGrid[bX][bY] = BOMB
                bombsToPlace--
            }
        }
    }

    private fun calculateField(x: Int, y: Int): Short {
        if (scoreGrid[x][y] == BOMB) {
            return BOMB
        }

        var bombsAround = 0

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

    private fun revealEmptyField(x: Int, y: Int){
        if(x < 0 || y < 0 || x >= gridSize || y >= gridSize) {
            return
        }

        if(revealedGrid[x][y] != UNREVEALED){
            return
        }

        if(scoreGrid[x][y] == BOMB) {
            return
        }

        if (scoreGrid[x][y] == EMPTY) {
            revealedGrid[x][y] = scoreGrid[x][y]
            revealEmptyField(x-1,y-1)
            revealEmptyField(x-1,y)
            revealEmptyField(x-1,y+1)

            revealEmptyField(x,y-1)
            revealEmptyField(x,y+1)

            revealEmptyField(x+1,y-1)
            revealEmptyField(x+1,y)
            revealEmptyField(x+1,y+1)
            return
        }

        revealedGrid[x][y] = scoreGrid[x][y]
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

