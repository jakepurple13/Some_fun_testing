package com.example.funutilities

import android.graphics.Color
import android.graphics.PorterDuff
import android.widget.ImageView
import androidx.annotation.IntRange
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import java.util.*
import kotlin.random.Random

fun ImageView.setColor(color: Color, mode: PorterDuff.Mode = PorterDuff.Mode.SCREEN) {
    this.setColor(color.toArgb(), mode)
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
    this.setColor(Color.argb(alpha, red, green, blue), mode)
}

/**
 * @param list the second list
 * @param predicate1 filter the second list
 * @param predicate2 filter the first list by what the second list has
 */
fun <T, R> List<T>.findSimilarities(
    list: List<T>,
    predicate1: (T) -> R,
    predicate2: (T) -> R
): List<T> {
    val aColIds = list.asSequence().map(predicate1).toSet()
    this.distinctBy { it }
    return this.filter { predicate2(it) in aColIds }
}

fun <T, U> List<T>.intersect(uList: List<U>, filterPredicate : (T, U) -> Boolean) = filter { m -> uList.any { filterPredicate(m, it)} }

fun Color.getComplimentaryColor(): Int {
    val rgbMAX = 255
    return Color.argb(
        rgbMAX - alpha(),
        rgbMAX - red(),
        rgbMAX - green(),
        rgbMAX - blue()
    )
}

fun Int.getComplimentaryColor(): Int {
    val rgbMAX = 255
    return Color.argb(
        rgbMAX - alpha,
        rgbMAX - red,
        rgbMAX - green,
        rgbMAX - blue
    )
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

/**
 * returns a random [IntRange]
 */
fun Random.nextIntRange(until: Int = Int.MAX_VALUE): kotlin.ranges.IntRange {
    val first = nextInt(until = until)
    val second = nextInt(first, until)
    return first..second
}

/**
 * returns a random [IntRange]
 */
fun Random.nextLongRange(until: Long = Long.MAX_VALUE): LongRange {
    val first = nextLong(until = until)
    val second = nextLong(first, until)
    return first..second
}

object RandomCharPool {

    internal val charPool: MutableList<Char> =
        (('a'..'z') + ('A'..'Z') + ('0'..'9')).toMutableList()

    private fun addToCharPool(char: MutableList<Char>) {
        val lists = char.findSimilarities(charPool, { it }, { it })
        char.removeAll(lists)
        charPool.addAll(char)
        charPool.sortBy { it }
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
fun Random.nextString(@IntRange(from = 1) length: Int): String {
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
    return RandomCharPool.charPool.random(this)
}

/**
 * returns a random uppercase char
 * to modify what else is/is not allowed in the random char pool, @see [RandomCharPool]
 */
fun Random.nextUpperCaseChar(): Char {
    return ('A'..'Z').random(this)
}

/**
 * returns a random lowercase char
 * to modify what else is/is not allowed in the random char pool, @see [RandomCharPool]
 */
fun Random.nextLowerCaseChar(): Char {
    return ('a'..'z').random(this)
}

/**
 * returns a random [Locale]
 */
fun Random.nextLocale(): Locale {
    return Locale.getAvailableLocales().random(this)
}

fun <T> List<T>.middle(): T {
    return get(size / 2)
}

fun <T> List<T>.middleOrNull(): T? {
    return getOrNull(size / 2)
}
