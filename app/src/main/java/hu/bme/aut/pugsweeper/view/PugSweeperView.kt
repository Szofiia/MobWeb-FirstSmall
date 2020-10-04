package hu.bme.aut.pugsweeper.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import hu.bme.aut.pugsweeper.R

class PugSweeperView(contex: Context?, attrs: AttributeSet?): View(contex, attrs) {

    var paintBackground = Paint()
    var paintLine = Paint()
    var paintText = Paint()

    var minePic = BitmapFactory.decodeResource(
        contex?.resources,
        R.drawable.mine)

    var binPic = BitmapFactory.decodeResource(
        contex?.resources,
        R.drawable.bin)

    init {
        paintBackground.color = Color.GRAY
        paintBackground.style = Paint.Style.FILL

        paintLine.color = Color.WHITE
        paintLine.style = Paint.Style.STROKE
        paintLine.strokeWidth = 5f

        paintText.color = Color.BLACK
        paintText.textSize = 60f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        paintText.textSize = height / 5f

        binPic = Bitmap
            .createScaledBitmap(binPic, width / 5, height / 5, false)
        minePic = Bitmap
            .createScaledBitmap(minePic, width / 5, height / 5, false)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintBackground)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = View.MeasureSpec.getSize(widthMeasureSpec)
        val h = View.MeasureSpec.getSize(heightMeasureSpec)

        val dim = if (w == 0) h else if (h == 0) w else if (w < h) w else h
        setMeasuredDimension(dim, dim)
    }
}