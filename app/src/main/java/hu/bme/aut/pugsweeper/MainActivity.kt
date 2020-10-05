package hu.bme.aut.pugsweeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.pugsweeper.model.Engine
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val bombsCount = 3
    val gridSize = 5
        get() = field

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Engine.initGame(bombsCount, gridSize);
        Engine.resetGame();
        remainingBombTxt.text = (bombsCount - Engine.revealedMines).toString()

        pugButton.setOnClickListener{
            Engine.resetGame()
            pugSweeperView.invalidate()
            Snackbar.make(pugSweeperContainer, "Your game restarted!", Snackbar.LENGTH_LONG).show()
        }

        flagSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Engine.setFlagMode(true)
                Snackbar.make(pugSweeperContainer, "You are placing flags!", Snackbar.LENGTH_LONG).show()
            }
            else {
                Engine.setFlagMode(false)
                Snackbar.make(pugSweeperContainer, "You are trying fields!", Snackbar.LENGTH_LONG).show()
            }
        }

    }

}
