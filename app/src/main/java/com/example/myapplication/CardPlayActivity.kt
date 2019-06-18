package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import crestron.com.deckofcards.Card
import crestron.com.deckofcards.Color
import crestron.com.deckofcards.Deck
import crestron.com.deckofcards.Suit
import kotlinx.android.synthetic.main.activity_card_play.*
import kotlinx.android.synthetic.main.card_item.view.*

class CardPlayActivity : AppCompatActivity() {

    var deck = Deck()
    var otherList = arrayListOf<Card>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_play)

        deck.deckListener = object : Deck.DeckListener {
            override fun draw(c: Card, size: Int) {
                Loged.i("$c")
            }
        }

        val layoutManagerPlayer = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        cards_to_show.setHasFixedSize(true)
        cards_to_show.layoutManager = layoutManagerPlayer
        cards_to_show.adapter = CardAdapter(deck.getDeck(), this@CardPlayActivity)

        val layoutManagerOther = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        other_cards.setHasFixedSize(true)
        other_cards.layoutManager = layoutManagerOther
        other_cards.adapter = CardAdapter(arrayListOf(Card.BackCard), this@CardPlayActivity)

        random_card_first.setOnClickListener {
            randomCard()
        }

        random_card_first.setOnLongClickListener {
            firstCard()
            true
        }

        random_middle_last.setOnClickListener {
            middleCard()
        }

        random_middle_last.setOnLongClickListener {
            lastCard()
            true
        }

        change_deck.setOnCheckedChangeListener { radioGroup, _ ->
            otherList.clear()
            val radio: RadioButton = findViewById(radioGroup.checkedRadioButtonId)
            deck = when(radio.tag.toString().trim().toInt()) {
                0 -> Deck()
                1 -> Deck.numberOnly(5,7)
                2 -> Deck.suitOnly(Suit.SPADES, Suit.HEARTS)
                3 -> Deck.colorOnly(Color.BLACK)
                4 -> Deck.randomDeck()
                5 -> {
                    Deck(shuffler = true,
                        numberOfDecks = 2,
                        seed = 1L,
                        cards = arrayListOf(Card.RandomCard),
                        deck = Deck(numberOfDecks = 0, cards = arrayListOf(Card.RandomCard)),
                        deckListener = object : Deck.DeckListener {
                            override fun draw(c: Card, size: Int) {
                                Loged.d("$c and $size")
                            }

                            override fun shuffle() {
                                super.shuffle()
                                Loged.d("Shuffling.")
                            }

                            override fun cardAdded(c: Collection<Card>) {
                                super.cardAdded(c)
                                Loged.d("$c")
                            }
                        })
                }
                else -> Deck()
            }

            if(deck.deckListener==null)
                deck.deckListener = object : Deck.DeckListener {
                    override fun draw(c: Card, size: Int) {
                        Loged.i("$c")
                    }
                }

            otherList.add(Card.BackCard)
            setCardAdapters()
        }

        unary_op.setOnClickListener {
            unaryPlusCard()
        }

        unary_op.setOnLongClickListener {
            unaryMinusCard()
            true
        }

        not_replace.setOnClickListener {
            notCard()
        }

        not_replace.setOnLongClickListener {
            replaceCard()
            true
        }

        get_group.setOnClickListener {
            getCards()
        }

        get_group.setOnLongClickListener {
            grouping()
            true
        }

        sorting_group.setOnCheckedChangeListener { radioGroup, i ->
            val radio: RadioButton = findViewById(radioGroup.checkedRadioButtonId)
            sorting(radio.tag.toString().trim().toInt())
        }

        remove_color_suit.setOnClickListener {
            removeColor()
        }

        remove_color_suit.setOnLongClickListener {
            removeSuit()
            true
        }

        remove_number.setOnClickListener {
            removeNumber()
        }

    }

    fun randomCard() {
        otherList.clear()
        val card = deck.randomCard ?: Card.BackCard
        otherList.add(card)
        setCardAdapters()
    }

    fun firstCard() {
        otherList.clear()
        val card = deck.first ?: Card.BackCard
        otherList.add(card)
        setCardAdapters()
    }

    fun middleCard() {
        otherList.clear()
        val card = deck.middle ?: Card.BackCard
        otherList.add(card)
        setCardAdapters()
    }

    fun lastCard() {
        otherList.clear()
        val card = deck.last ?: Card.BackCard
        otherList.add(card)
        setCardAdapters()
    }

    fun unaryPlusCard() {
        otherList.clear()
        val card = +deck
        otherList.add(card)
        setCardAdapters()
    }

    fun unaryMinusCard() {
        otherList.clear()
        val card = -deck
        otherList.add(card)
        setCardAdapters()
    }

    fun notCard() {
        otherList.clear()
        deck = !deck
        setCardAdapters()
    }

    fun replaceCard() {
        deck.replaceCard(Card(Suit.SPADES, 5), Card(Suit.CLUBS, 7)) { 1..it.size-2 }
        setCardAdapters()
    }

    fun removeColor() {
        deck.removeColor(Color.RED)
        setCardAdapters()
    }

    fun removeSuit() {
        deck.removeSuit(Suit.randomSuit())
        setCardAdapters()
    }

    fun removeNumber() {
        deck.removeNumber(5)
        setCardAdapters()
    }

    fun sorting(i: Int) {
        when(i) {
            0 -> deck.sortByValue()
            1 -> deck.sortBySuit()
            2 -> deck.sortByColor()
            3 -> deck.sortToReset()
            4 -> deck.shuffle(1L)
            5 -> deck.trueRandomShuffle()
        }
        setCardAdapters()
    }

    fun getCards() {
        otherList.clear()
        val card = deck.getCards(5)
        otherList.addAll(card)
        setCardAdapters()
    }

    fun grouping() {
        val valueDecks = deck.groupBy { it.suit }
        val spades = Deck(cards = valueDecks[Suit.SPADES], numberOfDecks = 0)
        val clubs = Deck(cards = valueDecks[Suit.CLUBS], numberOfDecks = 0)

        cards_to_show.adapter = CardAdapter(spades.getDeck(), this@CardPlayActivity)
        other_cards.adapter = CardAdapter(clubs.getDeck(), this@CardPlayActivity)

    }

    private fun setCardAdapters() {
        cards_to_show.adapter = CardAdapter(deck.getDeck(), this@CardPlayActivity)
        other_cards.adapter = CardAdapter(otherList, this@CardPlayActivity)
    }

    class CardAdapter(private var stuff: ArrayList<Card>,
                           var context: Context
    ) : RecyclerView.Adapter<ViewHolder>() {

        // Gets the number of animals in the list
        override fun getItemCount(): Int {
            return stuff.size
        }

        // Inflates the item views
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_item, parent, false))
        }

        // Binds each animal in the ArrayList to a view
        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.cardInfo.setImageResource(stuff[position].getImage(context))
        }

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val cardInfo: ImageView = view.cardImage!!
    }
}
