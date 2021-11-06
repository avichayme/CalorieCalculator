package com.meiri.caloriecalculator

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class WaterProgressBar : View {
    private var segmentCount: Int = 8
        set(value) {
            field = value
            initSegments()
        }

    var xxx: Int = 4
        set(value) {
            field = value
            initSegments()
        }

    var margin: Int = resources.getDimensionPixelSize(R.dimen.default_segment_margin)
        private set
    var radius: Int = resources.getDimensionPixelSize(R.dimen.default_corner_radius)

    private var segments = mutableListOf<Segment>()
    private val segmentWidth: Float
        get() = (measuredWidth - margin * (segmentCount - 1)).toFloat() / segmentCount

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initSegments()
    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val drawingComponents = getDrawingComponents()
        drawingComponents.first.forEachIndexed { drawingIndex, rectangle ->
            canvas?.drawRoundRect(
                rectangle,
                radius.toFloat(),
                radius.toFloat(),
                drawingComponents.second[drawingIndex]
            )
        }
//        getDrawingComponents2().forEachIndexed { index, it ->
//            canvas?.drawBitmap(it,index * segmentWidth + ((index) * margin), 0.toFloat(), Paint())
//        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 100
        val desiredHeight = 100
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        //Measure Width
        val width: Int = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize //Must be this size
            MeasureSpec.AT_MOST -> min(desiredWidth, widthSize) //Can't be bigger than...
            else -> desiredWidth //Be whatever you want
        }

        //Measure Height
        val height: Int = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize //Must be this size
            MeasureSpec.AT_MOST -> min(desiredHeight, heightSize) //Can't be bigger than...
            else -> desiredHeight //Be whatever you want
        }

        setMeasuredDimension(width, height)
    }

    private fun initSegments() {
        this.segments.clear()
        segments.addAll((1..segmentCount).map { Segment(it <= xxx) })
        this.invalidate()
    }

    private fun getDrawingComponents(): Pair<MutableList<RectF>, MutableList<Paint>> {

        val rectangles = mutableListOf<RectF>()
        val paints = mutableListOf<Paint>()

        val emptySegmentPaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.YELLOW //segmentBackgroundColor
        }

        val fullSegmentPaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.BLUE //segmentSelectedBackgroundColor
        }

        segments.forEachIndexed { index, segment ->
            val startBound = index * segmentWidth + ((index) * margin)
            val endBound = startBound + segmentWidth
            rectangles.add(RectF(startBound, height.toFloat(), endBound, 0f))
            if (segment.state == Segment.SegmentState.EMPTY)
                paints.add(emptySegmentPaint)
            else
                paints.add(fullSegmentPaint)
        }

        return Pair(rectangles, paints)
    }

    private fun getDrawingComponents2(): MutableList<Bitmap> {
        val glasses = mutableListOf<Bitmap>()
        val emptyGlassBitmap = BitmapFactory.decodeResource(resources,R.drawable.empty_glass)
        val fullGlassBitmap = BitmapFactory.decodeResource(resources,R.drawable.full_glass)

        segments.forEach {
            if (it.state == Segment.SegmentState.EMPTY)
                glasses.add(emptyGlassBitmap)
            else
                glasses.add(fullGlassBitmap)
        }

        return  glasses
    }
}