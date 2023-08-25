package com.rdapps.motiontext

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextPaint
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap


class MotionText(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {

    private val padding = Padding()

    val textPaint = TextPaint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        color = Color.BLACK
        textAlign = Paint.Align.LEFT
        textSize = 16f * context.resources.displayMetrics.density
    }

    var textColor: Int
        set(value) {
            textPaint.color = value
            setImageBitmap(prepareBitmap(text))
        }
        get() = textPaint.color

    var textSize: Float
        set(value) {
            textPaint.textSize = value
            setImageBitmap(prepareBitmap(text))
        }
        get() = textPaint.textSize

    private val bgPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.TRANSPARENT
    }

    val backgroundColor: Int
        get() = bgPaint.color

    override fun setBackgroundColor(color: Int) {
        bgPaint.color = color
        setImageBitmap(prepareBitmap(text))
    }

    var cornerRadius: Float = 0f
        set(value) {
            field = value
            setImageBitmap(prepareBitmap(text))
        }

    var textAllCaps: Boolean = false

    var text: String = " "
        set(value) {
            val text = if (textAllCaps) value.uppercase() else value
            field = text
            setImageBitmap(prepareBitmap(text))
        }

    private var iconDrawable: Drawable? = null

    private var iconSize = 0f
    private var iconPadding = 0f

    @ColorInt
    var iconTint = Color.TRANSPARENT
        set(value) {
            field = value
            iconDrawable?.setTint(value)
        }

    private var bitmap = prepareBitmap(text)

    init {
        fetchAttrs(attrs)
        padding.fetchAttributes(this)
        removeViewPadding()
        setImageBitmap(prepareBitmap(text))
        adjustViewBounds = true
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        padding.setPadding(left, top, right, bottom)
        setImageBitmap(prepareBitmap(text))
        removeViewPadding()
    }

    private fun removeViewPadding() {
        super.setPadding(0, 0, 0, 0)
    }

    private fun fetchAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MotionText)

        textColor = typedArray.getColor(R.styleable.MotionText_textColor, textColor)

        textSize = typedArray.getDimension(R.styleable.MotionText_textSize, textSize)

        cornerRadius = typedArray.getDimension(R.styleable.MotionText_cornerRadius, cornerRadius)

        setBackgroundColor(
            typedArray.getColor(
                R.styleable.MotionText_backgroundColor,
                backgroundColor
            )
        )

        if (typedArray.hasValue(R.styleable.MotionText_textFontFamily)) {
            if (isInEditMode && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                textPaint.typeface = typedArray.getFont(R.styleable.MotionText_textFontFamily)
            } else {
                val fontFamily = typedArray.getResourceId(R.styleable.MotionText_textFontFamily, -1)
                if (fontFamily > -1)
                    textPaint.typeface = ResourcesCompat.getFont(context, fontFamily)
            }
        }

        iconDrawable = typedArray.getDrawable(R.styleable.MotionText_icon)

        iconSize = typedArray.getDimension(
            R.styleable.MotionText_iconSize,
            iconDrawable?.let {
                if (it.intrinsicWidth > it.intrinsicHeight)
                    it.intrinsicWidth.toFloat()
                else
                    it.intrinsicHeight.toFloat()
            } ?: 0f
        )

        iconPadding = typedArray.getDimension(
            R.styleable.MotionText_iconPadding, iconPadding
        )

        iconTint = typedArray.getColor(R.styleable.MotionText_iconTint, iconTint)
        if (iconTint != Color.TRANSPARENT)
            iconDrawable?.setTint(iconTint)

        val passThroughText = typedArray.getBoolean(R.styleable.MotionText_passThroughText, false)

        if (passThroughText)
            textPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

        textAllCaps = typedArray.getBoolean(R.styleable.MotionText_textAllCaps, false)

        text = typedArray.getString(R.styleable.MotionText_text) ?: text

        typedArray.recycle()
    }

    private fun prepareBitmap(text: String): Bitmap {
        val textWidth = textPaint.measureText(text).toInt().takeIf { it > 0 } ?: 1
        val textHeight = with(textPaint.fontMetricsInt) {
            bottom - top + descent - (textPaint.textSize / 5).toInt()
        }.takeIf { it > 0 } ?: 1
        val baseline = with(textPaint.fontMetrics) {
            descent - ascent - (textPaint.textSize / 10)
        }

        val (iconWidth, iconHeight, totalIconWidth) = iconDrawable?.let {
            val ratio = it.intrinsicHeight.toFloat() / it.intrinsicWidth

            if (it.intrinsicWidth > it.intrinsicHeight) {
                Triple(iconSize, iconSize * ratio, iconSize + iconPadding)
            } else {
                val width = iconSize / ratio
                Triple(width, iconSize, width + iconPadding)
            }
        } ?: Triple(0f, 0f, 0f)

        bitmap = Bitmap.createBitmap(
            textWidth + padding.start + padding.end + totalIconWidth.toInt(),
            textHeight + padding.top + padding.bottom,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)

        // Draw background
        canvas.drawRoundRect(
            0f,
            0f,
            bitmap.width.toFloat(),
            bitmap.height.toFloat(),
            cornerRadius,
            cornerRadius,
            bgPaint
        )

        // Draw text
        canvas.drawText(
            text,
            padding.start.toFloat(),
            baseline + padding.bottom,
            textPaint
        )

        iconDrawable?.let {
            val drawableBitmap = it.toBitmap(
                iconWidth.toInt(), iconHeight.toInt(), Bitmap.Config.ARGB_8888
            )
            canvas.drawBitmap(
                drawableBitmap,
                textWidth + padding.start + iconPadding,
                bitmap.height / 2f - drawableBitmap.height / 2f,
                Paint()
            )
        }

        return bitmap
    }

    data class Padding(
        var start: Int = 0,
        var end: Int = 0,
        var top: Int = 0,
        var bottom: Int = 0
    ) {

        fun fetchAttributes(motionText: MotionText) {
            start =
                if (motionText.paddingStart != 0)
                    motionText.paddingStart
                else
                    motionText.paddingLeft

            end = if (motionText.paddingEnd != 0)
                motionText.paddingEnd
            else
                motionText.paddingRight

            top = motionText.paddingTop
            bottom = motionText.paddingBottom
        }

        fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
            start = left
            this.top = top
            end = right
            this.bottom = bottom
        }
    }
}