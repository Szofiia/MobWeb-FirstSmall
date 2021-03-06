package hu.bme.aut.pugsweeper.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import hu.bme.aut.pugsweeper.R
import hu.bme.aut.pugsweeper.model.PugSweeperEngine

class PugSweeperView(context: Context?, attrs: AttributeSet?): View(context, attrs) {

    private var backgroundPaint = Paint()
    private var endBackgroundPaint = Paint()
    private var emptyFieldPaint = Paint()
    private var linePaint = Paint()
    private var titlePaint = Paint()
    private var subTitlePaint = Paint()

    private var minePic = BitmapFactory.decodeResource(
        context?.resources,
        R.drawable.mine)

    private var flagPic = BitmapFactory.decodeResource(
        context?.resources,
        R.drawable.flag)

    private var gridSize: Int = 5
    private var currentState: Array<Array<Short>> = PugSweeperEngine.getRevealedFields()

    init {
        backgroundPaint.color = Color.parseColor("#FEDBD0")
        backgroundPaint.style = Paint.Style.FILL

        emptyFieldPaint.color = Color.parseColor("#FBB8AC")
        emptyFieldPaint.style = Paint.Style.FILL

        endBackgroundPaint.color = Color.argb(200,100,100,100)
        endBackgroundPaint.style = Paint.Style.FILL

        linePaint.color = Color.parseColor("#FBB8AC")
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = gridSize.toFloat()

        titlePaint.color = Color.WHITE
        titlePaint.textSize = height / gridSize.toFloat()

        subTitlePaint.color = Color.WHITE
        subTitlePaint.textSize = 48f

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        titlePaint.textSize = height / gridSize.toFloat()

        flagPic = Bitmap
            .createScaledBitmap(flagPic, width / gridSize, height / gridSize, false)
        minePic = Bitmap
            .createScaledBitmap(minePic, width / gridSize, height / gridSize, false)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
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
        drawCurrentState(canvas)
        drawGameEnd(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val touchX = event.x.toInt() / (width / gridSize)
            val touchY = event.y.toInt() / (height / gridSize)

            PugSweeperEngine.revealField(touchY, touchX)
            currentState = PugSweeperEngine.getRevealedFields()
            for (i in 0 until gridSize) {
                Log.d("[GRID RESET: GRID]", currentState[i].joinToString(",", "[", "]"))
            }

            invalidate()
        }
        return true
    }

    private fun drawEmptyBoard(canvas: Canvas?) {
        val sizeOfGap = (height / gridSize).toFloat()

        // Board border
        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), linePaint)

        // Horizontal lines
        canvas?.drawLine(0f, sizeOfGap, width.toFloat(), sizeOfGap, linePaint)
        canvas?.drawLine(0f, 2 * sizeOfGap,
            width.toFloat(), 2 * sizeOfGap, linePaint)
        canvas?.drawLine(0f, 3 * sizeOfGap,
            width.toFloat(), 3 * sizeOfGap, linePaint)
        canvas?.drawLine(0f, 4 * sizeOfGap,
            width.toFloat(), 4 * sizeOfGap, linePaint)

        // Vertical lines
        canvas?.drawLine(sizeOfGap, 0f, sizeOfGap, height.toFloat(), linePaint)
        canvas?.drawLine(2 * sizeOfGap, 0f,
            2 * sizeOfGap, height.toFloat(), linePaint)
        canvas?.drawLine(3 * sizeOfGap, 0f,
            3 * sizeOfGap, height.toFloat(), linePaint)
        canvas?.drawLine(4 * sizeOfGap, 0f,
            4 * sizeOfGap, height.toFloat(), linePaint)
    }

    private fun drawCurrentState(canvas: Canvas?) {
        val sizeOfGap = (height / gridSize).toFloat()
        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                when(currentState[i][j]) {
                    PugSweeperEngine.EMPTY -> canvas?.drawRect(j * sizeOfGap, i * sizeOfGap,
                        (j + 1) * sizeOfGap, (i + 1) * sizeOfGap, backgroundPaint)
                    PugSweeperEngine.FLAG -> drawFlag(canvas, j, i)
                    PugSweeperEngine.BOMB -> drawMine(canvas, j, i)
                    PugSweeperEngine.UNREVEALED -> {}
                    else -> drawNum(canvas, j, i, currentState[i][j])
                }
            }
        }
    }

    private fun drawGameEnd(canvas: Canvas?) {
        if(PugSweeperEngine.getEndGame()) {
            canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), endBackgroundPaint)
            if(PugSweeperEngine.getWin()) {
                canvas?.drawText("You Won!", 50f, 400f, titlePaint)

                canvas?.drawText("Made by D0EXP2, Zsófia Kecskés-Solymosi", 50f, 600f, subTitlePaint)
                return
            }
            canvas?.drawText("You Lost!", 50f, 400f, titlePaint)
        }
    }

    private fun drawFlag(canvas: Canvas?, x: Int, y: Int) {
        val sizeOfGap = (height / gridSize)
        val gapRect = Rect(x * sizeOfGap, y * sizeOfGap,
            (x + 1) * sizeOfGap, (y + 1) * sizeOfGap)

        canvas?.drawBitmap(flagPic, null, gapRect, null)
    }

    private fun drawMine(canvas: Canvas?, x: Int, y: Int) {
        val sizeOfGap = (height / gridSize)
        val gapRect = Rect(x * sizeOfGap, y * sizeOfGap,
            (x + 1) * sizeOfGap, (y + 1) * sizeOfGap)

        canvas?.drawBitmap(minePic, null, gapRect, null)
    }

    private fun drawNum(canvas: Canvas?, x: Int, y: Int, num: Short) {
        val sizeOfGap = (height / gridSize).toFloat()
        val sizeOfPadding = 35

        canvas?.drawText(num.toString(), x * sizeOfGap + sizeOfPadding,
            (y + 1) * sizeOfGap - sizeOfPadding, titlePaint)
    }
}