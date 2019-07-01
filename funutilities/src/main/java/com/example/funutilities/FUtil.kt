package com.example.funutilities

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.IntRange
import java.util.*
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

fun ImageView.setColor(color: Color, mode: PorterDuff.Mode = PorterDuff.Mode.SCREEN) {
    this.setColorFilter(color.toArgb(), mode)
}

fun ImageView.setColor(color: Int, mode: PorterDuff.Mode = PorterDuff.Mode.SCREEN) {
    this.setColorFilter(color, mode)
}

fun ImageView.setColor(
    @IntRange(from = 0, to = 255) alpha: Int = 255,
    @IntRange(from = 0, to = 255) red: Int,
    @IntRange(from = 0, to = 255) green: Int,
    @IntRange(from = 0, to = 255) blue: Int,
    mode: PorterDuff.Mode = PorterDuff.Mode.SCREEN
) {
    this.setColorFilter(Color.argb(alpha, red, green, blue), mode)
}

/**
 * @param list1 the first list
 * @param list2 the second list
 * @param predicate1 filter the second list
 * @param predicate2 filter the first list by what the second list has
 */
fun <T, R> findSimilarities(
    list1: List<T>,
    list2: List<T>,
    predicate1: (T) -> R,
    predicate2: (T) -> R
): List<T> {
    val aColIds = list2.asSequence().map(predicate1).toSet()
    return list1.filter { predicate2(it) in aColIds }
}

/**
 * returns a random color
 */
fun Random.nextColor(
    @IntRange(from = 0, to = 255) alpha: Int = nextInt(0, 255),
    @IntRange(from = 0, to = 255) red: Int = nextInt(0, 255),
    @IntRange(from = 0, to = 255) green: Int = nextInt(0, 255),
    @IntRange(from = 0, to = 255) blue: Int = nextInt(0, 255)
): Int = Color.argb(alpha, red, green, blue)

object RandomCharPool {

    internal val charPool: MutableList<Char> =
        (('a'..'z') + ('A'..'Z') + ('0'..'9')).toMutableList()

    private fun addToCharPool(char: MutableList<Char>) {
        val lists = findSimilarities(char, charPool, { it }, { it })
        char.removeAll(lists)
        charPool.addAll(char)
    }

    /**
     * adds characters to the pool of characters for [nextString] and [nextChar]
     */
    fun addToCharPool(vararg char: Char) {
        addToCharPool(char.toMutableList())
    }

    /**
     * adds characters to the pool of characters for [nextString] and [nextChar]
     */
    fun addToCharPool(char: Collection<Char>) {
        addToCharPool(char.toMutableList())
    }

    /**
     * removes characters from the pool of characters for [nextString] and [nextChar]
     */
    fun removeFromCharPool(vararg char: Char) {
        charPool.removeAll(char.toList())
    }

    /**
     * removes characters from the pool of characters for [nextString] and [nextChar]
     */
    fun removeFromCharPool(char: Collection<Char>) {
        charPool.removeAll(char.toList())
    }

    /**
     * resets the pool of characters for [nextString] and [nextChar]
     */
    fun resetCharPool() {
        charPool.clear()
        charPool.addAll(('a'..'z') + ('A'..'Z') + ('0'..'9'))
    }
}

/**
 * returns a random string based on length
 * to modify what else is/is not allowed in the random char pool, @see [RandomCharPool]
 */
fun Random.nextString(@IntRange(from = 0) length: Int): String {
    var string = ""
    for (i in 1..length) {
        string += RandomCharPool.charPool[nextInt(0, RandomCharPool.charPool.size)]
    }
    return string
}

/**
 * returns a random char
 * to modify what else is/is not allowed in the random char pool, @see [RandomCharPool]
 */
fun Random.nextChar(): Char {
    return RandomCharPool.charPool.random()
}

fun Random.nextLocale(): Locale {
    return Locale.getAvailableLocales().random()
}