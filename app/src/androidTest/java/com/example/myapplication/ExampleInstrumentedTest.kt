package com.example.myapplication

import android.text.Spannable
import androidx.core.graphics.toColor
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.funutilities.getComplimentaryColor
import com.example.showapi.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        //val appContext = InstrumentationRegistry.getTargetContext()
        //assertEquals("com.example.myapplication", appContext.packageName)
        Loged.w("asdf")
        Loged.wtf("asdf")
        Loged.TAG = "asdf"
        Loged.SHOW_PRETTY = true
        val y = 4
        println("${y.getComplimentaryColor()}")
        println("${y.toColor().getComplimentaryColor()}")
    }

    @Test
    fun useAppContexts() {
        val show = com.example.showapi.ShowApi(Source.getSourceFromUrl(com.example.showapi.Source.RECENT_CARTOON.link))
        val list = show.showInfoList
        val ep = com.example.showapi.EpisodeApi(list[0])
        println(ep.description)
        println(ep.episodeList[0].getVideoLink())

        fun imgascii() {
            ImgAscii()
                .quality(AsciiQuality.BEST)
                .url(ep.image)
                .convert(object : ImgAscii.Listener {
                    override fun onProgress(percentage: Int) {
                        println("$percentage")
                    }

                    override fun onResponse(response: Spannable?) {
                        println("$response")
                    }

                })
        }

        runBlocking {
            withContext(Dispatchers.Main) {
                imgascii()
            }
        }

    }

    @Test
    fun netTest2() {
        runBlocking {
            //println("$list")
            withContext(Dispatchers.Default) {
                val show = com.example.showapi.ShowApi(com.example.showapi.Source.CARTOON_MOVIES)
                val list = show.showInfoList
                val ep = com.example.showapi.EpisodeApi(list[0])
                println(ep.episodeList[0].getVideoLinks())
            }
        }
    }

}
