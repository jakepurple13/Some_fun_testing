package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Spannable
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
import com.example.showapi.EpisodeApi
import com.example.showapi.ShowApi
import com.example.showapi.ShowInfo
import com.example.showapi.Source
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

        fun bleh() {
            val show = ShowApi(Source.getSourceFromUrl(Source.RECENT_CARTOON.link))
            val list = show.showInfoList
            Loged.i("$list")
            val episodeApi = EpisodeApi(list[0])
            val link = episodeApi.episodeList[0].getVideoLink()
            Loged.wtf(link)
            Loged.wtf("${episodeApi.description} + ${episodeApi.image}")
        }

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
                    /*ImgAscii()
                        .quality(AsciiQuality.BEST)
                        //.color(true)
                        .url(episodeApi.image)
                        .convert(object : ImgAscii.Listener {
                            override fun onProgress(percentage: Int) {
                                println("$percentage")
                            }

                            override fun onResponse(response: Spannable?) {
                                println("$response")
                            }

                        })*/
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

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.title.text = "$position: ${list[position].name}\n${list[position].url}"
            holder.description.text = ""
            holder.title.setOnClickListener {
                GlobalScope.launch {
                    val episodeApi = EpisodeApi(list[position])
                    val link = episodeApi.episodeList[0].getVideoLink()
                    val epInfo = episodeApi.toString()
                    Loged.wtf(link)
                    GlobalScope.launch(Dispatchers.Main) {
                        holder.description.text = "$epInfo\n$link"
                    }
                }
            }
            holder.title.setOnLongClickListener {
                GlobalScope.launch {
                    val episodeApi = EpisodeApi(list[position])
                    GlobalScope.launch(Dispatchers.Main) {
                        ImgAscii()
                            .quality(AsciiQuality.WORST)
                            .color(true)
                            .url(episodeApi.image)
                            .convert(object : ImgAscii.Listener {
                                override fun onProgress(percentage: Int) {
                                    print("$percentage%\t")
                                }

                                override fun onResponse(response: Spannable?) {
                                    println()
                                    GlobalScope.launch(Dispatchers.Main) {
                                        holder.description.text = response!!
                                    }
                                }

                            })
                    }
                }
                true
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
