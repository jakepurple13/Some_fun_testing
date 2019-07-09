package com.example.myapplication

import android.net.Uri
import androidx.core.net.toUri
import com.example.cardutilities.*
import com.example.funutilities.*
import crestron.com.deckofcards.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import kotlin.random.Random
import kotlin.reflect.KProperty

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(MockitoJUnitRunner::class)
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
        deck += c
        deck2 += c
        deck2 += c
        deck2 += Card(Suit.SPADES, 5)
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
        for (i in 0..100) {
            list += SimTest("$i", "${i * 100}")
        }
        val list1 = arrayListOf<SimTest>()
        for (i in 50..150) {
            list1 += SimTest("$i", "${i * 100}")
        }
        val sim4 = findSimilarities(list, list1, {
            it.link
        }, {
            it.url
        })

        println("list $list")
        println("lis1 $list1")
        println("sim4 $sim4")

        sim4.middleOrNull()

    }

    @Test
    fun netTest() {

        runBlocking {
            //println("$list")
            withContext(Dispatchers.Default) {
                val show = com.example.showapi.ShowApi(com.example.showapi.Source.RECENT_CARTOON)
                val list = show.showInfoList
                val ep = com.example.showapi.EpisodeApi(list[0])
                println(ep.episodeList[0].getVideoLink())
                //println("$list")
            }
        }

        var count = 0
        val s: Int by ByTested {
            println("Here we are at ${count++}")
            5
        }
        println("jhgf")
        println("$s")
        val a: Int by ByTested {
            s + s
        }
        println("$s and $a")

        ByTested {
            println("here")
        }
    }

    @Test
    fun netTest2() {
        for((i,j) in (5..10).withIndex()) {
            println("i:$i and j:$j")
        }
        for(i in (5..10).withIndex()) {
            println("index:${i.index} and value:${i.value}")
        }
        println("0".repeat(1))
        for((i,j) in (Random.nextIntRange(10)).withIndex()) {
            println("i:$i and j:$j")
        }
        println("0".repeat(10))
        for((i,j) in (Random.nextLongRange(10)).withIndex()) {
            println("i:$i and j:$j")
        }
        val locale = Random.nextLocale()
        val lc = Random.nextLowerCaseChar()
        val uc = Random.nextUpperCaseChar()
        println("$locale and $lc and $uc")
    }

    interface ByTest<out T> {
        val value: T
    }

    class ByTested<out T>(override val value: () -> T) : ByTest<Function0<T>> {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value()
    }

    @Test
    fun kotTest() {

        fun isOdd(x: Int) = x % 2 != 0 // == (Int) -> Boolean
        val numbers = listOf(1,2,3)
        println("just isOdd" + numbers.filter(::isOdd))

        fun <A, B, C> compose(f: (B) -> C, g: (A) -> B): (A) -> C {
            return { x -> f(g(x)) }
        }

        fun length(s: String) = s.length

        val oddLength = compose(::isOdd, ::length)
        val strings = listOf("a", "ab", "abc")

        println(strings.filter(oddLength))

        fun <A, B, C, D> composes(f: (C) -> D, g: (B) -> C, h: (A) -> B): (A) -> D {
            return { x -> f(g(h(x))) }
        }

        fun addStuff(s: String) = s

        val oddLengths = composes(::isOdd, ::length, ::addStuff)
        println(strings.filter(oddLengths))

    }

    @Test
    fun netr() {

        val show = com.example.showapi.ShowApi(com.example.showapi.Source.RECENT_ANIME)
        val list = show.showInfoList
        val ep = com.example.showapi.EpisodeApi(list[0])
        println(ep.episodeList[0].getVideoLink())
        println("name " + ep.episodeList[0].name + " and url " + ep.episodeList[0].url)
        println("asdf " + ep.episodeList[0].getVideoInfo()[0].filename!!)

        /*val url = "http://st7.anime1.com/[Erai-raws] Tensei shitara Slime Datta Ken - 25 (Special) [720p][Multiple Subtitle]_af.mp4?st=Q-07POJkaLBsbelZBTv1WQ&e=1562692048"
        val regex = "^[^\\[]+(.*mp4)".toRegex().toPattern().matcher(url)
        while(regex.find()) {
            val s = regex.group(1)!!
            println(s)
        }

        val url2 = ep.episodeList[0].url.toUri().pathSegments
        println(url2)*/


    }

}
