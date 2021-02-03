package com.zj.coherent

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Created by zhangjian on 2020/7/21.
 */
class TransferringCover : View {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        paint.color = Color.parseColor("#80000000")
    }

    fun setColor(@ColorInt color: Int) {
        paint.color = color
    }

    private var paint: Paint = Paint()

    var viewWidth: Int = 0
    var viewHeight: Int = 0

    var progress: Float = 0F
    set(value) {
        field = value
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w
        viewHeight = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val path = Path()
        val centerX = viewWidth.toDouble() / 2
        val centerY = viewHeight.toDouble() / 2
        val r = (sqrt(centerX) + sqrt(centerY)).pow(2.toDouble())  //外切圆半径
        val recent = RectF(
            (centerX - r).toFloat(),
            (centerY - r).toFloat(),
            (centerX + r).toFloat(),
            (centerY + r).toFloat()
        )
        path.moveTo(centerX.toFloat(), centerY.toFloat())
        path.arcTo(
            recent,
            270 - (1 - progress) * 360,
            (1 - progress) * 360
        )
        path.close()
        canvas?.drawPath(path, paint)
    }
}