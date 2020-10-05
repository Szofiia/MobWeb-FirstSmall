package hu.bme.aut.pugsweeper.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import hu.bme.aut.pugsweeper.MainActivity
import hu.bme.aut.pugsweeper.R
import hu.bme.aut.pugsweeper.model.Engine

class PugSweeperView(contex: Context?, attrs: AttributeSet?): View(contex, attrs) {

    var paintBackground = Paint()
    var paintEndBackground = Paint()
    var paintEmpty = Paint()
    var paintLine = Paint()
    var paintText = Paint()

    var minePic = BitmapFactory.decodeResource(
        contex?.resources,
        R.drawable.mine)

    var flagPic = BitmapFactory.decodeResource(
        contex?.resources,
        R.drawable.flag)

    var currentState: Array<Array<Short>> = Engine.getRevealedFields()

    init {
        paintBackground.color = Color.GRAY
        paintBackground.style = Paint.Style.FILL

        paintEmpty.color = Color.RED
        paintEmpty.style = Paint.Style.FILL

        paintEndBackground.color = Color.argb(200,100,100,100)
        paintEndBackground.style = Paint.Style.FILL

        paintLine.color = Color.WHITE
        paintLine.style = Paint.Style.STROKE
        paintLine.strokeWidth = 5f

        paintText.color = Color.BLACK
        paintText.textSize = 60f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        paintText.textSize = height / 5f

        flagPic = Bitmap
            .createScaledBitmap(flagPic, width / 5, height / 5, false)
        minePic = Bitmap
            .createScaledBitmap(minePic, width / 5, height / 5, false)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintBackground)
        if(currentState.isEmpty()) {
            drawBoard(canvas, null)
        }
        else {
            drawBoard(canvas, currentState)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = View.MeasureSpec.getSize(widthMeasureSpec)
        val h = View.MeasureSpec.getSize(heightMeasureSpec)

        val dim = if (w == 0) h else if (h == 0) w else if (w < h) w else h
        setMeasuredDimension(dim, dim)
    }

    private fun drawBoard(canvas: Canvas?, currentState: Array<Array<Short>>?) {
        drawEmptyBoard(canvas)

        if(currentState == null) {
            return
        }

        for (i in 0 until 5) {
            Log.d("[DRAW STATE]", currentState[i].joinToString(",", "[", "]"))
        }

        val sizeOfGap = (height / 5).toFloat();
        for (i in 0 until 5) {
            for (j in 0 until 5) {
                when(currentState[i][j]) {
                    Engine.EMPTY -> canvas?.drawRect(j * sizeOfGap, i * sizeOfGap,
                        (j + 1) * sizeOfGap, (i + 1) * sizeOfGap, paintBackground)
                    Engine.FLAG -> drawFlag(canvas, j, i)
                    Engine.BOMB -> drawMine(canvas, j, i)
                    Engine.UNREVEALED -> {}
                    else -> drawNum(canvas, j, i, currentState[i][j])
                }
            }
        }

        // Detect if game ends
        if(Engine.getEndGame()) {
            canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintEndBackground)
            if(Engine.getWin()) {
                canvas?.drawText("You Won!", 0f, 120f, paintText)

                canvas?.drawText("Made by", 0f, 240f, paintText)
                canvas?.drawText("D0EXP2, Zsófia Kecskés-Solymosi", 0f, 320f, paintText)
                return;
            }
            canvas?.drawText("You Lost!", 0f, 120f, paintText)
        }
    }

    private fun drawFlag(canvas: Canvas?, x: Int, y: Int) {
        val sizeOfGap = (height / 5);
        val gapRect = Rect(x * sizeOfGap, y * sizeOfGap,
            (x + 1) * sizeOfGap, (y + 1) * sizeOfGap)

        canvas?.drawBitmap(flagPic, null, gapRect, null)
    }

    private fun drawMine(canvas: Canvas?, x: Int, y: Int) {
        val sizeOfGap = (height / 5);
        val gapRect = Rect(x * sizeOfGap, y * sizeOfGap,
            (x + 1) * sizeOfGap, (y + 1) * sizeOfGap)

        canvas?.drawBitmap(minePic, null, gapRect, null)
    }

    private fun drawNum(canvas: Canvas?, x: Int, y: Int, num: Short) {
        val sizeOfGap = (height / 5).toFloat();
        val sizeOfPadding = 20;

        canvas?.drawText(num.toString(), x * sizeOfGap + sizeOfPadding,
            (y + 1) * sizeOfGap - sizeOfPadding, paintText)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            Log.d("[TOUCHEVENT]", "I touched the screen" + Engine.getFlagMode().toString())

            val touchX = event.x.toInt() / (width / 5)
            val touchY = event.y.toInt() / (height / 5)

            Engine.revealField(touchY, touchX)
            currentState = Engine.getRevealedFields()
            for (i in 0 until 5) {
                Log.d("[GRID RESET: GRID]", currentState[i].joinToString(",", "[", "]"))
            }

            invalidate()
        }
        return true
    }

    fun drawEmptyBoard(canvas: Canvas?) {
        val sizeOfGap = (height / 5).toFloat();

        // Board border
        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintLine)

        // Horizontal lines
        canvas?.drawLine(0f, sizeOfGap, width.toFloat(), sizeOfGap, paintLine)
        canvas?.drawLine(0f, 2 * sizeOfGap,
            width.toFloat(), 2 * sizeOfGap, paintLine)
        canvas?.drawLine(0f, 3 * sizeOfGap,
            width.toFloat(), 3 * sizeOfGap, paintLine)
        canvas?.drawLine(0f, 4 * sizeOfGap,
            width.toFloat(), 4 * sizeOfGap, paintLine)

        // Vertical lines
        canvas?.drawLine(sizeOfGap, 0f, sizeOfGap, height.toFloat(), paintLine)
        canvas?.drawLine(2 * sizeOfGap, 0f,
            2 * sizeOfGap, height.toFloat(), paintLine)
        canvas?.drawLine(3 * sizeOfGap, 0f,
            3 * sizeOfGap, height.toFloat(), paintLine)
        canvas?.drawLine(4 * sizeOfGap, 0f,
            4 * sizeOfGap, height.toFloat(), paintLine)
    }

    fun restart() {
        invalidate()
    }
}