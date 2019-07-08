package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Rect
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
import com.example.dragswipe.*
import com.example.funutilities.*
import crestron.com.deckofcards.Card
import crestron.com.deckofcards.Color
import crestron.com.deckofcards.Deck
import crestron.com.deckofcards.Suit
import kotlinx.android.synthetic.main.activity_card_play.*
import kotlinx.android.synthetic.main.card_item.view.*
import kotlin.random.Random

class CardPlayActivity : AppCompatActivity() {

    private var deck = Deck()
    private var otherList = arrayListOf<Card>()
    private lateinit var adapter: CardSwipeAdapter
    private lateinit var otherAdapter: CardSwipeAdapter
    private lateinit var helper: DragSwipeHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_play)

        background.setBackgroundColor(intent.getIntExtra("bgColor", 0))

        deck.deckListener = object : Deck.DeckListener {
            override fun draw(c: Card, size: Int) {
                Loged.i("$c")
            }
        }

        val layoutManagerPlayer = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        cards_to_show.setHasFixedSize(true)
        cards_to_show.layoutManager = layoutManagerPlayer
        adapter = CardSwipeAdapter(deck.getDeck(), this@CardPlayActivity)
        cards_to_show.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(this, layoutManagerPlayer.orientation)
        cards_to_show.addItemDecoration(dividerItemDecoration)

        val layoutManagerOther = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        other_cards.setHasFixedSize(true)
        other_cards.layoutManager = layoutManagerOther
        otherAdapter = CardSwipeAdapter(arrayListOf(Card.BackCard), this@CardPlayActivity)
        other_cards.adapter = otherAdapter

        setDragStuff()

        class OverlapDecoration(private var horizontalOverlap: Int = -200) : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val itemPosition = parent.getChildAdapterPosition(view)
                if (itemPosition == 0) {
                    return
                }
                outRect.set(horizontalOverlap, 0, 0, 0)
            }
        }

        val bitmap = BitmapFactory.decodeResource(resources, Card.BackCard.getImage(this))
        cards_to_show.addItemDecoration(OverlapDecoration((-bitmap.width / 1.5).toInt()))

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

        remove_number.setOnLongClickListener {
            //addToOther()
            shuffleAdapter()

            true
        }

        if(Random.nextInt(0, 10) > 11) {
            cards_to_show.removeDragSwipeHelper(helper)
            cards_to_show.attachDragSwipeHelper(helper)
        }

    }

    private fun shuffleAdapter() {
        adapter.shuffleItems()
        Loged.i("First item: ${adapter.getFirstItem()}\n" +
        "Middle item: ${adapter.getMiddleItem()}\n" +
        "Last item: ${adapter.getLastItem()}")
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
        helper = DragSwipeUtils.setDragSwipeUp(otherAdapter, other_cards, Direction.START.or(Direction.END), Direction.DOWN.value)
    }

    private fun removeNumber() {
        deck.removeNumber(5)
        setCardAdapters()
        DragSwipeUtils.disableDragSwipe(helper)
    }

    private fun addToOther() {
        otherAdapter.addItem(Card.RandomCard, otherAdapter.list.size/2)
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

        adapter.setListNotify(spades.getDeck())
        otherAdapter.setListNotify(clubs.getDeck())
    }

    private fun setCardAdapters() {
        adapter.setListNotify(deck.getDeck())
        otherAdapter.setListNotify(otherList)
    }

    private fun setDragStuff() {
        // Setup ItemTouchHelper
        DragSwipeUtils.setDragSwipeUp(adapter, cards_to_show, ItemTouchHelper.START.or(ItemTouchHelper.END), Direction.UP.value, dragSwipeActions = object : DragSwipeActions<Card, ViewHolder> {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
                dragSwipeAdapter: DragSwipeAdapter<Card, ViewHolder>
            ) {
                super.onMove(recyclerView, viewHolder, target, dragSwipeAdapter)
                Loged.d("${viewHolder.adapterPosition} to ${target.adapterPosition}")
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Direction,
                dragSwipeAdapter: DragSwipeAdapter<Card, ViewHolder>
            ) {
                Loged.d("${viewHolder.adapterPosition} and it was swiped $direction")
                super.onSwiped(viewHolder, direction, dragSwipeAdapter)
            }
        })
        helper = DragSwipeUtils.setDragSwipeUp(otherAdapter, other_cards, Direction.START + Direction.END, Direction.DOWN.value)
    }

    class CardSwipeAdapter(
        stuff: ArrayList<Card>,
        var context: Context
    ) : DragSwipeAdapter<Card, ViewHolder>(stuff) {

        // Gets the number of animals in the list
        override fun getItemCount(): Int {
            return list.size
        }

        // Inflates the item views
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_item, parent, false))
        }

        // Binds each animal in the ArrayList to a view
        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.cardInfo.setImageResource(list[position].getImage(context))
            holder.cardInfo.setColor(Random.nextColor())
        }

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val cardInfo: ImageView = view.cardImage!!
    }

}
