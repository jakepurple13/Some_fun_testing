package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dragswipe.*
import com.example.funutilities.get
import com.example.funutilities.shuffleItems
import kotlinx.android.synthetic.main.activity_show_drag_swipe.*
import kotlinx.android.synthetic.main.show_info_layout.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class ShowDragSwipeActivity : AppCompatActivity() {

    lateinit var adapter: ShowAdapter
    var helper: DragSwipeHelper? = null
    val listOfDeleted = arrayListOf<ShowInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_drag_swipe)

        show_list.setHasFixedSize(true)
        show_list.layoutManager = LinearLayoutManager(this)
        show_list.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        fun getStuffAsync() = GlobalScope.launch {
            Loged.w("Starting to get the stuff from ${Source.RECENT_CARTOON.link}")
            try {
                val show = ShowApi(Source.RECENT_CARTOON)
                val list = show.showInfoList
                Loged.i("$list")
                val episodeApi = EpisodeApi(list[0])
                val link = episodeApi.episodeList[0].getVideoLink()
                Loged.wtf(link)
                runOnUiThread {
                    adapter = ShowAdapter(list, this@ShowDragSwipeActivity) {
                        show_list.smoothScrollToPosition(adapter.itemCount-1)
                    }
                    show_list.adapter = adapter
                    helper =
                        DragSwipeUtils.setDragSwipeUp(
                            adapter,
                            show_list,
                            Direction.UP + Direction.DOWN,
                            Direction.START + Direction.END,
                            object : DragSwipeActions<ShowInfo, ViewHolder> {
                                override fun onSwiped(
                                    viewHolder: RecyclerView.ViewHolder,
                                    direction: Int,
                                    dragSwipeAdapter: DragSwipeAdapter<ShowInfo, ViewHolder>
                                ) {
                                    listOfDeleted+=dragSwipeAdapter[viewHolder.adapterPosition]
                                    super.onSwiped(viewHolder, direction, dragSwipeAdapter)
                                }
                            }
                        )
                }
            } catch (e: SocketTimeoutException) {
                Loged.e(e.localizedMessage!!)
            }
        }

        getStuffAsync().start()

        shuffle_button.setOnClickListener {
            adapter.shuffleItems()
        }

        add_button.setOnClickListener {
            adapter.addItems(listOfDeleted)
            listOfDeleted.clear()
        }

    }

    class ShowAdapter(stuff: ArrayList<ShowInfo>, var context: Context, var scrollToEnd: () -> Unit) :
        DragSwipeAdapter<ShowInfo, ViewHolder>(stuff) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.show_info_layout,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.title.text = list[position].name
            holder.description.text = ""
            holder.title.setOnClickListener {
                GlobalScope.launch {
                    val episodeApi = EpisodeApi(list[0])
                    val link = episodeApi.episodeList[0].getVideoLink()
                    Loged.wtf(link)
                    GlobalScope.launch(Dispatchers.Main) {
                        holder.description.text = link
                    }
                }
            }
        }

        override fun addItems(items: Collection<ShowInfo>, position: Int) {
            super.addItems(items, position)
            scrollToEnd()
        }

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.show_title!!
        val description: TextView = view.show_des!!
    }

}
