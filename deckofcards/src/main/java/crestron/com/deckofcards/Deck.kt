package crestron.com.deckofcards

import android.annotation.TargetApi
import android.os.Build
import java.util.*
import kotlin.collections.ArrayList

/**
 * The Class Deck.
 */
class Deck {

    //var
    /**
     * the actual list of cards
     */
    private var deckOfCards: ArrayList<Card> = ArrayList()

    /**
     * the listener for the deck
     */
    var deckListener: DeckListener? = null

    /**
     * thrown if you try to draw from an empty deck
     */
    private val emptyDeck = CardNotFoundException("Deck is Empty")

    /**
     * The size of the deck.
     *
     * @return The size of the deck (int)
     */
    val size: Int
        get() = deckOfCards.size

    /**
     * if the deck is empty
     *
     * @return true if the deck is empty
     */
    val isEmpty: Boolean
        get() = deckOfCards.isEmpty()

    /**
     * Draws a random card from the deck.
     *
     * @return Card
     */
    val randomCard: Card?
        get() = deckOfCards.getOrNull(CardUtil.randomNumber(0, size - 1))

    /**
     * the first card (equivalent to [draw] but without removing)
     * (Also the top of the deck)
     */
    val first: Card?
        get() = deckOfCards.firstOrNull()

    /**
     * the card directly in the middle of the deck (without removing)
     */
    val middle: Card?
        get() = deckOfCards.getOrNull(size / 2)

    /**
     * the last card (bottom of the deck) ((without removing))
     */
    val last: Card?
        get() = deckOfCards.lastOrNull()

    //builders
    class DeckBuilder {

        private val cards = mutableListOf<Card>()

        fun card(block: CardBuilder.() -> Unit) {
            cards.add(CardBuilder().apply(block).build())
        }

        fun build(): Deck = Deck(cards)

        /**
         * add an entire 52 card deck
         */
        fun addNormalDeck() {
            cards.addAll(Deck().deckOfCards)
        }

        /**
         * add a card with #suit and a #value
         */
        fun card(suit: Suit, value: Int) {
            cards.add(Card(suit, value))
        }

        fun cards(cardList: Collection<Card>) {
            cards.addAll(cardList)
        }

        /**
         * add a card
         */
        fun card(c: Card) {
            cards.add(c)
        }

    }

    class CardBuilder {

        var suit: Suit = Suit.SPADES
        var value = 1
        var card: Card? = null

        fun build(): Card {
            return card ?: Card(suit, value)
        }

    }

    companion object Builder {

        /**
         * Create a deck via DSL
         */
        fun deck(block: DeckBuilder.() -> Unit): Deck = DeckBuilder().apply(block).build()

        /**
         * Builds a deck of only [suit]
         * @return a Deck of [suit]
         */
        fun suitOnly(vararg suit: Suit): Deck {
            val d = Deck()
            d.initialize(Suit.SPADES in suit, Suit.CLUBS in suit, Suit.DIAMONDS in suit, Suit.HEARTS in suit)
            return d
        }

        /**
         * Builds a deck of only [num]s in all suits
         * @return a Deck of [num]s
         */
        fun numberOnly(vararg num: Int): Deck {
            val d = Deck()
            for (i in num) {
                d += Card(Suit.SPADES, i)
                d += Card(Suit.CLUBS, i)
                d += Card(Suit.DIAMONDS, i)
                d += Card(Suit.HEARTS, i)
            }
            return d
        }

        /**
         * Builds a deck of only [num]s
         * @return a Deck of [num]s
         */
        @Throws(CardNotFoundException::class)
        fun numberOnly(num: IntRange): Deck {
            if (num.first < 1 || num.last > 13) {
                throw CardNotFoundException("Range is not allowed")
            }
            val d = Deck()
            for (i in num) {
                d += Card(Suit.SPADES, i)
                d += Card(Suit.CLUBS, i)
                d += Card(Suit.DIAMONDS, i)
                d += Card(Suit.HEARTS, i)
            }
            return d
        }

        /**
         * Builds a deck of only [color]
         * @return a Deck of [color]
         */
        @Throws(CardNotFoundException::class)
        fun colorOnly(color: Color): Deck {
            val d = Deck()
            when (color) {
                Color.BLACK -> d.initialize(spades = true, clubs = true, diamonds = false, hearts = false)
                Color.RED -> d.initialize(spades = false, clubs = false, diamonds = true, hearts = true)
                Color.BACK -> throw CardNotFoundException("Cannot Find Back Card")
            }
            return d
        }

        /**
         * creates a [cardAmount]-card deck with random cards
         * [cardAmount] default is 52
         */
        fun randomDeck(cardAmount: Int = 52): Deck {
            val d = Deck()
            for (i in 0 until cardAmount)
                d += Card.RandomCard
            return d
        }

        /**
         * purely for the compareCardLists methods
         * for code clean up so these lines aren't repeated twice
         */
        private fun comparingMethod(
            list1: Collection<Card>,
            list2: Collection<Card>,
            check: (Card, Card) -> Boolean = { c1: Card, c2: Card -> c1.value == c2.value }
        ): Boolean {

            val cardList1 = Deck(cards = list1)
            val cardList2 = Deck(cards = list2)

            cardList1.sortToReset()
            cardList2.sortToReset()

            for (l in 0 until list1.size) {
                if (!check(cardList1[l], cardList2[l]))
                    return false
            }
            return true
        }

        /**
         * compares two lists of cards
         * sorts them to resetting (A-K SPADES, A-K CLUBS, A-K DIAMONDS, A-K HEARTS)
         * default comparison is value==value
         */
        fun compareCardLists(
            list1: Collection<Card>,
            list2: Collection<Card>,
            check: (Card, Card) -> Boolean = { c1: Card, c2: Card -> c1.value == c2.value }
        ): Boolean {
            if (list1.size != list2.size) {
                return false
            }

            return comparingMethod(list1, list2, check)
        }

        /**
         * compares two decks
         * sorts them to resetting (A-K SPADES, A-K CLUBS, A-K DIAMONDS, A-K HEARTS)
         * default comparison is value==value
         */
        fun compareCardLists(
            list1: Deck,
            list2: Deck,
            check: (Card, Card) -> Boolean = { c1: Card, c2: Card -> c1.value == c2.value }
        ): Boolean {
            if (list1.size != list2.size) {
                return false
            }

            return comparingMethod(list1.getDeck(), list2.getDeck(), check)
        }

    }

    //Constructors
    /**
     * A Deck of Cards
     *
     * @param shuffler true if the deck should be shuffled
     * @param numberOfDecks the number of decks to have
     * @param seed the seed to use if shuffled
     * @param deckListener the listener for shuffling and drawing
     */
    constructor(
        shuffler: Boolean = false,
        numberOfDecks: Int = 1,
        seed: Long? = null,
        deckListener: DeckListener? = null
    ) {
        for (i in 0 until numberOfDecks) {
            initialize()
        }
        this.deckListener = deckListener
        if (shuffler)
            shuffle(seed)
    }

    /**
     * @param shuffler if you want to shuffle the deck [Default is false]
     * @param numberOfDecks how many decks you want [Default is 1]
     * @param seed the seed of the shuffle [Default is null]
     * @param cards if you want to add some cards to the deck at first [Default is null]
     * @param deck if you want to add a deck to this deck [Default is null]
     * @param deckListener if you want to add a decklistener to this deck [Default is null]
     */
    constructor(
        shuffler: Boolean = false,
        numberOfDecks: Int = 1,
        seed: Long? = null,
        cards: Collection<Card>? = null,
        deck: Deck? = null,
        deckListener: DeckListener? = null
    ) {
        for (i in 0 until numberOfDecks) {
            initialize()
        }

        if (cards != null)
            deckOfCards.addAll(cards)

        if (deck != null)
            deckOfCards.addAll(deck.deckOfCards)

        if (shuffler)
            shuffle(seed)

        this.deckListener = deckListener
    }

    /**
     * A Deck of Cards, unshuffled.
     * A blank deck with no cards at first
     */
    private constructor()

    constructor(cards: Collection<Card>, deckListener: DeckListener? = null) {
        deckOfCards.addAll(cards)
        this.deckListener = deckListener
    }

    constructor(deck: Deck, deckListener: DeckListener? = null) {
        deckOfCards.addAll(deck.deckOfCards)
        this.deckListener = deckListener
    }

    /**
     * A Deck of Cards, unshuffled.
     *
     * @param numberOfDecks number of decks in one deck
     */
    constructor(numberOfDecks: Int) {
        for (i in 0 until numberOfDecks) {
            initialize()
        }
    }

    /**
     * A Deck of Cards, shuffled.
     *
     * @param numberOfDecks   number of decks in one deck
     * @param shuffler the shuffler
     */
    constructor(numberOfDecks: Int, shuffler: Boolean = false) {
        for (i in 0 until numberOfDecks) {
            initialize()
            if (shuffler)
                shuffle()
        }
        if (shuffler)
            shuffle()
    }

    /**
     * A Deck of Cards, shuffled.
     *
     * @param shuffler the shuffler
     */
    constructor(shuffler: Boolean = false) {
        initialize()
        if (shuffler)
            shuffle()
    }

    //operator overloading
    /**
     * checks to see if the Deck has the card c
     * @param c the card to check for
     */
    operator fun contains(c: Card): Boolean = c in deckOfCards

    operator fun contains(suit: Suit): Boolean = deckOfCards.any { it.suit == suit }

    operator fun contains(color: Color): Boolean = deckOfCards.any { it.color == color }

    operator fun contains(num: Int): Boolean = deckOfCards.any { it.value == num }

    operator fun contains(cards: Collection<Card>): Boolean = deckOfCards.containsAll(cards)

    /**
     * adds card to deck
     * @param c the card to add
     */
    operator fun plusAssign(c: Card) = addCard(c)

    /**
     * adds a list of cards to the deck
     * @param c the cards to add
     */
    operator fun plusAssign(c: Collection<Card>) = addCards(c)

    /**
     * adds another deck to this deck
     */
    operator fun plusAssign(d: Deck) = addCards(d.deckOfCards)

    /**
     * adds the wanted suit
     */
    operator fun plusAssign(suit: Suit) {
        when (suit) {
            Suit.HEARTS -> initialize(spades = false, clubs = false, diamonds = false, hearts = true)
            Suit.DIAMONDS -> initialize(spades = false, clubs = false, diamonds = true, hearts = false)
            Suit.CLUBS -> initialize(spades = false, clubs = true, diamonds = false, hearts = false)
            Suit.SPADES -> initialize(spades = true, clubs = false, diamonds = false, hearts = false)
        }
    }

    /**
     * adds the wanted color
     *
     */
    operator fun plusAssign(color: Color) {
        when (color) {
            Color.BLACK -> initialize(spades = true, clubs = true, diamonds = false, hearts = false)
            Color.RED -> initialize(spades = false, clubs = false, diamonds = true, hearts = true)
            Color.BACK -> throw CardNotFoundException("Cannot Find Back Card")
        }
    }

    /**
     * removes a card from the deck
     */
    operator fun minusAssign(c: Card) {
        getCard(c)
    }

    /**
     * removes all suit of s from this deck
     */
    operator fun minusAssign(s: Suit) {
        removeSuit(s)
    }

    /**
     * removes all color of color from this deck
     */
    operator fun minusAssign(color: Color) {
        removeColor(color)
    }

    /**
     * removes num number of cards from this deck
     */
    operator fun minus(num: Int): Collection<Card> = getCards(num)

    /**
     * removes the num card from this deck
     */
    operator fun minus(num: Float): Card = getCard(num.toInt())

    /**
     * gets the cards between start and end
     */
    operator fun get(start: Int, end: Int): Collection<Card> = deckOfCards.subList(start, end)

    /**
     * gets all of the cards in [range]
     * @return a [Collection] of cards that are in the [range]
     */
    operator fun get(range: IntRange): Collection<Card> = deckOfCards.slice(range)

    /**
     * gets the card at the num index
     */
    operator fun get(num: Int): Card = deckOfCards[num]

    /**
     * gets all of the colors of [color]
     * @return a [Collection] of cards that are [color]s
     */
    operator fun get(vararg color: Color): Collection<Card> = deckOfCards.filter { color.contains(it.color) }

    /**
     * gets all of the suits of [suit]
     * @return a [Collection] of cards that are [suit]s
     */
    operator fun get(vararg suit: Suit): Collection<Card> = deckOfCards.filter { suit.contains(it.suit) }

    operator fun get(card: Card): Int = getCardLocation(card)

    /**
     * sets a card
     */
    operator fun set(int: Int, card: Card) {
        deckOfCards[int] = card
    }

    /**
     * sets a range of cards
     */
    operator fun set(intRange: IntRange, cards: Collection<Card>) {
        val intSize = intRange.last - intRange.first + 1
        if(intSize!=cards.size) {
            return
        }
        var counter = 0
        for(i in intRange) {
            deckOfCards[i] = cards.elementAt(counter++)
        }
    }

    /**
     * replaces a card with another card
     */
    operator fun set(cardToReplace: Card, cardReplaceWith: Card) = replaceCard(cardToReplace, cardReplaceWith)

    /**
     * compares the amount of cards in the deck
     * 1 if Deck has more cards than num
     * -1 if Deck has less cards than num
     * 0 if Deck and num are equal
     */
    operator fun compareTo(num: Int): Int = when {
        size > num -> 1
        size < num -> -1
        size == num -> 0
        else -> num
    }

    /**
     * compares the amount of cards in the deck
     * 1 if Deck has more cards than otherDeck
     * -1 if Deck has less cards than otherDeck
     * 0 if Deck and otherDeck are equal
     */
    operator fun compareTo(otherDeck: Deck): Int = when {
        size > otherDeck.size -> 1
        size < otherDeck.size -> -1
        size == otherDeck.size -> 0
        else -> otherDeck.size
    }

    /**
     * adds num number of decks to this deck
     */
    operator fun timesAssign(num: Int) {
        for (i in 1 until num) {
            initialize()
        }
    }

    /**
     * removes num number of decks from this deck
     */
    operator fun divAssign(num: Int) {
        val size = if (num == 1) {
            52
        } else {
            size / num
        }
        for (i in 0 until size) {
            try {
                deckOfCards.removeAt(0)
            } catch (e: IndexOutOfBoundsException) {
                break
            }
        }
    }

    /**
     * allows iteration
     */
    operator fun iterator() = deckOfCards.iterator()

    /**
     * Shorthand to draw from the bottom of the deck
     */
    operator fun unaryMinus() = getCard(size - 1)

    /**
     * Shorthand to draw from the deck
     */
    operator fun unaryPlus(): Card = draw()

    /**
     * returns a deck with all the cards that are not in this deck
     */
    operator fun not(): Deck {
        val d = Deck(false)
        d.removeIf { it in deckOfCards }
        return d
    }

    /**
     * adds deck to this deck
     * @param deck the deck to add
     */
    infix fun addDeck(deck: Deck) = plusAssign(deck)

    /**
     * adds num number of decks to this deck
     */
    infix fun addDecks(num: Int) = timesAssign(num)

    /**
     * removes num number of decks from this deck
     */
    infix fun removeDecks(num: Int) = divAssign(num)

    /**
     * draws [num] cards from the deck
     * @return a [Collection] of cards
     */
    infix fun drawCards(num: Int): Collection<Card> = getCards(num)

    /**
     * This creates a deck
     * @param spades if you want spades
     * @param clubs if you want clubs
     * @param diamonds if you want diamonds
     * @param hearts if you want hearts
     */
    private fun initialize(
        spades: Boolean = true,
        clubs: Boolean = true,
        diamonds: Boolean = true,
        hearts: Boolean = true
    ) {
        for (i in 1..13) {
            if (spades)
                deckOfCards.add(Card(Suit.SPADES, i))
            if (clubs)
                deckOfCards.add(Card(Suit.CLUBS, i))
            if (diamonds)
                deckOfCards.add(Card(Suit.DIAMONDS, i))
            if (hearts)
                deckOfCards.add(Card(Suit.HEARTS, i))
        }
    }

    //methods
    /**
     * clears the deck
     */
    fun clear() {
        deckOfCards.clear()
    }

    /**
     * reverses the order of the cards
     */
    fun reverse() {
        deckOfCards.reverse()
    }

    /**
     * removes all cards of color
     * @param color the color to remove
     * @return true if any were removed
     */
    @TargetApi(Build.VERSION_CODES.N)
    fun removeColor(color: Color): Boolean {
        return deckOfCards.removeIf {
            it.color.equals(color)
        }
    }

    /**
     * removes all cards of suit
     * @param suit the suit to remove
     * @return true if any were removed
     */
    @TargetApi(Build.VERSION_CODES.N)
    fun removeSuit(suit: Suit): Boolean {
        return deckOfCards.removeIf {
            it.suit.equals(suit)
        }
    }

    /**
     * removes all cards with the value num
     * @param num the number to remove
     * @return true if any were removed
     */
    @TargetApi(Build.VERSION_CODES.N)
    fun removeNumber(num: Int): Boolean {
        return deckOfCards.removeIf {
            it.value == num
        }
    }

    /**
     * removes all cards with the value num
     * @return true if any were removed
     */
    @TargetApi(Build.VERSION_CODES.N)
    fun removeIf(predicate: (Card) -> Boolean): Boolean {
        return deckOfCards.removeIf(predicate)
    }

    /**
     * compares this deck to another deck
     */
    fun compareToDeck(otherDeck: Deck, check: (Card, Card) -> Boolean): Boolean {
        if(size!=otherDeck.size) {
            return false
        }
        return comparingMethod(this.deckOfCards, otherDeck.deckOfCards, check)
    }

    /**
     * Draws a card.
     *
     * @return Card
     */
    @Throws(CardNotFoundException::class)
    fun draw(): Card {
        try {
            val c = deckOfCards.removeAt(0)
            deckListener?.draw(c, size)
            return c
        } catch (e: IndexOutOfBoundsException) {
            throw emptyDeck
        }
    }

    /**
     * Adds a card to the deck.
     *
     * @param c Card
     */
    infix fun addCard(c: Card) {
        deckOfCards.add(c)
        deckListener?.cardAdded(mutableListOf(c))
    }

    /**
     * Adds cards to the deck.
     *
     * @param c Card
     */
    infix fun addCards(c: Collection<Card>) {
        deckOfCards.addAll(c)
        deckListener?.cardAdded(c)
    }

    /**
     * places a card in a wanted location
     */
    fun addCard(location: Int = CardUtil.randomNumber(0, size), card: Card) {
        deckOfCards.add(location, card)
        deckListener?.cardAdded(mutableListOf(card))
    }

    /**
     * replaces 1 occurrence of [cardToReplace] with [cardReplaceWith]
     * default range == 0 until deck.size
     */
    fun replaceCard(cardToReplace: Card, cardReplaceWith: Card, range: (Deck) -> IntProgression = { deck -> 0 until deck.size }) {
        for(i in range(this)) {
            if(this[i] == cardToReplace) {
                this[i] = cardReplaceWith
                break
            }
        }
    }

    /**
     * replaces all [cardToReplace] with [cardToReplace]
     */
    fun replaceAllCards(cardToReplace: Card, cardReplaceWith: Card) {
        for(i in 0 until size) {
            if(this[i] == cardToReplace) {
                this[i] = cardReplaceWith
            }
        }
    }

    /**
     * Draws a random card from the deck.
     *
     * @param n The place where the card is drawn
     * @return Card
     */
    @Throws(CardNotFoundException::class)
    infix fun getCard(n: Int): Card {
        try {
            val c = deckOfCards.removeAt(n)
            deckListener?.draw(c, size)
            return c
        } catch (e: IndexOutOfBoundsException) {
            throw emptyDeck
        }
    }

    /**
     * Gets a [Collection] of cards from the top of the deck
     * @param num the number of cards to [draw]
     * @return a [Collection] of cards
     */
    fun getCards(num: Int): Collection<Card> {
        val cards = arrayListOf<Card>()
        for (i in 0 until num) {
            cards += draw()
        }
        return cards
    }

    /**
     * Gets a [Collection] of cards from the top of the deck
     * @param cardList the number of cards to get
     * @return a [Collection] of cards
     */
    fun getCards(cardList: Collection<Card>): Collection<Card> {
        val cards = arrayListOf<Card>()
        for (i in cardList) {
            cards += getCard(i)
        }
        return cards
    }

    /**
     * Gets the card you want.
     *
     * @param s Suit
     * @param v Value
     * @return Your Card
     */
    @Throws(CardNotFoundException::class)
    fun getCard(s: Suit, v: Int): Card {
        val check = Card(s, v)
        for (i in deckOfCards.indices) {
            if (deckOfCards[i].equals(check)) {
                val cTemp = deckOfCards.removeAt(i)
                deckListener?.draw(cTemp, size)
                return cTemp
            }
        }
        throw CardNotFoundException("Could not find card $check")
    }

    /**
     * Gets a card out of the deck
     * @param c the wanted card
     * @return the card
     */
    @Throws(CardNotFoundException::class)
    fun getCard(c: Card): Card {
        for (i in deckOfCards.indices) {
            if (deckOfCards[i].equals(c)) {
                val cTemp = deckOfCards.removeAt(i)
                deckListener?.draw(cTemp, size)
                return cTemp
            }
        }
        throw CardNotFoundException("Could not find card $c")
    }

    /**
     * Gets a card out of the deck
     * @return the card
     */
    @Throws(CardNotFoundException::class)
    fun getCard(predicate: (Card) -> Boolean): Card {
        for (i in deckOfCards.indices) {
            if (predicate(deckOfCards[i])) {
                val cTemp = deckOfCards.removeAt(i)
                deckListener?.draw(cTemp, size)
                return cTemp
            }
        }
        throw CardNotFoundException("Could not find card")
    }

    /**
     * Get the location of a card
     * @param c the wanted card
     * @return the location of [c]
     */
    @Throws(CardNotFoundException::class)
    fun getCardLocation(c: Card): Int {
        for((num, card) in deckOfCards.withIndex()) {
            if (card == c) {
                return num
            }
        }
        return -1
    }

    /**
     * Gets the first card by Value.
     *
     * @param v Value of Card
     * @return the first card by Value
     */
    @Throws(CardNotFoundException::class)
    fun getFirstCardByValue(v: Int): Card {
        for (i in 0 until size) {
            if (v == deckOfCards[i].value) {
                val c = deckOfCards.removeAt(i)
                deckListener?.draw(c, size)
                return c
            }
        }
        throw CardNotFoundException("Could not find card for value $v")
    }

    /**
     * Gets the first card by Value.
     * @return the first card by Value
     */
    @Throws(CardNotFoundException::class)
    fun getFirstCardByValue(predicate: (Int) -> Boolean): Card {
        for (i in 0 until size) {
            if (predicate(deckOfCards[i].value)) {
                val c = deckOfCards.removeAt(i)
                deckListener?.draw(c, size)
                return c
            }
        }
        throw CardNotFoundException("Could not find card for the wanted value")
    }

    /**
     * Gets the last card by Value.
     *
     * @param v Value of Card
     * @return the last card by Value
     */
    @Throws(CardNotFoundException::class)
    fun getLastCardByValue(v: Int): Card {
        for (i in size - 1 downTo 0) {
            if (v == deckOfCards[i].value) {
                val c = deckOfCards.removeAt(i)
                deckListener?.draw(c, size)
                return c
            }
        }

        throw CardNotFoundException("Could not find card for value $v")
    }

    /**
     * Gets the last card by Value.
     *
     * @param predicate a predicate
     * @return the last card by Value
     */
    @Throws(CardNotFoundException::class)
    fun getLastCardByValue(predicate: (Int) -> Boolean): Card {
        for (i in size - 1 downTo 0) {
            if (predicate(deckOfCards[i].value)) {
                val c = deckOfCards.removeAt(i)
                deckListener?.draw(c, size)
                return c
            }
        }
        throw CardNotFoundException("Could not find card for the wanted value")
    }

    /**
     * Gets the first card by suit.
     *
     * @param s Suit
     * @return the first card by suit
     */
    @Throws(CardNotFoundException::class)
    fun getFirstCardBySuit(s: Suit): Card {
        for (i in 0 until size) {
            if (s.equals(deckOfCards[i].suit)) {
                val c = deckOfCards.removeAt(i)
                deckListener?.draw(c, size)
                return c
            }
        }
        throw CardNotFoundException("Could not find card for suit $s")
    }

    /**
     * Gets the last card by suit.
     *
     * @param s Suit
     * @return the last card by suit
     */
    @Throws(CardNotFoundException::class)
    fun getLastCardBySuit(s: Suit): Card {
        for (i in size - 1 downTo 0) {
            if (s.equals(deckOfCards[i].suit)) {
                val c = deckOfCards.removeAt(i)
                deckListener?.draw(c, size)
                return c
            }
        }
        throw CardNotFoundException("Could not find card for suit $s")
    }

    /**
     * Gets the first card by suit.
     * @return the first card by suit
     */
    @Throws(CardNotFoundException::class)
    fun getFirstCardBySuit(predicate: (Suit) -> Boolean): Card {
        for (i in 0 until size) {
            if (predicate(deckOfCards[i].suit)) {
                val c = deckOfCards.removeAt(i)
                deckListener?.draw(c, size)
                return c
            }
        }
        throw CardNotFoundException("Could not find card for suit")
    }

    /**
     * Gets the last card by suit.
     * @return the last card by suit
     */
    @Throws(CardNotFoundException::class)
    fun getLastCardBySuit(predicate: (Suit) -> Boolean): Card {
        for (i in size - 1 downTo 0) {
            if (predicate(deckOfCards[i].suit)) {
                val c = deckOfCards.removeAt(i)
                deckListener?.draw(c, size)
                return c
            }
        }
        throw CardNotFoundException("Could not find card for suit")
    }

    /**
     * Gets the first card by color.
     *
     * @param color the color
     * @return the first card by color
     */
    @Throws(CardNotFoundException::class)
    fun getFirstCardByColor(color: Color): Card {
        for (i in 0 until size) {
            if (color.equals(deckOfCards[i].color)) {
                val c = deckOfCards.removeAt(i)
                deckListener?.draw(c, size)
                return c
            }
        }
        throw CardNotFoundException("Could not find card for the color $color")
    }

    /**
     * Gets the last card by color.
     *
     * @param color the color
     * @return the last card by color
     */
    @Throws(CardNotFoundException::class)
    fun getLastCardByColor(color: Color): Card {
        for (i in size - 1 downTo 0) {
            if (color.equals(deckOfCards[i].color)) {
                val c = deckOfCards.removeAt(i)
                deckListener?.draw(c, size)
                return c
            }
        }
        throw CardNotFoundException("Could not find card for the color $color")
    }

    /**
     * Gets the first card by color.
     * @return the first card by color
     */
    @Throws(CardNotFoundException::class)
    fun getFirstCardByColor(predicate: (Color) -> Boolean): Card {
        for (i in 0 until size) {
            if (predicate(deckOfCards[i].color)) {
                val c = deckOfCards.removeAt(i)
                deckListener?.draw(c, size)
                return c
            }
        }
        throw CardNotFoundException("Could not find card for the color")
    }

    /**
     * Gets the last card by color.
     * @return the last card by color
     */
    @Throws(CardNotFoundException::class)
    fun getLastCardByColor(predicate: (Color) -> Boolean): Card {
        for (i in size - 1 downTo 0) {
            if (predicate(deckOfCards[i].color)) {
                val c = deckOfCards.removeAt(i)
                deckListener?.draw(c, size)
                return c
            }
        }
        throw CardNotFoundException("Could not find card for the color")
    }

    /**
     * Sorts the deck by color
     * (Black, Red)
     */
    fun sortByColor() {
        deckOfCards.sortWith(compareBy { it.color })
    }

    /**
     * Sorts the deck by card value
     */
    fun sortByValue() {
        deckOfCards.sortWith(compareBy { it.value })
    }

    /**
     * Sorts the deck by suit
     * (Spades, Clubs, Diamonds, Hearts)
     */
    fun sortBySuit() {
        deckOfCards.sortWith(compareBy { it.suit })
    }

    /**
     * Sorts the deck to a brand new deck. Values are Ascending and Suit order (Spades, Clubs, Diamonds, Hearts)
     */
    fun sortToReset() {
        val spadesList = this[Suit.SPADES]
        val clubsList = this[Suit.CLUBS]
        val diamondsList = get(Suit.DIAMONDS)
        val heartsList = get(Suit.HEARTS)
        clear()
        deckOfCards.apply {
            addAll(spadesList.sortedWith(compareBy { it.value }))
            addAll(clubsList.sortedWith(compareBy { it.value }))
            addAll(diamondsList.sortedWith(compareBy { it.value }))
            addAll(heartsList.sortedWith(compareBy { it.value }))
        }
    }

    /**
     * a custom sort
     */
    fun sortBy(comparator: Comparator<in Card>) {
        deckOfCards.sortWith(comparator)
    }

    /**
     * Shuffles the deck.
     *
     * @param seed for generation
     */
    fun shuffle(seed: Long? = null) {
        val gen: Random = if (seed == null) {
            Random()
        } else {
            Random(seed)
        }

        deckOfCards.shuffle(gen)

        if (deckListener != null) {
            deckListener!!.shuffle()
        }
    }

    /**
     * People say that you need to shuffle a deck 7 times before it is truly shuffled and randomized.
     * That is what this does.
     *
     * @param seed for generation (Yes, you can seed a true random shuffle if you wish)
     */
    fun trueRandomShuffle(seed: Long? = null) {
        for (i in 1..7) {
            shuffle(seed)
        }
    }

    /**
     * returns the deck
     */
    fun getDeck(): ArrayList<Card> {
        return deckOfCards
    }

    /**
     * partitions a deck into two decks based off of a predicate
     */
    fun partition(predicate: (Card) -> Boolean): Pair<Deck, Deck> {
        val (deck1, deck2) = deckOfCards.partition(predicate)
        return Pair(Deck(cards = deck1), Deck(cards = deck2))
    }

    /**
     * find cards based on a predicate
     * @return a deck of those cards
     */
    fun find(predicate: (Card) -> Boolean): Deck {
        val list = arrayListOf<Card>()
        for (c in deckOfCards)
            if (predicate(c)) {
                list += c
            }
        return Deck(cards = list)
    }

    /**
     * split the deck at a chosen location or midway
     */
    fun splitDeck(location: Int = size / 2): Pair<Deck, Deck> {
        val deck1 = deckOfCards.slice(0 until location)
        val deck2 = deckOfCards.slice(location until size)
        return Pair(Deck(cards = deck1), Deck(cards = deck2))
    }

    /**
     * checks to see if there are no cards of a predicate in the deck
     */
    fun none(predicate: (Card) -> Boolean): Boolean = deckOfCards.none(predicate)

    /**
     * checks to see if there are any cards of a predicate in the deck
     */
    fun any(predicate: (Card) -> Boolean): Boolean = deckOfCards.any(predicate)

    /**
     * groups the deck by a predicate
     */
    fun <T> groupBy(predicate: (Card) -> T): Map<T, List<Card>> = deckOfCards.groupBy(predicate)

    /**
     * associates the deck by a predicate
     */
    fun <T> associateBy(predicate: (Card) -> T): Map<T, Card> = deckOfCards.associateBy(predicate)

    /**
     * executes a for each action
     */
    fun forEach(action: (Card) -> Unit) {
        deckOfCards.forEach(action)
    }

    /**
     * a custom count method
     */
    fun countSpecific(predicate: (Card) -> Boolean): Int {
        return deckOfCards.count(predicate)
    }

    //To String Methods
    /**
     * The Deck.
     *
     * @return The remaining contents of the deck
     */
    override fun toString(): String {
        return deckOfCards.joinToString("\n") { it.toString() }
    }

    /**
     * The Deck.
     *
     * @return The remaining contents of the deck
     */
    fun toNormalString(): String {
        return deckOfCards.joinToString("\n") { it.toNormalString() }
    }

    /**
     * The Deck.
     *
     * @return The remaining contents of the deck
     */
    fun toSymbolString(): String {
        return deckOfCards.joinToString("\n") { it.toSymbolString() }
    }

    /**
     * The Deck.
     *
     * @return The remaining contents of the deck
     */
    fun toPrettyString(): String {
        return deckOfCards.joinToString("\n") { it.toPrettyString() }
    }

    /**
     * The Deck in Array String Format
     *
     * @return The remaining contents of the deck
     */
    fun toArrayString(): String {
        return "$deckOfCards"
    }

    /**
     * The Deck in Array String Format
     *
     * @return The remaining contents of the deck
     */
    fun toArrayNormalString(): String {
        return "[${deckOfCards.joinToString(separator = ", ") { it.toNormalString() }}]"
    }

    /**
     * The Deck in Array String Format
     *
     * @return The remaining contents of the deck
     */
    fun toArraySymbolString(): String {
        return "[${deckOfCards.joinToString(separator = ", ") { it.toSymbolString() }}]"
    }

    /**
     * The Deck in Array String Format
     *
     * @return The remaining contents of the deck
     */
    fun toArrayPrettyString(): String {
        return "[${deckOfCards.joinToString(separator = ", ") { it.toPrettyString() }}]"
    }

    /**
     * a custom print statement based on the user
     * default is "$i=$card\t
     */
    fun toCustomString(stringStatement: (Card, Int) -> String = { card: Card, i: Int -> "$i=$card\t" }): String {
        var tempString = ""
        for(i in 0 until size)
            tempString += stringStatement(this[i], i)
        return tempString
    }

    /**
     * a listener that listens for Deck actions
     */
    interface DeckListener {
        /**
         * listener for when the deck is shuffles
         */
        fun shuffle() {
            println("Shuffling...")
        }

        /**
         * listener for when a card is removed from the deck
         * @param c the card that was removed
         * @param size the size of the deck
         */
        fun draw(c: Card, size: Int)

        /**
         * when a card is added to the deck
         * @param c the card(s) that was added
         */
        fun cardAdded(c: Collection<Card>) {
            println(c)
        }
    }
}