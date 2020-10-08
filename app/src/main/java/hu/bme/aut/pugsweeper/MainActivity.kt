package hu.bme.aut.pugsweeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.pugsweeper.databinding.ActivityMainBinding
import hu.bme.aut.pugsweeper.model.PugSweeperEngine
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private val bombsCount = 3
    private val gridSize = 5
    private var elapsedTime = 0
    private var elapsedTimer: Timer? = null
    private lateinit var binding: ActivityMainBinding

    inner class GameTimerTask : TimerTask() {
        override fun run() {
            runOnUiThread {
                if(elapsedTime >= 300 || PugSweeperEngine.getEndGame()) {
                    stopTimer()
                } else {
                    elapsedTime++
                    elapsTimeTxt.text = elapsedTime.toString()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        PugSweeperEngine.initGame(bombsCount, gridSize, this)
        PugSweeperEngine.resetGame()

        remainingBombTxt.text = (bombsCount - PugSweeperEngine.revealedMines).toString()
        elapsTimeTxt.text = "0"

        if(elapsedTimer == null) {
            elapsedTimer = Timer()
            elapsedTimer?.schedule(GameTimerTask(), 1000, 1000)
        }

       // Pug button restarts the game
        binding.pugButton.setOnClickListener{
            PugSweeperEngine.resetGame()
            pugSweeperView.invalidate()

            remainingBombTxt.text = (bombsCount - PugSweeperEngine.revealedMines).toString()
            elapsTimeTxt.text = "0"
            restartTimer()

            Snackbar.make(pugSweeperContainer, "Your game restarted!",
                Snackbar.LENGTH_LONG).show()
        }

        // Switch switches flag or try mode
        binding.flagSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                PugSweeperEngine.setFlagMode(true)
                Snackbar.make(pugSweeperContainer, "You are placing flags!",
                    Snackbar.LENGTH_LONG).show()
            }
            else {
                PugSweeperEngine.setFlagMode(false)
                Snackbar.make(pugSweeperContainer, "You are trying fields!",
                    Snackbar.LENGTH_LONG).show()
            }
        }
    }

    fun updateText() {
        remainingBombTxt.text = (bombsCount - PugSweeperEngine.revealedMines).toString()
    }

    private fun restartTimer() {
        stopTimer()
        elapsedTimer = Timer()
        elapsedTimer?.schedule(GameTimerTask(), 1000, 1000)
    }

    private fun stopTimer() {
        elapsedTime = 0
        elapsedTimer?.cancel()
        elapsedTimer = null
    }
}
