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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import crestron.com.deckofcards.Card
import crestron.com.deckofcards.Color
import crestron.com.deckofcards.Deck
import crestron.com.deckofcards.Suit
import kotlinx.android.synthetic.main.activity_card_play.*
import kotlinx.android.synthetic.main.card_item.view.*

class CardPlayActivity : AppCompatActivity() {

    private var deck = Deck()
    private var otherList = arrayListOf<Card>()
    private lateinit var adapter: CardAdapter

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
        adapter = CardAdapter(deck.getDeck(), this@CardPlayActivity)
        cards_to_show.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(this, layoutManagerPlayer.orientation)
        cards_to_show.addItemDecoration(dividerItemDecoration)

        setDragStuff()

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
            deck = when (radio.tag.toString().trim().toInt()) {
                0 -> Deck()
                1 -> Deck.numberOnly(5, 7)
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

            if (deck.deckListener == null)
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

        sorting_group.setOnCheckedChangeListener { radioGroup, _ ->
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

    private fun randomCard() {
        otherList.clear()
        val card = deck.randomCard ?: Card.BackCard
        otherList.add(card)
        setCardAdapters()
    }

    private fun firstCard() {
        otherList.clear()
        val card = deck.first ?: Card.BackCard
        otherList.add(card)
        setCardAdapters()
    }

    private fun middleCard() {
        otherList.clear()
        val card = deck.middle ?: Card.BackCard
        otherList.add(card)
        setCardAdapters()
    }

    private fun lastCard() {
        otherList.clear()
        val card = deck.last ?: Card.BackCard
        otherList.add(card)
        setCardAdapters()
    }

    private fun unaryPlusCard() {
        otherList.clear()
        val card = +deck
        otherList.add(card)
        setCardAdapters()
    }

    private fun unaryMinusCard() {
        otherList.clear()
        val card = -deck
        otherList.add(card)
        setCardAdapters()
    }

    private fun notCard() {
        otherList.clear()
        deck = !deck
        setCardAdapters()
    }

    private fun replaceCard() {
        deck.replaceCard(Card(Suit.SPADES, 5), Card(Suit.CLUBS, 7)) { 1..it.size - 2 }
        setCardAdapters()
    }

    private fun removeColor() {
        deck.removeColor(Color.RED)
        setCardAdapters()
    }

    private fun removeSuit() {
        deck.removeSuit(Suit.randomSuit())
        setCardAdapters()
    }

    private fun removeNumber() {
        deck.removeNumber(5)
        setCardAdapters()
    }

    private fun sorting(i: Int) {
        when (i) {
            0 -> deck.sortByValue()
            1 -> deck.sortBySuit()
            2 -> deck.sortByColor()
            3 -> deck.sortToReset()
            4 -> deck.shuffle(1L)
            5 -> deck.trueRandomShuffle()
        }
        setCardAdapters()
    }

    private fun getCards() {
        otherList.clear()
        val card = deck.getCards(5)
        otherList.addAll(card)
        setCardAdapters()
    }

    private fun grouping() {
        val valueDecks = deck.groupBy { it.suit }
        val spades = Deck(cards = valueDecks[Suit.SPADES], numberOfDecks = 0)
        val clubs = Deck(cards = valueDecks[Suit.CLUBS], numberOfDecks = 0)

        adapter.stuff = spades.getDeck()
        adapter.notifyDataSetChanged()
        other_cards.adapter = CardAdapter(clubs.getDeck(), this@CardPlayActivity)
    }

    private fun setCardAdapters() {
        adapter.stuff = deck.getDeck()
        adapter.notifyDataSetChanged()
        other_cards.adapter = CardAdapter(otherList, this@CardPlayActivity)
    }

    private fun setDragStuff() {
        // Setup ItemTouchHelper
        val callback = DragManageAdapter(
            adapter, ItemTouchHelper.START.or(ItemTouchHelper.END),
            0
        )
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(cards_to_show)
    }

    class CardAdapter(
        var stuff: ArrayList<Card>,
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

        /**
         * Function called to swap dragged items
         */
        fun swapItems(fromPosition: Int, toPosition: Int) {
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    stuff[i] = stuff.set(i+1, stuff[i])
                }
            } else {
                for (i in fromPosition..toPosition + 1) {
                    stuff[i] = stuff.set(i-1, stuff[i])
                }
            }
            notifyItemMoved(fromPosition, toPosition)
        }

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val cardInfo: ImageView = view.cardImage!!
    }

    class DragManageAdapter(
        adapter: CardAdapter,
        dragDirs: Int,
        swipeDirs: Int
    ) :
        ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {
        private var nameAdapter = adapter

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            nameAdapter.swapItems(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        }

    }

}
