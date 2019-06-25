package com.example.myapplication

import com.example.cardutilities.*
import crestron.com.deckofcards.Card
import crestron.com.deckofcards.Deck
import crestron.com.deckofcards.nextCard
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

        val d = Deck()
        val a = d.getDeck()
        var c = Card.RandomCard
        a.addAll(d.removeCards(5))
        a.addWithAction(Card.RandomCard) {
            println("$it")
        }
        a.removeWithAction(4) {
            c = it
        }
        println("1 $c")
        val c1 = c++
        println("2 $c")
        val c2 = c--
        println("3 $c")
        println("4 $c1")
        println("5 $c2")
        println("6 $c")
        println("7 $c")
        val c3 = +c
        println("8 $c")
        val c4 = -c
        println("9 $c")
        println("10 $c3")
        println("11 $c4")
        val c5 = Random.nextCard()
        println("Prev ${c5.previousCard()}")
        println("Card $c5")
        println("Next ${c5.nextCard()}")

    }
}
