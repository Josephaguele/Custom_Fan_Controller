package com.josephagu.customfancontroller

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// enum class that represents the fan speed
private enum class FanSpeed(val label: Int) {
    OFF(R.string.fan_off),
    LOW(R.string.fan_low),
    MEDIUM(R.string.fan_medium),
    HIGH(R.string.fan_high);
}

private const val RADIUS_OFFSET_LABEL = 30
private const val RADIUS_OFFSET_INDICATOR = -35

class DialView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /*The radius is the current radius of the circle. This value is set when the view is drawn on the screen.
    The fanSpeed is the current speed of the fan, which is one of the values in the FanSpeed enumeration.
    By default that value is OFF.
    Finally pointPosition is an X,Y point that will be used for drawing several of the view's elements on the screen.
    **/
    private var radius = 0.0f                   // Radius of the circle.
    private var fanSpeed = FanSpeed.OFF         // The active selection.
    // position variable which will be used to draw label and indicator circle position
    private val pointPosition: PointF = PointF(0.0f, 0.0f)

    // Initialization of a paint object with a handful of basic styles.
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create( "", Typeface.BOLD)
    }

    //to calculate the size for the custom view's dial.
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        radius = (min(width, height) / 2.0 * 0.8).toFloat()
    }

    /*This extension function on the PointF class calculates the X, Y coordinates on the screen for
    the text label and current indicator (0, 1, 2, or 3), given the current FanSpeed position and
    radius of the dial. You'll use this in onDraw().*/
    private fun PointF.computeXYForSpeed(pos: FanSpeed, radius: Float) {
        // Angles are in radians.
        val startAngle = Math.PI * (9 / 8.0)
        val angle = startAngle + pos.ordinal * (Math.PI / 4)
        x = (radius * cos(angle)).toFloat() + width / 2
        y = (radius * sin(angle)).toFloat() + height / 2
    }

    //Override the onDraw() method to render the view on the screen with the Canvas and Paint classes
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //Inside onDraw() add this line to set the paint color to gray (Color.GRAY) or
        // green (Color.GREEN) depending on whether the fan speed is OFF or any other value

        //Set dial background color to green if selection not off.
        paint.color = if (fanSpeed == FanSpeed.OFF) Color.GRAY else Color.GREEN
        //Draw a circle for the dial*/.
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paint)

        /*
        * This part uses the PointF.computeXYforSpeed() extension method to calculate the
        *  X,Y coordinates for the indicator center based on the current fan speed.*/
        // Draw the indicator circle.
        val markerRadius = radius + RADIUS_OFFSET_INDICATOR
        pointPosition.computeXYForSpeed(fanSpeed, markerRadius)
        paint.color = Color.BLACK
        canvas.drawCircle(pointPosition.x, pointPosition.y, radius/12, paint)

        /*Finally, draw the fan speed labels (0, 1, 2, 3) at the appropriate positions around the
        // dial. This part of the method calls PointF.computeXYForSpeed() again to get the position
        // for each label, and reuses the pointPosition object each time to avoid allocations.
        // Use drawText() to draw the labels.*/

        // Draw the text labels.
        val labelRadius = radius + RADIUS_OFFSET_LABEL
        for (i in FanSpeed.values()) {
            pointPosition.computeXYForSpeed(i, labelRadius)
            val label = resources.getString(i.label)
            canvas.drawText(label, pointPosition.x, pointPosition.y, paint)
        }
    }

}
