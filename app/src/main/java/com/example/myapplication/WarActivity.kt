package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import crestron.com.deckofcards.Card
import crestron.com.deckofcards.Deck
import kotlinx.android.synthetic.main.activity_war.*
import kotlinx.coroutines.*

class WarActivity : AppCompatActivity() {

    private val deck = Deck(shuffler = true)
    var playerDeck = Deck(cards = deck[0..25])
    val playerCards = arrayListOf<Card>()
    var enemyDeck = Deck(cards = deck[26..51])
    val enemyCards = arrayListOf<Card>()
    var playerCard: Card = Card.BackCard
        set(value) {
            field = value
            playerpile.setImageResource(field.getImage(this@WarActivity))
        }
    var enemyCard: Card = Card.BackCard
        set(value) {
            field = value
            enemypile.setImageResource(field.getImage(this@WarActivity))
        }

    var warCards = arrayListOf<Card>()

    var stopPlaying = false

    @SuppressLint("SetTextI18n")
    fun ArrayList<Card>.addCard(card: Card) {
        add(card)
        playerInfo.text = "Cards Remaining: ${playerDeck.size}\nCards Waiting: ${playerCards.size}"
        enemyInfo.text = "Cards Remaining: ${enemyDeck.size}\nCards Waiting: ${enemyCards.size}"
    }

    @SuppressLint("SetTextI18n")
    fun ArrayList<Card>.addCards(card: Collection<Card>) {
        addAll(card)
        playerInfo.text = "Cards Remaining: ${playerDeck.getDeck().size}\nCards Waiting: ${playerCards.size}"
        enemyInfo.text = "Cards Remaining: ${enemyDeck.getDeck().size}\nCards Waiting: ${enemyCards.size}"
        stopPlaying = enemyDeck.getDeck().isEmpty() || playerDeck.getDeck().isEmpty()
    }

    lateinit var dialog: AlertDialog

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_war)

        background.setBackgroundColor(intent.getIntExtra("bgColor", 0))

        dialog = AlertDialog.Builder(this@WarActivity)
            .setTitle("Winner Decided")
            .setPositiveButton(
                "Okay"
            ) { _, _ -> finish() }
            .create()

        playerDeck.deckListener = object : Deck.DeckListener {
            @SuppressLint("SetTextI18n")
            override fun draw(c: Card, size: Int) {
                if (size < 1) {
                    if (playerCards.isEmpty()) {
                        /*if (!dialog.isShowing) {
                            dialog.setMessage("You Lose")
                            dialog.show()
                        }
                        playerDeck += Card.RandomCard*/
                        stopPlaying = true
                    } else {
                        playerDeck += playerCards
                        playerCards.clear()
                    }
                }
                playerInfo.text = "Cards Remaining: $size\nCards Waiting: ${playerCards.size}"
            }
        }
        enemyDeck.deckListener = object : Deck.DeckListener {
            @SuppressLint("SetTextI18n")
            override fun draw(c: Card, size: Int) {
                if (size < 1) {
                    if (enemyCards.isEmpty()) {
                        /*if (!dialog.isShowing) {
                            dialog.setMessage("You Win")
                            dialog.show()
                        }
                        enemyDeck += Card.RandomCard*/
                        stopPlaying = true
                    } else {
                        enemyDeck += enemyCards
                        enemyCards.clear()
                    }
                }
                enemyInfo.text = "Cards Remaining: $size\nCards Waiting: ${enemyCards.size}"
            }
        }

        Loged.d("${playerDeck.toArrayString()} and ${playerDeck.size}")
        Loged.d("${enemyDeck.toArrayString()} and ${enemyDeck.size}")

        fun autoPlay() = GlobalScope.launch(Dispatchers.Main) {
            Loged.i("Starting with ${autoswitch.isChecked}")
            while (autoswitch.isChecked) {
                val jobs = arrayListOf<Deferred<Unit>>()
                if (playerpile.isEnabled) {
                    //playerpile.performClick()
                    delay(700)
                    jobs += placeCardsDownAsync()
                }
                if (collectButton.isEnabled) {
                    //collectButton.performClick()
                    jobs += collectingAsync()
                    delay(700)
                }
                jobs.forEach { it.await() }
                if (dialog.isShowing) {
                    break
                }
            }
        }

        autoswitch.setOnCheckedChangeListener { compoundButton, b ->
            GlobalScope.launch(Dispatchers.Main) {
                if (autoPlay().isActive) {
                    autoPlay().cancel()
                } else {
                    autoPlay().start()
                }
            }
        }

        playerpile.setOnClickListener {
            placeCardsDownAsync().start()
        }

        collectButton.setOnClickListener {
            collectingAsync().start()
        }

    }

    private fun placeCardsDownAsync() = GlobalScope.async(Dispatchers.Main) {
        playerCard = playerDeck.draw()
        enemyCard = enemyDeck.draw()
        playerpile.isEnabled = false
        collectButton.isEnabled = true
    }

    private fun collectingAsync() = GlobalScope.async(Dispatchers.Main) {
        Loged.i("$playerCard and $enemyCard")
        val playerValue = if (playerCard.value == 1) 14 else playerCard.value
        val enemyValue = if (enemyCard.value == 1) 14 else enemyCard.value
        Loged.i("$playerValue and $enemyValue")
        val winner = when {
            playerValue > enemyValue -> {
                playerCards.addCard(playerCard)
                playerCards.addCard(enemyCard)
                "Player Wins with $playerCard beating $enemyCard"
            }
            enemyValue > playerValue -> {
                enemyCards.addCard(playerCard)
                enemyCards.addCard(enemyCard)
                "Enemy Wins with $enemyCard beating $playerCard"
            }
            else -> {
                for (i in 0 until playerValue) {
                    playerCard = playerDeck.draw()
                    enemyCard = enemyDeck.draw()
                    warCards.add(playerCard)
                    warCards.add(enemyCard)
                    delay(500)
                    if (stopPlaying)
                        break
                }
                when {
                    playerValue > enemyValue -> {
                        playerCards.addCards(warCards)
                        "Player Wins with $playerCard beating $enemyCard"
                    }
                    enemyValue > playerValue -> {
                        enemyCards.addCards(warCards)
                        "Enemy Wins with $enemyCard beating $playerCard"
                    }
                    else -> {
                        playerCards.addCards(warCards)
                        "Player Wins with $playerCard beating $enemyCard"
                    }
                }
            }//war().await()
        }
        if(stopPlaying) {
            if (!dialog.isShowing) {
                dialog.setMessage(if(playerDeck > enemyDeck) "You Win" else "You Lose")
                dialog.show()
            }
        }
        warCards.clear()
        warinfo.text = winner
        collectButton.isEnabled = false
        delay(500)
        playerCard = Card.BackCard
        enemyCard = Card.BackCard
        playerpile.isEnabled = true
    }
}
