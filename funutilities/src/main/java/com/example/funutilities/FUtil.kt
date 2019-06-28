package com.example.funutilities

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.IntRange
import kotlin.random.Random

fun Button.listener(listener: (View) -> Unit) {
    this.setOnClickListener {
        listener(it)
    }
}

fun Button.longListener(listener: (View) -> Boolean) {
    this.setOnLongClickListener {
        listener(it)
    }
}

fun ImageView.setColor(color: Color) {
    this.setColorFilter(color.toArgb(), PorterDuff.Mode.SCREEN)
}

fun ImageView.setColor(@IntRange(from=0, to=255) alpha: Int = 255, @IntRange(from=0, to=255) red: Int, @IntRange(from=0, to=255) green: Int, @IntRange(from=0, to=255) blue: Int) {
    this.setColorFilter(Color.argb(alpha, red, green, blue), PorterDuff.Mode.SCREEN)
}

internal fun getRandomColorVar() = Random.nextInt(0, 255)

/**
 * returns a random color
 */
fun Random.nextColor(@IntRange(from=0, to=255) alpha: Int = getRandomColorVar(),
                     @IntRange(from=0, to=255) red: Int = getRandomColorVar(),
                     @IntRange(from=0, to=255) green: Int = getRandomColorVar(),
                     @IntRange(from=0, to=255) blue: Int = getRandomColorVar()): Int = Color.argb(alpha, red, green, blue)
