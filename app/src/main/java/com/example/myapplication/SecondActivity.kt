package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import crestron.com.deckofcards.Card
import crestron.com.deckofcards.Deck
import kotlinx.android.synthetic.main.activity_second.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

/*class Card2(suit: Suit, value: Int) : Card(suit, value) {
    init {
        maxValue = 5
        minValue = 3
    }

    override fun getImage(context: Context): Int {
        return Card.BackCard.getImage(context)
    }
}*/

class SecondActivity : AppCompatActivity() {

    private var deck = Deck(shuffler = true)
    private var numDecks = 1

    private var showCard = true

    private var playerCount = 0
        @SuppressLint("SetTextI18n")
        set(value) {
            field = value
            playerTotal.text = "$field${playerCards.joinToString(separator = " ") { "\n$it" }}"
        }
    private var dealerCount = 0
        @SuppressLint("SetTextI18n")
        set(value) {
            field = value
            dealerTotal.text = "$field\n${dealerCards.joinToString("\n")}"
        }

    private var playerCards = arrayListOf<Card>()
    private var dealerCards = arrayListOf<Card>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        background.setBackgroundColor(intent.getIntExtra("bgColor", 0))

        deck.deckListener = object : Deck.DeckListener {
            @SuppressLint("SetTextI18n")
            override fun draw(c: Card, size: Int) {
                if (size < 5) {
                    deck = Deck(shuffler = true, numberOfDecks = ++numDecks, deckListener = this)
                    deck.trueRandomShuffle()
                }
                infoToSpeak.text = "$size cards left"
            }

            @SuppressLint("SetTextI18n")
            override fun shuffle() {
                super.shuffle()
                infoToSpeak.text = "Shuffling..."
            }

            @SuppressLint("SetTextI18n")
            override fun cardAdded(c: Collection<Card>) {
                super.cardAdded(c)
                infoToSpeak.text = "Adding Cards!"
            }
        }

        playerHit.setOnClickListener {
            playerMove()
        }

        stand.setOnClickListener {
            playerHit.isEnabled = false
            stand.isEnabled = false
            dealerMove()
        }

        reset.setOnClickListener {
            playerCards.clear()
            dealerCards.clear()
            playerCount = 0
            dealerCount = 0
            playerCard.setImageResource(Card.BackCard.getImage(this@SecondActivity))
            dealerCard.setImageResource(Card.BackCard.getImage(this@SecondActivity))

            start()
        }

    }

    private fun start() = GlobalScope.launch(Dispatchers.Main) {
        reset.isEnabled = false
        playerMove()
        delay(500)
        val info = hit(dealerCards)
        dealerCount = info.first
        if (showCard)
            dealerCard.setImageResource(info.second.getImage(this@SecondActivity))
        delay(500)
        playerMove()
        playerHit.isEnabled = true
        stand.isEnabled = true
    }

    private fun dealerMove() = GlobalScope.launch(Dispatchers.Main) {
        do {
            val info = hit(dealerCards)
            dealerCount = info.first
            if (showCard)
                dealerCard.setImageResource(info.second.getImage(this@SecondActivity))
            delay(500)
        } while (dealerCount <= 16)

        when {
            playerCount > 21 -> alert("Busted. Dealer Wins")
            dealerCount > 21 -> alert("Dealer Busts. Player Wins!")
            playerCount > 21 && dealerCount > 21 -> alert("Both of you busted! No winner!")
            else -> when {
                dealerCount < playerCount -> alert("Player Wins!")
                dealerCount > playerCount -> alert("Dealer Wins!")
                else -> alert("Push. Dealer Wins.")
            }
        }
        delay(300)
        reset.isEnabled = true
    }

    private fun alert(message: String) {
        //Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        infoToSpeak.text = message
    }

    private fun playerMove() {
        val info = hit(playerCards)
        playerCount = info.first
        if (showCard)
            playerCard.setImageResource(info.second.getImage(this@SecondActivity))
        if (playerCount > 21) {
            playerHit.isEnabled = false
            stand.performClick()
        }
    }

    private fun hit(listCard: ArrayList<Card>): Pair<Int, Card> {
        val c: Card = deck.draw()
        listCard.add(c)
        val sortedCards = arrayListOf<Card>().apply {
            addAll(listCard)
            Collections.sort(this, compareByDescending { it.valueTen })
        }
        var num = 0
        for (card in sortedCards) {
            val amount = if (card.value == 1 && num + 11 < 22) {
                11
            } else if (card.value == 1) {
                1
            } else {
                card.valueTen
            }
            num += amount
        }
        return Pair(num, c)
    }
}
