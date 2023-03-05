package com.example.watch.view

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Canvas
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.example.watch.R
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
import kotlin.properties.Delegates

class ClockView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = R.attr.clockStyle,
    defStyleRes: Int = R.style.DefaultClockStyle
) : View(context, attributeSet, defStyleAttr, defStyleRes) {

    private val horsSet: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)

    private val rectText: Rect = Rect()

    private val safeRectangleClock: RectF = RectF()

    private var innerRadius: Float by Delegates.notNull()

    var colorHourHand: Int = DEFAULT_COLOR
        set(value) {
            field = value
            createPaintHourHand()
        }

    var colorMinuteHand: Int = DEFAULT_COLOR
        set(value) {
            field = value
            createPaintMinuteHand()
        }
    var colorSecondHand: Int = DEFAULT_COLOR
        set(value) {
            field = value
            createPaintSecondHand()
        }
    var colorOuterCircle: Int = DEFAULT_COLOR
        set(value) {
            field = value
            createPaintOuterCircle()
        }
    var colorText: Int = DEFAULT_COLOR
        set(value) {
            field = value
            createPaintText()
        }
    var colorMinuteScale: Int = DEFAULT_COLOR
        set(value) {
            field = value
            createPaintMinuteScale()
        }
    var colorInnerBackground: Int = DEFAULT_COLOR_BACKGROUND
        set(value) {
            field = value
            createInnerBackgroundPaint()
        }

    private var outerCircleWidth: Float by Delegates.notNull()

    private lateinit var paintHourHand: Paint
    private lateinit var paintMinuteHand: Paint
    private lateinit var paintSecondHand: Paint
    private lateinit var paintOuterCircle: Paint
    private lateinit var paintText: Paint
    private lateinit var paintMinuteScale: Paint
    private lateinit var paintInnerBackground: Paint

    init {
        initAttributes(attributeSet, defStyleAttr, defStyleRes)
        createPaints()
    }

    private fun initAttributes(attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val typedArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.ClockView,
            defStyleAttr,
            defStyleRes
        )
        colorHourHand = typedArray.getColor(R.styleable.ClockView_hourHandColor, DEFAULT_COLOR)
        colorMinuteHand = typedArray.getColor(R.styleable.ClockView_minuteHandColor, DEFAULT_COLOR)
        colorSecondHand = typedArray.getColor(R.styleable.ClockView_secondHandColor, DEFAULT_COLOR)
        colorOuterCircle =
            typedArray.getColor(R.styleable.ClockView_outerCircleColor, DEFAULT_COLOR)
        colorText = typedArray.getColor(R.styleable.ClockView_textNumbersColor, DEFAULT_COLOR)
        colorMinuteScale =
            typedArray.getColor(R.styleable.ClockView_minuteScaleColor, DEFAULT_COLOR)
        colorInnerBackground =
            typedArray.getColor(R.styleable.ClockView_innerBackgroundColor, Color.LTGRAY)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val minHeight = suggestedMinimumHeight + paddingTop + paddingBottom
        val defaultSize =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_SIZE,
                resources.displayMetrics
            ).toInt()
        setMeasuredDimension(
            resolveSize(max(minWidth, defaultSize), widthMeasureSpec),
            resolveSize(max(minHeight, defaultSize), heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val safeWidth = w - paddingLeft - paddingRight
        val safeHeight = h - paddingTop - paddingBottom
        val min = min(safeHeight, safeWidth)
        val max = max(safeHeight, safeWidth)
        safeRectangleClock.left =
            if (safeHeight < safeWidth) paddingLeft.toFloat() + ((max - min) * HALF_RATIO) else paddingLeft.toFloat()
        safeRectangleClock.right = safeRectangleClock.left + min
        safeRectangleClock.top =
            if (safeHeight > safeWidth) paddingTop.toFloat() + ((max - min) * HALF_RATIO) else paddingTop.toFloat()
        safeRectangleClock.bottom = safeRectangleClock.top + min
        outerCircleWidth = min * OUTER_CIRCLE_RATIO_OF_RADIUS
        innerRadius = (min * HALF_RATIO) - outerCircleWidth * HALF_RATIO
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val calendar = Calendar.getInstance()
        val centerX = safeRectangleClock.centerX()
        val centerY = safeRectangleClock.centerY()
        if (safeRectangleClock.width() == 0f || safeRectangleClock.height() == 0f) return
        onDrawInnerBackground(canvas, centerX, centerY, innerRadius)
        onDrawCircle(canvas, centerX, centerY, innerRadius)
        onDrawMinuteScale(canvas, centerX, centerY, innerRadius)
        drawClockNumbers(canvas, centerX, centerY, innerRadius)
        drawHandsLines(canvas, centerX, centerY, innerRadius, calendar)

        postInvalidateDelayed(DELAY_DRAW)
        invalidate()
    }

    private fun drawHandsLines(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        radius: Float,
        calendar: Calendar
    ) {
        drawSecondHandLine(canvas, centerX, centerY, radius, calendar)
        drawMinuteHandLine(canvas, centerX, centerY, radius, calendar)
        drawHourHandLine(canvas, centerX, centerY, radius, calendar)
    }

    private fun onDrawInnerBackground(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        radius: Float
    ) {
        canvas.drawCircle(centerX, centerY, radius, paintInnerBackground)
    }

    private fun onDrawCircle(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        paintOuterCircle.strokeWidth = outerCircleWidth
        canvas.drawCircle(centerX, centerY, radius, paintOuterCircle)
    }

    private fun onDrawMinuteScale(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {

        for (second in 0..COUNT_SECONDS_OR_MINUTES) {
            val x =
                (centerX + cos(getAngleForMinute(second.toFloat())) * (radius * PADDING_SCALE_RATIO_OF_RADIUS))
            val y =
                (centerY + sin(getAngleForMinute(second.toFloat())) * (radius * PADDING_SCALE_RATIO_OF_RADIUS))
            val sizeBigDots = radius * SCALE_RATIO_OF_RADIUS
            val sizeSmallDots = sizeBigDots * SCALE_RATIO_OF_BIG_DOTS

            if (second % 5 == 0)
                canvas.drawCircle(x, y, sizeBigDots, paintMinuteScale)
            else
                canvas.drawCircle(x, y, sizeSmallDots, paintMinuteScale)
        }
    }

    private fun drawClockNumbers(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        val fontSize =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                innerRadius * TEXT_SCALE_RATIO_OF_RADIUS,
                resources.displayMetrics
            )
        paintText.textSize = fontSize

        for (hour in horsSet) {
            val strHour = hour.toString()
            paintText.getTextBounds(strHour, 0, strHour.length, rectText)
            val angle =
                PI / (COUNT_HOUR * HALF_RATIO) * (hour - (COUNT_HOUR * HALF_RATIO) * HALF_RATIO)
            val x =
                (centerX + cos(angle) * (radius * PADDING_TEXT_RATIO_OF_RADIUS) - rectText.width() * HALF_RATIO).toFloat()
            val y =
                (centerY + sin(angle) * (radius * PADDING_TEXT_RATIO_OF_RADIUS) + rectText.height() * HALF_RATIO).toFloat()
            canvas.drawText(strHour, x, y, paintText)
        }
    }

    private fun drawHourHandLine(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        radius: Float,
        calendar: Calendar
    ) {
        var hour = calendar.get(Calendar.HOUR_OF_DAY)
        hour = if (hour > COUNT_HOUR) hour - COUNT_HOUR else hour
        val min = calendar.get(Calendar.MINUTE)
        val moment =
            (((hour + (min / COUNT_SECONDS_OR_MINUTES)) * FIVE_MINUTE) + (min * (FIVE_MINUTE / COUNT_SECONDS_OR_MINUTES)))
        paintHourHand.strokeWidth = innerRadius * HOUR_HAND_WIDTH_RATIO
        canvas.drawLine(
            (centerX - cos(getAngleForMinute(moment)) * (radius * TAIL_HAND_RATIO)),
            (centerY - sin(getAngleForMinute(moment)) * (radius * TAIL_HAND_RATIO)),
            (centerX + cos(getAngleForMinute(moment)) * radius * SHORT_HAND_TRUNCATION_RATIO),
            (centerY + sin(getAngleForMinute(moment)) * radius * SHORT_HAND_TRUNCATION_RATIO),
            paintHourHand
        )
    }

    private fun drawMinuteHandLine(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        radius: Float,
        calendar: Calendar
    ) {
        val sec = calendar.get(Calendar.SECOND)
        val moment =
            (calendar.get(Calendar.MINUTE) + (sec * (ONE_MINUTE / COUNT_SECONDS_OR_MINUTES)))
        paintMinuteHand.strokeWidth = innerRadius * MINUTE_HAND_WIDTH_RATIO
        canvas.drawLine(
            (centerX - cos(getAngleForMinute(moment)) * (radius * TAIL_HAND_RATIO)),
            (centerY - sin(getAngleForMinute(moment)) * (radius * TAIL_HAND_RATIO)),
            (centerX + cos(getAngleForMinute(moment)) * radius * LONG_HAND_TRUNCATION_RATIO),
            (centerY + sin(getAngleForMinute(moment)) * radius * LONG_HAND_TRUNCATION_RATIO),
            paintMinuteHand
        )
    }

    private fun drawSecondHandLine(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        radius: Float,
        calendar: Calendar
    ) {
        val moment = calendar.get(Calendar.SECOND).toFloat()
        val handLength = radius * LONG_HAND_TRUNCATION_RATIO
        paintSecondHand.strokeWidth = innerRadius * SECOND_HAND_WIDTH_RATIO
        canvas.drawLine(
            (centerX - cos(getAngleForMinute(moment)) * (radius * TAIL_HAND_RATIO)),
            (centerY - sin(getAngleForMinute(moment)) * (radius * TAIL_HAND_RATIO)),
            (centerX + cos(getAngleForMinute(moment)) * handLength),
            (centerY + sin(getAngleForMinute(moment)) * handLength),
            paintSecondHand
        )
    }

    private fun getAngleForMinute(moment: Float): Float =
        (PI * moment / 30 - PI * HALF_RATIO).toFloat()

    private fun createPaints() {
        createPaintHourHand()
        createPaintSecondHand()
        createPaintMinuteHand()
        createPaintOuterCircle()
        createPaintMinuteScale()
        createPaintText()
        createInnerBackgroundPaint()
    }

    private fun createPaintHourHand() {
        paintHourHand = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            reset()
            color = colorHourHand
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
    }

    private fun createPaintMinuteHand() {
        paintMinuteHand = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorMinuteHand
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
    }

    private fun createPaintSecondHand() {
        paintSecondHand = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorSecondHand
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
    }

    private fun createPaintOuterCircle() {
        paintOuterCircle = Paint().apply {
            color = colorOuterCircle
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
    }

    private fun createPaintMinuteScale() {
        paintMinuteScale = Paint().apply {
            color = colorMinuteScale
            style = Paint.Style.FILL
            isAntiAlias = true
        }
    }

    private fun createPaintText() {
        paintText = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.color = colorText
            it.style = Paint.Style.FILL
            it.isAntiAlias = true
        }
    }

    private fun createInnerBackgroundPaint() {
        paintInnerBackground = Paint().also {
            it.color = colorInnerBackground
            it.style = Paint.Style.FILL
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()!!
        val savedState = SavedState(superState)
        return savedState.apply {
            colorHourHand = this@ClockView.colorHourHand
            colorMinuteHand = this@ClockView.colorMinuteHand
            colorSecondHand = this@ClockView.colorSecondHand
            colorOuterCircle = this@ClockView.colorOuterCircle
            colorText = this@ClockView.colorText
            colorMinuteScale = this@ClockView.colorMinuteScale
            colorInnerBackground = this@ClockView.colorInnerBackground
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        this@ClockView.colorHourHand = savedState.colorHourHand ?: DEFAULT_COLOR
        this@ClockView.colorMinuteHand = savedState.colorMinuteHand ?: DEFAULT_COLOR
        this@ClockView.colorSecondHand = savedState.colorSecondHand ?: DEFAULT_COLOR
        this@ClockView.colorOuterCircle = savedState.colorOuterCircle ?: DEFAULT_COLOR
        this@ClockView.colorText = savedState.colorText ?: DEFAULT_COLOR
        this@ClockView.colorMinuteScale = savedState.colorMinuteScale ?: DEFAULT_COLOR
        this@ClockView.colorInnerBackground =
            savedState.colorInnerBackground ?: DEFAULT_COLOR_BACKGROUND
    }

    private class SavedState : BaseSavedState {

        var colorHourHand: Int? = null
        var colorMinuteHand: Int? = null
        var colorSecondHand: Int? = null
        var colorOuterCircle: Int? = null
        var colorText: Int? = null
        var colorMinuteScale: Int? = null
        var colorInnerBackground: Int? = null

        constructor(superState: Parcelable) : super(superState)

        constructor(parcel: Parcel) : super(parcel) {
            colorHourHand = parcel.readInt()
            colorMinuteHand = parcel.readInt()
            colorSecondHand = parcel.readInt()
            colorOuterCircle = parcel.readInt()
            colorText = parcel.readInt()
            colorMinuteScale = parcel.readInt()
            colorInnerBackground = parcel.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(colorHourHand!!)
            out.writeInt(colorMinuteHand!!)
            out.writeInt(colorSecondHand!!)
            out.writeInt(colorOuterCircle!!)
            out.writeInt(colorText!!)
            out.writeInt(colorMinuteScale!!)
            out.writeInt(colorInnerBackground!!)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel): SavedState {
                    return SavedState(source)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return Array(size) { null }
                }
            }
        }
    }

    private companion object {
        const val DEFAULT_COLOR = Color.BLACK
        const val DEFAULT_COLOR_BACKGROUND = Color.LTGRAY
        const val DEFAULT_SIZE = 200f
        const val LONG_HAND_TRUNCATION_RATIO = 0.8f
        const val SHORT_HAND_TRUNCATION_RATIO = 0.6f
        const val TAIL_HAND_RATIO = 0.1f
        const val SECOND_HAND_WIDTH_RATIO = 0.02f
        const val HOUR_HAND_WIDTH_RATIO = 0.06f
        const val MINUTE_HAND_WIDTH_RATIO = 0.03f
        const val COUNT_SECONDS_OR_MINUTES = 60
        const val SCALE_RATIO_OF_RADIUS = 0.02f
        const val SCALE_RATIO_OF_BIG_DOTS = 0.6f
        const val TEXT_SCALE_RATIO_OF_RADIUS = 0.08f
        const val OUTER_CIRCLE_RATIO_OF_RADIUS = 0.03f
        const val PADDING_TEXT_RATIO_OF_RADIUS = 0.75f
        const val PADDING_SCALE_RATIO_OF_RADIUS = 0.9f
        const val ONE_MINUTE = 1f
        const val FIVE_MINUTE = 5f
        const val HALF_RATIO = 0.5f
        const val COUNT_HOUR = 12
        const val DELAY_DRAW = 500L
    }
}
