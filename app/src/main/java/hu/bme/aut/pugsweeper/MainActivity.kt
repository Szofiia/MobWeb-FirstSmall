package hu.bme.aut.pugsweeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hu.bme.aut.pugsweeper.model.Engine

class MainActivity : AppCompatActivity() {

    val engine = Engine();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        engine.initGrid();
        engine.resetGrid();
    }
}
