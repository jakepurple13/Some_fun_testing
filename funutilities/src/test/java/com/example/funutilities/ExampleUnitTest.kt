package com.example.funutilities

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
        println("Test 1 ${Random.nextString(62)}")
        for(i in 0..10) {
            println("Test ${i+2} ${Random.nextString(62)}")
        }
        RandomCharPool.resetCharPool()
        RandomCharPool.removeFromCharPool((('a'..'g') + ('A'..'G')).toList())
        RandomCharPool.removeFromCharPool('l')
        RandomCharPool.addToCharPool('c', 'g')
        RandomCharPool.addToCharPool(('F'..'L').toList())

        for(i in 0..10) {
            println("Test ${i+13} ${Random.nextString(123)}")
        }

        Random.nextColor(red = 255)

    }
}
