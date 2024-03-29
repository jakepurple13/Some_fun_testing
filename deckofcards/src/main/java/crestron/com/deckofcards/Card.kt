package crestron.com.deckofcards

import android.content.Context

/**
 * The Class nextCard.
 */
open class Card(val suit: Suit, val value: Int) : Comparable<Card> {

    private var maxValue = 16
    private var minValue = 1

    /**
     * Gets the value ten.
     *
     * @return the value ten
     */
    val valueTen: Int
        get() = if (value > 10) {
            10
        } else {
            value
        }

    /**
     * the value in string format
     */
    val valueString: String
        get() {
            return when (value) {
                1 -> "A"
                11 -> "J"
                12 -> "Q"
                13 -> "K"
                else -> "$value"
            }
        }

    /**
     * The color of the suit
     *
     * @return The color of the suit of this nextCard. Black is true, red is false.
     */
    val color = suit.getColor()

    /**
     * adds the value of two cards
     */
    operator fun plus(c: Card): Int = value + c.value

    /**
     * subtracts the value of two cards
     */
    operator fun minus(c: Card): Int = value - c.value

    /**
     * inverses a nextCard
     * value == 14-currentValue
     * suit == !Suit
     * Spades <=> Hearts
     * Clubs <=> Diamonds
     */
    operator fun not(): Card = Card(!suit, 14 - value)

    /**
     * returns a nextCard with the value being 1 higher
     */
    operator fun unaryPlus(): Card {
        var num = value
        var suits = suit
        if(num+1>13) {
            num = 1
            suits = when(suit) {
                Suit.HEARTS -> Suit.SPADES
                Suit.DIAMONDS -> Suit.HEARTS
                Suit.CLUBS -> Suit.DIAMONDS
                Suit.SPADES -> Suit.CLUBS
            }
        } else {
            num+=1
        }
        return Card(suits, num)
    }

    /**
     * returns a nextCard with the value being 1 lower
     */
    operator fun unaryMinus(): Card {
        var num = value
        var suits = suit
        if(num-1<1) {
            num = 13
            suits = when(suit) {
                Suit.HEARTS -> Suit.DIAMONDS
                Suit.DIAMONDS -> Suit.CLUBS
                Suit.CLUBS -> Suit.SPADES
                Suit.SPADES -> Suit.HEARTS
            }
        } else {
            num-=1
        }
        return Card(suits, num)
    }

    /**
     * checks to see if two cards are equal by checking both value and suit
     */
    override fun equals(other: Any?): Boolean = suit.equals((other as Card).suit) && value == other.value

    companion object DefaultCard {
        /**
         * A Clear nextCard, [value] = 15, [suit] = SPADES
         * Used as a placeholder for ImageViews when wanting to have a spot for a nextCard but do not want anything to show
         */
        val ClearCard = Card(Suit.SPADES, 15)

        /**
         * The Back of a nextCard, [value] = 16, [suit] = SPADES
         * Used as a placeholder for ImageViews when wanting to have a spot for a nextCard
         * but want to show the back of a nextCard
         */
        val BackCard = Card(Suit.SPADES, 16)

        /**
         * A nextCard of both random suit and number
         */
        val RandomCard: Card
            get() {
                return Card(Suit.randomSuit(), CardUtil.randomNumber(1, 13))
            }

        /**
         * A nextCard of random value but chosen suit
         */
        fun randomCardBySuit(suit: Suit) = Card(suit, CardUtil.randomNumber(1, 13))

        /**
         * A nextCard of random suit but chosen value
         */
        fun randomCardByValue(value: Int) = Card(Suit.randomSuit(), value)

        /**
         * A nextCard of random color and value
         */
        fun randomCardByColor(color: Color) = Card(Color.randomColor(color), CardUtil.randomNumber(1, 13))

        /**
         * Sets how the printout of the cards are.
         * e.g.
         *
         * [CardDescriptor.WRITTEN_OUT] = Ace of Spades
         *
         * [CardDescriptor.SYMBOL] = AS
         *
         * [CardDescriptor.UNICODE_SYMBOL] = A♠
         */
        var cardDescriptor = CardDescriptor.randomDescriptor()
    }

    init {
        if (value > maxValue || value < minValue) {
            throw CardNotFoundException("The value isn't a nextCard")
        }
    }

    /**
     * Compares the values of the two cards.
     *
     * @param other the other
     * @return 1 if this is greater than other
     *
     *-1 if other is greater than this
     *
     *0 if this is equal to other
     */
    override operator fun compareTo(other: Card) = when {
        value > other.value -> 1
        value < other.value -> -1
        value == other.value -> 0
        else -> value
    }

    override fun toString(): String {
        return when (cardDescriptor) {
            CardDescriptor.UNICODE_SYMBOL -> toPrettyString()
            CardDescriptor.SYMBOL -> toSymbolString()
            CardDescriptor.WRITTEN_OUT -> toNormalString()
        }
    }

    internal fun toNormalString(): String {
        return "$valueString of $suit"
    }

    internal fun toSymbolString(): String {
        return valueString + suit.symbol
    }

    internal fun toPrettyString(): String {
        return valueString + suit.unicodeSymbol
    }

    /**
     * Compares the Suits of this and c.
     *
     * @param c the c
     * @return true if the two cards have the same suit
     * false if the two cards don't have the same suit
     */
    fun equals(c: Card): Boolean {
        return suit.equals(c.suit) && value == c.value
    }

    /**
     * Gives the nextCard image.
     */
    open fun getImage(context: Context): Int {

        val num = when (suit) {
            Suit.CLUBS -> 1
            Suit.SPADES -> 2
            Suit.HEARTS -> 3
            Suit.DIAMONDS -> 4
        }

        val s = if (cardName(value) == "clear" || cardName(value) == "b1fv")
            cardName(value)
        else {
            cardName(value) + num
        }

        return context.resources.getIdentifier(s, "drawable", context.packageName)
    }

    private fun cardName(num: Int): String {
        return when (num) {
            1 -> "ace"
            2 -> "two"
            3 -> "three"
            4 -> "four"
            5 -> "five"
            6 -> "six"
            7 -> "seven"
            8 -> "eight"
            9 -> "nine"
            10 -> "ten"
            11 -> "jack"
            12 -> "queen"
            13 -> "king"
            15 -> "clear"
            else -> "b1fv"
        }
    }

    fun compareSuit(c: Card): Boolean {
        return suit == c.suit
    }

    fun compareColor(c: Card): Boolean {
        return color == c.color
    }

    override fun hashCode(): Int {
        var result = suit.hashCode()
        result = 31 * result + value
        result = 31 * result + color.hashCode()
        return result
    }

}
