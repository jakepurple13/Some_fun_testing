package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.lang.Math.round


/**
 * Created by Bachors on 10/31/2017.
 * https://github.com/bachors/Android-Img2Ascii
 */

enum class AsciiQuality(val value: Int) {
    BEST(1),
    GOOD(2),
    OK(3),
    BAD(4),
    WORST(5)
}

class ImgAscii {

    private val chars = arrayOf("@", "#", "+", "\\", ";", ":", ",", ".", "`", " ")
    private var rgbImage: Bitmap? = null
    private var color: Boolean? = false
    private var quality = 3
    private val qualityColor = 6
    private var response: Spannable? = null
    private var listener: Listener? = null
    private var url: String? = null
    private var wantedWidth: Int? = null

    private fun transformation(wantedWidth: Int) = object : Transformation {

        override fun transform(source: Bitmap): Bitmap {
            val targetWidth = wantedWidth

            val aspectRatio = source.height.toDouble() / source.width.toDouble()
            val targetHeight = (targetWidth * aspectRatio).toInt()
            val result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false)
            if (result != source) {
                // Same bitmap is returned if sizes are the same
                source.recycle()
            }
            return result
        }

        override fun key(): String {
            return "transformation" + " desiredWidth"
        }
    }

    fun bitmap(rgbImage: Bitmap): ImgAscii {
        this.rgbImage = rgbImage
        return this
    }

    fun url(url: String, wantedWidth: Int? = null): ImgAscii {
        this.url = url
        this.wantedWidth = wantedWidth
        return this
    }

    fun quality(quality: AsciiQuality): ImgAscii {
        this.quality = quality.value
        return this
    }

    fun color(color: Boolean?): ImgAscii {
        this.color = color
        return this
    }

    fun convert(listener: Listener) {
        this.listener = listener
        startProcess()
    }

    private fun startProcess() {
        val pic = Picasso.get().load(url)

        if (wantedWidth != null) {
            pic.transform(transformation(wantedWidth!!))
        }

        pic.into(object : com.squareup.picasso.Target {
            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                rgbImage = bitmap
                asyncStuff().start()
            }
        })
    }

    fun asyncStuff() = GlobalScope.launch {
        doInBackAsync().await()
        onFinished()
    }

    private fun doInBackAsync() = GlobalScope.async {
        if (color!!) {
            quality += qualityColor
            if (quality > 5 + qualityColor || quality < 1 + qualityColor)
                quality = 3 + qualityColor
        } else {
            if (quality > 5 || quality < 1)
                quality = 3
        }
        var tx: String
        val span = SpannableStringBuilder()
        val width = rgbImage!!.width
        val height = rgbImage!!.height
        var i = 0
        var y = 0
        while (y < height) {
            var x = 0
            while (x < width) {
                val pixel = rgbImage!!.getPixel(x, y)
                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)
                if (color!!) {
                    tx = "#"
                    span.append(tx)
                    span.setSpan(
                        ForegroundColorSpan(Color.rgb(red, green, blue)),
                        i,
                        i + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else {
                    var brightness = red + green + blue
                    brightness = round((brightness / (765 / (chars.size - 1))).toFloat())
                    tx = chars[brightness]
                    span.append(tx)
                }
                i++
                x += quality
            }
            tx = "\n"
            span.append(tx)
            onProgUpdate(y, height)
            i++
            if (!isActive) break
            y += quality
        }
        response = span
    }

    private fun onProgUpdate(vararg progress: Int?) {
        val current = progress[0]
        val total = progress[1]
        val percentage = 100 * current!! / total!!
        listener!!.onProgress(percentage)
    }

    private fun onFinished() {
        listener!!.onResponse(response)
    }

    interface Listener {
        fun onProgress(percentage: Int)
        fun onResponse(response: Spannable?)
    }

}