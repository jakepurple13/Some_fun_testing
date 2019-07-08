package com.example.myapplication

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cardutilities.fullInfo
import com.example.cardviews.CardAnimateInfo
import com.example.cardviews.CardAnimationListener
import com.example.cardviews.CardProgressType
import com.example.dragswipe.*
import com.example.funutilities.RecyclerViewDragSwipeManager
import com.example.funutilities.shuffleItems
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial
import crestron.com.deckofcards.Card
import crestron.com.deckofcards.Deck
import crestron.com.deckofcards.nextCard
import kotlinx.android.synthetic.main.activity_new_feature_test.*
import kotlinx.android.synthetic.main.card_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class NewFeatureTest : AppCompatActivity() {

    private lateinit var adapter: TestAdapter
    private val lists = arrayListOf<Card>()
    private var count = 0
    private lateinit var manager: RecyclerViewDragSwipeManager

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_feature_test)
        background.setBackgroundColor(intent.getIntExtra("bgColor", 0))
        bubblenoti.setOnClickListener {
            GlobalScope.launch {
                delay(200)
                sendNoti(this@NewFeatureTest)
            }
        }
        confirmdialog.setOnClickListener {
            adapter.shuffleItems()
        }

        confirmdialog.setOnLongClickListener {
            //lists += count++
            lists += Deck().getDeck()
            adapter.setListNotify(lists)
            true
        }

        manager = RecyclerViewDragSwipeManager(fun_recycler)

        val layoutManagerOther = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        fun_recycler.setHasFixedSize(true)
        //fun_recycler.layoutManager = layoutManagerOther
        fun_recycler.layoutManager = GridLayoutManager(this, 3)
        adapter = TestAdapter(lists, this@NewFeatureTest)
        fun_recycler.adapter = adapter
        /*DragSwipeUtils.setDragSwipeUp(
            adapter,
            fun_recycler,
            Direction.UP + Direction.DOWN,
            Direction.START + Direction.END
        )*/

        val callBack = SwipeToDelete(
            adapter,
            Direction.UP + Direction.DOWN + Direction.START + Direction.END,
            Direction.START + Direction.END,
            this@NewFeatureTest
        )

        val helper2 = DragSwipeUtils.setDragSwipeUp(
            fun_recycler,
            callBack,
            object : DragSwipeActions<Card, ViewHolders> {
                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Direction,
                    dragSwipeAdapter: DragSwipeAdapter<Card, ViewHolders>
                ) {
                    super.onSwiped(viewHolder, direction, dragSwipeAdapter)
                    Loged.wtf("$direction")

                    if (direction == Direction.START) {
                        Loged.i("Went left")
                    } else if (direction == Direction.END) {
                        Loged.i("Went right")
                    }
                }
            })

        val helper = DragSwipeUtils.setDragSwipeUp(
            adapter,
            fun_recycler,
            { _, _ ->
                Direction.UP + Direction.DOWN + Direction.START + Direction.END
            },
            { _, _ ->
                Direction.START + Direction.END
            },
            object : DragSwipeActions<Card, ViewHolders> {
                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Direction,
                    dragSwipeAdapter: DragSwipeAdapter<Card, ViewHolders>
                ) {
                    super.onSwiped(viewHolder, direction, dragSwipeAdapter)
                    Loged.wtf("$direction")

                    if (direction == Direction.START) {
                        Loged.i("Went left")
                    } else if (direction == Direction.END) {
                        Loged.i("Went right")
                    }
                }
            }
        )

        val bool = Random.nextBoolean()

        manager.dragSwipeHelper = if(bool) {
            Loged.wtf("First Helper")
            helper
        } else {
            Loged.wtf("Custom Helper")
            helper2
        }

        manager.dragSwipedEnabled = true

        cardImage.setOnClickListener {
            cardImage.card = Card.RandomCard
        }

        cardImage.setOnLongClickListener {
            cardImage.showBack = !cardImage.showBack
            true
        }

        cardImage.animateOnChange = true

        cardImage.cardAnimateInfo = CardAnimateInfo(100, false, CardAnimationListener(end = {
            Loged.w("${it.card} and ${it.card?.fullInfo()}")
        }))

        cardprogress.card = Card.RandomCard

        cardprogress.cardAnimateInfo.reverse = true
        cardprogress.animateToReset = false
        cardprogress.type = CardProgressType.ROTATE
        cardprogress.cardAnimateInfo.listener = CardAnimationListener(end = {
            Loged.w("${it.card} and ${it.max} and ${it.progress}")
        })

        Loged.i("${cardprogress.max}")

        cardprogress.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                for (i in 1..100) {
                    cardprogress.progress = i
                    //delay(cardprogress.animate().duration*2)
                    //delay(100)
                }
            }
        }

        cardprogress.setOnLongClickListener {
            cardprogress.animate().cancel()
            cardprogress.card = Random.nextCard()
            true
        }
    }

    class TestAdapter(stuff: ArrayList<Card>, val context: Context) :
        DragSwipeAdapter<Card, ViewHolders>(stuff) {
        override fun getItemCount(): Int {
            return list.size
        }

        // Inflates the item views
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolders {
            return ViewHolders(
                LayoutInflater.from(context).inflate(
                    R.layout.card_item,
                    parent,
                    false
                )
            )
        }

        // Binds each animal in the ArrayList to a view
        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolders, position: Int) {
            //holder.cardInfo.setImageResource(Card.RandomCard.getImage(context))
            holder.cardInfo.setImageResource(list[position].getImage(context))
            holder.cardInfo.setOnClickListener {
                Toast.makeText(
                    context,
                    "Number: ${list[position]} at position ${holder.adapterPosition}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    class ViewHolders(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val cardInfo: ImageView = view.cardImage!!
    }

    fun sendNoti(context: Context) {
        val channel = NotificationChannel("asdf1", "asdf1", NotificationManager.IMPORTANCE_HIGH)
        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.createNotificationChannel(channel)

        // Create bubble intent
        val target = Intent(context, BubbleActivity::class.java)
        val bubbleIntent = PendingIntent.getActivity(context, 0, target, 0 /* flags */)

        // Create bubble metadata
        val bubbleData = Notification.BubbleMetadata.Builder()
            .setDesiredHeight(600)
            // Note: although you can set the icon_team is not displayed in Q Beta 2
            .setIcon(Icon.createWithResource(context, R.drawable.icon_teams_three))
            .setIntent(bubbleIntent)
            .build()

        // Create notification
        val chatBot = Person.Builder()
            .setBot(true)
            .setName("BubbleBot")
            .setImportant(true)
            .build()

        val builder = Notification.Builder(context, "asdf1")
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    1,
                    Intent(this@NewFeatureTest, NewFeatureTest::class.java),
                    0
                )
            )
            .setSmallIcon(R.drawable.icon_teams_three)
            //.setLargeIcon(Icon.createWithResource(context, R.drawable.icon_teams_three))
            .setBubbleMetadata(bubbleData)
            .addPerson(chatBot)

        // mNotificationId is a unique integer your app uses to identify the
        // notification. For example, to cancel the notification, you can pass its ID
        // number to NotificationManager.cancel().
        mNotificationManager.notify(1, builder.build())
    }

    class SwipeToDelete(
        dragSwipeAdapter: DragSwipeAdapter<Card, ViewHolders>,
        dragDirs: Int,
        swipeDirs: Int,
        val context: Context
    ) : DragSwipeManageAdapter<Card, ViewHolders>(dragSwipeAdapter, dragDirs, swipeDirs) {

        private val deleteIcon = IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_delete)
        private val intrinsicWidth = deleteIcon.intrinsicWidth
        private val intrinsicHeight = deleteIcon.intrinsicHeight
        private val background = ColorDrawable()
        private val backgroundColor = Color.parseColor("#00f44336")
        private val clearPaint =
            Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            if (viewHolder.adapterPosition%10 == 0) return 0
            return super.getMovementFlags(recyclerView, viewHolder)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val itemView = viewHolder.itemView
            val itemHeight = itemView.bottom - itemView.top
            val isCanceled = dX == 0f && !isCurrentlyActive

            if (isCanceled) {
                clearCanvas(
                    c,
                    itemView.right + dX,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat()
                )
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                return
            }

            // Draw the red delete background
            background.color = backgroundColor
            background.setBounds(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            background.draw(c)

            // Calculate position of delete icon
            val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
            val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
            val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
            val deleteIconRight = itemView.right - deleteIconMargin
            val deleteIconBottom = deleteIconTop + intrinsicHeight

            // Draw the delete icon
            deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
            deleteIcon.draw(c)

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

        private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
            c?.drawRect(left, top, right, bottom, clearPaint)
        }
    }

}
