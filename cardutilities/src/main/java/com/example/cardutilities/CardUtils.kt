package com.example.cardutilities

import crestron.com.deckofcards.Card
import crestron.com.deckofcards.Deck
import kotlin.random.Random

/**
 * removes num of cards and returns them
 */
fun Deck.removeCards(num: Int): Collection<Card> {
    val cards = arrayListOf<Card>()
    for (i in 0..num) {
        cards += this.draw()
    }
    return cards
}

fun Random.nextDeck(): Deck = Deck.randomDeck()

fun ArrayList<Card>.addWithAction(card: Card, action: (Card) -> Unit) {
    add(card)
    action(card)
}

fun ArrayList<Card>.removeWithAction(index: Int, action: (Card) -> Unit) {
    action(removeAt(index))
}

/**
 * gets info on a card, value suit color
 */
fun Card.fullInfo(): String {
    return "$valueString of $suit of $color"
}

operator fun Card.inc(): Card {
    return unaryPlus()
}

operator fun Card.dec(): Card {
    return unaryMinus()
}

fun Card.nextCard(): Card {
    return unaryPlus()
}

fun Card.previousCard(): Card {
    return unaryMinus()
}

operator fun Card.times(num: Int): Collection<Card> {
    val cards = arrayListOf<Card>()
    for(i in 0..num) {
        cards+=this
    }
    return cards
}