package crestron.com.deckofcards

import junit.framework.TestCase.assertEquals
import org.junit.Test
import kotlin.random.Random
import kotlin.reflect.KProperty

class TestTwo {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, (2 + 2).toLong())
    }

    private fun log(s: String) {
        println("$s\n")
    }

    @Test
    fun test2345() {
        //val f = 1..9
        //System.out.println(5.5 in f)
    }

    @Test
    fun operatorTest() {
        val d = Deck()

        val c = +d
        val c1 = -d

        log("$c and $c1 and ${Random.nextCard()}")

    }

    @Test
    fun partitionTest() {
        Card.cardDescriptor = CardDescriptor.UNICODE_SYMBOL
        val d = Deck()
        val (black, red) = d.partition { it.color == Color.BLACK }
        log("${black.toArrayString()}\n${red.toArrayString()}")
        val (spades, clubs) = black.partition { it.suit == Suit.SPADES }
        val (hearts, diamonds) = red.partition { it.suit == Suit.HEARTS }
        log("${spades.toArrayString()}\n${clubs.toArrayString()}\n${diamonds.toArrayString()}\n${hearts.toArrayString()}")

        class DelegateExample {
            operator fun getValue(thisRef: Any?, prop: KProperty<*>): String {        // 2
                return "$thisRef, thank you for delegating '${prop.name}' to me!"
            }

            operator fun setValue(thisRef: Any?, prop: KProperty<*>, value: String) { // 2
                println("$value has been assigned to ${prop.name} in $thisRef")
            }
        }

        class Example {
            var p: String by DelegateExample()
        }

        val e = Example()

        e.p = "asdf"
        log(e.p)

        val d2 = d.find { it.value > 10 }
        log(d2.toArrayString())
        d.shuffle()
        d.sortBy(compareBy { it.value })
        val getCardP = d.getCard { it == Card(Suit.SPADES, 1) }
        val firstCardValue = d.getFirstCardByValue { it == 11 }
        val lastCardValue = d.getLastCardByValue { it == 11 }
        val firstCardSuit = d.getFirstCardBySuit { it == Suit.SPADES }
        val lastCardSuit = d.getLastCardBySuit { it == Suit.HEARTS }
        val firstCardColor = d.getFirstCardByColor { it == Color.BLACK }
        val lastCardColor = d.getLastCardByColor { it == Color.RED }
        log(
            "$getCardP" +
                    "\n$firstCardValue and $lastCardValue" +
                    "\n$firstCardSuit and $lastCardSuit" +
                    "\n$firstCardColor and $lastCardColor"
        )

        log("${d.removeIf { it.value == 12 }}")

        log(d.toArrayString())
        d.addCard(0, card = Card.RandomCard)
        log(d.toArrayString())

        val d3 = Deck.numberOnly(1..5)
        log(d3.toArrayString())

        val (d4, d5) = Deck().splitDeck()
        log("${d4.toArrayString()} and ${d4.size}\n${d5.toArrayString()} and ${d5.size} and ${d5.isEmpty}")

        log("${d5.first} and ${d5.middle} and ${d5.last}")

        val d6 = Deck(true, 1, 1L, null, null, object : Deck.DeckListener {
            override fun draw(c: Card, size: Int) {
                log("$c and $size")
            }
        })

        log(d6.toArrayString())

        d6 addCards d2.getDeck()

        d6.addCard(Card.RandomCard)

        d6 addCard Card.RandomCard

        log(d6.toArrayString())

        val c = d6 getCard 5

        log("$c")

        log("${d6.getDeck().sumBy { it.value }}")

    }

    @Test
    fun xa() {
        val d = Deck.randomDeck()
        log("$d")
        d(true) {
            it.shuffle()
        }
    }

    @Test
    fun opTesting() {
        Card.cardDescriptor = CardDescriptor.UNICODE_SYMBOL
        val d = Deck()
        log("${arrayListOf(Card(Suit.HEARTS, 3), Card(Suit.SPADES, 3)) in d}")
        d.getCards(arrayListOf(Card(Suit.SPADES, 3), Card(Suit.HEARTS, 3)))
        log("${arrayListOf(Card(Suit.HEARTS, 3), Card(Suit.SPADES, 3)) !in d}")
        d.removeNumber(10)
        log("${d.none { it.value == 10 }}")
        log("${d.any { it.value == 9 }}")
        log(d.toArrayString())
        log((!d).toArrayString())

        fun groupTest(d: Deck) {
            val suitGroup = d.groupBy { it.suit }
            println("$suitGroup")
            val colorGroup = d.groupBy { it.color }
            println("$colorGroup")
            val valueGroup = d.groupBy { it.value }
            println("$valueGroup")
        }

        val d1 = d.getDeck().associateWith { it.suit }
        log("$d1")
        groupTest(d)
        val c1 = d.associateBy { it.value }
        log("$c1")
        d.forEach {
            print("$it, ")
        }
        log("\nCards that have value 9 == ${d.countSpecific { it.value == 9 }}")
        val dr = Deck.randomDeck(100)
        log("${dr.toArrayString()} and size is ${dr.size}")
        val dr1 = Deck.randomDeck()
        log("${dr1.toArrayString()} and size is ${dr1.size}")
        val dd = Deck(true)
        groupTest(dd)

        val deck1 = Deck(false)
        deck1.deckListener = object : Deck.DeckListener {
            override fun draw(c: Card, size: Int) {
                log("$c")
            }
        }
        deck1.sortToReset()
        val deck2 = Deck(true)
        val c = Deck.compareCardLists(deck1.getDeck(), deck2.getDeck()) { card1, c2 ->
            card1.suit == c2.suit
        }
        val c2 = Deck.compareCardLists(deck1.getDeck(), deck2.getDeck())
        log("$c and $c2")
        log("${deck1.toArrayString()} \n ${deck2.toArrayString()}")
        deck1-=Card.RandomCard
        deck1+=Card.RandomCard
        val c3 = Deck.compareCardLists(deck1, deck2)
        log("$c3")
        log("${deck1.toArrayString()} \n ${deck2.toArrayString()}")

        fun highFunTest(deck: Deck, deckAction: (Card) -> Unit) {
            for (i in deck) {
                deckAction(i)
            }
        }
        print("[")
        highFunTest(deck2) {
            print("$it, ")
        }
        print("]\n")

        val deckAction: (Card) -> Unit = { println("$it") }
        deckAction(Card(Suit.SPADES, 4))

        val c5 = deck1.compareToDeck(deck2) { card1, card2 ->
            card1.suit == card2.suit
        }
        log("$c5")
        log("${deck1.toArrayString()} \n ${deck2.toArrayString()}")

        val aS = Card(Suit.SPADES, 1)
        val kD = Card(Suit.DIAMONDS, 13)
        deck1.replaceCard(aS, !aS) {
            it.size-1 downTo 0
        }
        deck1.replaceAllCards(!aS, aS)

        deck1.replaceCard(kD, !kD) {
            26..40
        }

        log(deck1.toArrayString())


        fun printCards(deckToPrint: Deck, range: ((Deck) -> IntProgression)? = null) {
            print("[")
            val cd = range ?: { deck: Deck -> 0 until deck.size }
            for(i in cd(deckToPrint)) {
                print("$i=${deckToPrint[i]}, ")
            }
            print("]\n")
        }

        printCards(deck1)
        printCards(deck1) { 26..40 }

        log(deck1.toCustomString { card, num -> "$num=${card.toPrettyString()}\t" })

        val deck3 = Deck(false)
        val valueDecks = deck3.groupBy { it.suit }
        println("$valueDecks")
        val spades = Deck(cards = valueDecks[Suit.SPADES], numberOfDecks = 0)
        val clubs = Deck(cards = valueDecks[Suit.CLUBS], numberOfDecks = 0)
        val diamonds = Deck(cards = valueDecks[Suit.DIAMONDS], numberOfDecks = 0)
        val hearts = Deck(cards = valueDecks[Suit.HEARTS], numberOfDecks = 0)

        val statementString = { card: Card, i: Int -> "$i=$card\t" }
        log(spades.toCustomString(statementString))
        log(clubs.toCustomString(statementString))
        log(diamonds.toCustomString(statementString))
        log(hearts.toCustomString(statementString))

        log("-------------------------")

        hearts[5..8] = spades[2..5]
        clubs[Card(Suit.CLUBS, 5)] = Card(Suit.DIAMONDS, 5)
        log("${clubs[Card(Suit.CLUBS, 5)]}")
        log("${clubs[Card(Suit.DIAMONDS, 5)]}")

        log(spades.toCustomString(statementString))
        log(clubs.toCustomString(statementString))
        log(diamonds.toCustomString(statementString))
        log(hearts.toCustomString(statementString))

    }

    @Test
    fun higherOrderTesting() {
        val repeatFun: String.(Int) -> String = { times -> this.repeat(times) }
        val twoParameters: (String, Int) -> String = repeatFun // OK

        fun runTransformation(f: (String, Int) -> String): String {
            return f("hello", 3)
        }

        val result = runTransformation(repeatFun) // OK
        val result1 = twoParameters("world", 3)

        println("result = $result\nresult1 = $result1")
    }

    @Test
    fun dTest() {
        val d = Deck()

        //d-=nextCard(Suit.SPADES, 5)

        val c = d.getCardLocation(Card(Suit.SPADES, 5))
        log("${Card(Suit.SPADES, 5)} is in the $c place of the deck")

        val dq = Deck.deck {
            addNormalDeck()
            card(Card.RandomCard)
            cards(Deck.colorOnly(Color.BLACK).getDeck())
            build()
        }

        dq.draw()

        dq.deckListener = object : Deck.DeckListener {
            override fun draw(c: Card, size: Int) {
                log("$c")
            }
        }

        dq addDeck Deck(shuffler = true)
        dq += Card.RandomCard
    }

    @Test
    fun kotlinFunTest() {
        val numbers = listOf("one", "two", "three", "four")
        val numbersSequence = numbers.asSequence()
        log("$numbersSequence")
    }

    @Test
    @Throws(CardNotFoundException::class)
    fun randomTest() {

        log("${Card.RandomCard}")

        var d = Deck()

        log(d.toString())
        log(d.toNormalString())
        log(d.toSymbolString())
        log(d.toPrettyString())
        log(d.toArrayString())
        log(d.toArrayNormalString())
        log(d.toArraySymbolString())
        log(d.toArrayPrettyString())

        Card.cardDescriptor = CardDescriptor.UNICODE_SYMBOL

        d.deckListener = object : Deck.DeckListener {
            override fun draw(c: Card, size: Int) {
                log("$c and $size")
            }
        }

        log("Random nextCard is ${d.randomCard}")
        log("Random nextCard is ${d.randomCard}")
        log("Random nextCard is ${d.randomCard}")
        log("Random nextCard is ${d.randomCard}")
        log("Random nextCard is ${d.getCard(6)}")
        d += Card.RandomCard
        d += arrayListOf<Card>().apply {
            add(Card.RandomCard)
            add(Card.RandomCard)
            add(Card.RandomCard)
            add(Card.randomCardByColor(Color.BLACK))
            add(Card.randomCardBySuit(Suit.SPADES))
            add(Card.randomCardByValue(5))
        }
        d += Deck()
        Card.ClearCard
        d += Deck.deck {
            card {
                suit = Suit.randomSuit()
                value = CardUtil.randomNumber(1, 13)
            }
            addNormalDeck()
            card(Card.RandomCard)
        }

        log("${d.randomCard?.compareSuit(Card.RandomCard)}")

        d.clear()

        d = Deck()

        d.deckListener = object : Deck.DeckListener {
            override fun draw(c: Card, size: Int) {
                log("$c and $size")
            }
        }

        CardDescriptor.setRandomDescriptor()

        log(d.toArrayString())
        d.trueRandomShuffle()
        log(d.toArrayString())
        d.sortToReset()
        log(d.toArrayString())
        d.trueRandomShuffle(3)
        log(d.toArrayString())
        d.sortToReset()
        d.trueRandomShuffle(3)
        log(d.toArrayString())

        /*
        var total = 0
        for(c in d) {
            total+=c.value
        }
        log("$total") //total == 364
        */

        val x = Card.RandomCard + Card.RandomCard
        log("$x")

    }

    @Test
    @Throws(CardNotFoundException::class)
    fun deckTest() {
        var d = Deck()

        println(d.removeColor(Color.BLACK))

        println(d.removeSuit(Suit.HEARTS))

        println(d.removeNumber(6))

        println(d)

        log("Deck without black, hearts, and 6: $d")
        val deck = Deck()
        d += deck.getDeck()
        log("Deck plus another deck: $d")
        d += Card(Suit.SPADES, 1)
        log("Deck plus Ace of Spades: $d")
        d -= Suit.SPADES
        log("Deck minus spades: $d")
        d -= Color.RED
        log("Deck minus red: $d")
        log("5 cards from deck: ${(d - 5)}")
        log("One nextCard from deck: ${(d - 5f)}")
        d *= 2
        log("Deck size is ${d.size} and : $d")
        d /= 2
        log("Deck size is ${d.size} and : $d")

        d = Deck()

        log("Random nextCard is ${d.randomCard}")
        log("Random nextCard is ${d.randomCard}")
        log("Random nextCard is ${d.randomCard}")
        log("Random nextCard is ${d.randomCard}")
        log("Random nextCard is ${d.getCard(6)}")

        d = Deck()

        log("${d.draw() + d.draw()}")
        var c = d.draw()
        log("$c")
        log("${d.draw() > d.draw()}")
        log("${d.draw() < d.draw()}")
        log("${d.draw() >= d.draw()}")
        log("${d.draw() <= d.draw()}")
        log("${d.draw() == d.draw()}")
        log("${d.draw() + d.draw()}")
        log("${d.draw() - d.draw()}")

        d = Deck()

        c = Card(Suit.SPADES, 1)

        log("${c in d}")
        log("${c !in d}")
        log("${d.contains(c)}")
        log("${!d.contains(c)}")

        d = Deck()
        d *= 2
        d -= Suit.DIAMONDS
        d -= Suit.CLUBS
        d -= Suit.HEARTS

        log("New deck is: $d and the size is ${d.size}")
        d.shuffle()
        log("New deck is: $d and the size is ${d.size}")

        /*val hand = Hand()

        infix fun Hand.deal(num: Int) {
            deck.dealHand(this, num)
        }

        hand deal 5

        log("$hand")*/

        val cd = d.getCard(5)
        log("$cd")
        d addDeck deck
        log("1 New deck is: $d and the size is ${d.size}")
        d addDecks 2
        log("2 New deck is: $d and the size is ${d.size}")
        d removeDecks 1
        log("3 New deck is: $d and the size is ${d.size}")
        for (i in d[2, 6]) {
            log("Range [2,6] $i")
        }

        log("Ranged[2..6] only ${d[2..6]}")

        log("Suits only ${d[Suit.SPADES, Suit.DIAMONDS]}")

        log("Suits only ${d[Suit.SPADES]}")

        log("Color only ${d[Color.BLACK]}")

        log("Color only ${d[Color.BACK]}")

        d.sortByColor()
        log("Sort color ${d.toArrayString()}")
        d.shuffle()
        d.sortByValue()
        log("Sort value ${d.toArrayString()}")
        d.shuffle()
        d.sortBySuit()
        log("Sort suit ${d.toArrayString()}")
        d.shuffle()
        d.sortByValue()
        d.sortBySuit()
        log("Sort value and suit ${d.toArrayString()}")
        log("Cards ${d drawCards 4}")
        d /= 1
        d /= 1
        d /= 1
        log("deck $d")
        d += Suit.SPADES
        log("deck ${d.toArrayString()}")
        d += Color.RED
        log("deck ${d.toArrayString()}")

        log("First Color ${d.getFirstCardByColor(Color.RED)}")
        log("First Suit ${d.getFirstCardBySuit(Suit.SPADES)}")
        log("First Value ${d.getFirstCardByValue(2)}")
        log("Last Color ${d.getLastCardByColor(Color.BLACK)}")
        log("Last Suit ${d.getLastCardBySuit(Suit.SPADES)}")
        log("Last Value ${d.getLastCardByValue(2)}")

        d.getCards(4)
        d.getCard(Suit.SPADES, 7)
        d.addCards(Deck().getDeck())
        d.getCardLocation(Card(Suit.DIAMONDS, 6))
        d.reverse()

        d.draw().compareColor(Card(Suit.DIAMONDS, 4))

    }

    @Test
    fun zxcv() {

        loopi@ for (i in 1..3) {
            loopj@ for (j in 5..7) {
                if (i == 2 /*&& j == 6*/) break@loopj
                print((i * 100) + j)
                print(" ")
            }
            println("$i loop ends")
        }

        println("We are done")

        loopi@ for (i in 1..3) {
            for (j in 5..7) {
                if (i == 2 && j == 6) continue@loopi
                print((i * 100) + j)
                print(" ")
            }
            println("$i loop ends")
        }

        println("We are done")

        fun foo() {
            listOf(1, 2, 3, 4, 5).forEach lit@{
                if (it == 3) return@lit // non-local return directly to the caller of foo()
                print(it)
            }
            println("this point is unreachable")
        }

        foo()

        val bytes = 0b11010010_01101001_10010100_10010010

        log("$bytes")

        val secondProperty = "Second property: $bytes".also(::println)

        log(secondProperty)

        val answer by lazy {
            println("Calculating the answer...")
            42
        }

        println("The answer is $answer.")

        class Suits {

            inline fun <reified T : Enum<T>> printAllValues() {
                print(enumValues<T>().joinToString { it.name })
            }

        }

        Suits().printAllValues<Suit>()

        val lazyValue: String by lazy {
            println("computed!")
            "Hello"
        }
        println(lazyValue)
        println(lazyValue)

        /*class User(val map: Map<String, Any?>? = null) {
            var name: String by Delegates.observable("<no name>") { _, old, new ->
                println("$old -> $new")
            }

            val name1: String by map
            val age: Int by map
        }

        val user1 = User(mapOf("name" to "John Doe", "age" to 25))
        log(user1.map!!.toString())

        val user = User()
        user.name = "first"
        user.name = "second"
        log(user.name)
        log(user.name1)
        log("${user.age}")*/

        val items = listOf(1, 2, 3, 4, 5)
        // Lambdas are code blocks enclosed in curly braces.
        items.fold(0) { acc: Int, i: Int ->
            print("acc = $acc, i = $i, ")
            val result = acc + i
            println("result = $result")
            result
        }
        // Parameter types in a lambda are optional if they can be inferred:
        val joinedToString = items.fold("Elements:") { acc, i -> "$acc $i" }
        // Function references can also be used for higher-order function calls:
        val product = items.fold(1, Int::times)
        //sampleEnd
        println("joinedToString = $joinedToString")
        println("product = $product")

        var decked = Deck.suitOnly(Suit.SPADES, Suit.HEARTS)

        println("${decked.getDeck()}")

        decked = Deck.numberOnly(2, 3, 4)

        log(decked.toArrayString())

        decked = Deck.colorOnly(Color.BLACK)

        log(decked.toArrayString())

        decked = Deck(deck = Deck())

        log(decked.toArrayString())

        decked = Deck(cards = arrayListOf<Card>().apply {
            add(Card.RandomCard)
        })

        log(decked.toArrayString())

        decked = Deck(deck = Deck())

        log(decked.toArrayString())

        decked = Deck(numberOfDecks = 2)

        log(decked.toArrayString())

        decked = Deck(numberOfDecks = 2, shuffler = true)

        log(decked.toArrayString())

    }

}