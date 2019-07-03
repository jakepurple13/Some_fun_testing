package com.example.myapplication

import com.example.cardutilities.*
import com.example.funutilities.findSimilarities
import crestron.com.deckofcards.*
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random
import kotlin.reflect.KProperty

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

    @Test
    fun testeringest() {

        Card.cardDescriptor = CardDescriptor.UNICODE_SYMBOL

        val deck = Deck()
        val deck2 = Deck.randomDeck()
        //deck+=deck2
        val c = Card.RandomCard
        deck+=c
        deck2+=c
        deck2+=c
        deck2+=Card(Suit.SPADES, 5)
        deck.sortToReset()
        deck2.sortToReset()

        val similarities = findSimilarities(deck.getDeck(), deck2.getDeck(), {
            it
        }, {
            it
        })

        val similarities2 = findSimilarities(deck2.getDeck(), deck.getDeck(), {
            it
        }, {
            it
        })

        val similarities3 = findSimilarities(deck.getDeck(), deck2.getDeck(), {
            it
        }, {
            it
        })

        println("Dec1 ${deck.toArrayPrettyString()}")
        println("Dec2 ${deck2.toArrayPrettyString()}")
        println("Sim1 $similarities")
        println("Sim2 $similarities2")
        println("Sim3 $similarities3")

        class SimTest(val link: String, val url: String) {
            override fun toString(): String {
                return "$link and $url"
            }
        }

        val list = arrayListOf<SimTest>()
        for(i in 0..100) {
            list+=SimTest("$i", "${i*100}")
        }
        val list1 = arrayListOf<SimTest>()
        for(i in 50..150) {
            list1+=SimTest("$i", "${i*100}")
        }
        val sim4 = findSimilarities(list, list1, {
            it.link
        }, {
            it.url
        })

        println("list $list")
        println("lis1 $list1")
        println("sim4 $sim4")

    }

    @Test
    fun netTest() {
        //val show = ShowApi(Source.RECENT_CARTOON)
        //val list = show.showInfoList
        //println("$list")
        var count = 0
        val s: Int by ByTested {
            println("Here we are at ${count++}")
            5
        }
        println("jhgf")
        println("$s")
        val a: Int by ByTested {
            s+s
        }
        println("$s and $a")

        ByTested {
            println("here")
        }
    }

    interface ByTest<out T> {
        val value: T
    }

    class ByTested<out T>(override val value: () -> T) : ByTest<Function0<T>> {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value()
    }

}
