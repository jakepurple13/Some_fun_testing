package com.example.myapplication

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import crestron.com.deckofcards.Card
import crestron.com.deckofcards.CardDescriptor
import crestron.com.deckofcards.Deck
import kotlinx.android.synthetic.main.activity_bubble.*

class BubbleActivity : AppCompatActivity() {

    private val rgbMAX = 255

    var red = 0
        set(value) {
            field = value
            bgChange()
        }
    var green = 0
        set(value) {
            field = value
            bgChange()
        }
    var blue = 0
        set(value) {
            field = value
            bgChange()
        }
    var alpha = 0
        set(value) {
            field = value
            bgChange()
        }

    val deck = Deck(shuffler = true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bubble)

        deck.deckListener = object : Deck.DeckListener {
            @SuppressLint("SetTextI18n")
            override fun draw(c: Card, size: Int) {
                if(size<=1) {
                    deck addDeck Deck(shuffler = true)
                }
                cardView.setImageResource(c.getImage(this@BubbleActivity))
                cardText.text = "$c\n$size cards left"
            }

            @SuppressLint("SetTextI18n")
            override fun cardAdded(c: Collection<Card>) {
                super.cardAdded(c)
                cardText.text = "Adding..."
            }
        }

        cardView.setOnClickListener {
            deck.draw()
        }

        redBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                red = progress
            }
        })

        greenBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                green = progress
            }
        })

        blueBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                blue = progress
            }
        })

        alphaBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                alpha = progress
            }
        })

        writeout.setOnClickListener {
            Card.cardDescriptor = CardDescriptor.WRITTEN_OUT
        }

        symbolout.setOnClickListener {
            Card.cardDescriptor = CardDescriptor.SYMBOL
        }

        unicodeout.setOnClickListener {
            Card.cardDescriptor = CardDescriptor.UNICODE_SYMBOL
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bgChange() {
        val color = Color.argb(alpha, red, green, blue)
        background.setBackgroundColor(color)
        redValue.text = "R: $red"
        greenValue.text = "G: $green"
        blueValue.text = "B: $blue"
        alphaValue.text = "A: $alpha"
        val textColor = getComplimentaryColor(color)
        redValue.setTextColor(textColor)
        greenValue.setTextColor(textColor)
        blueValue.setTextColor(textColor)
        alphaValue.setTextColor(textColor)
        cardText.setTextColor(textColor)
        //Log.e("Tag", "${getBGColor()} and ${Color.argb(alpha, red, green, blue)}")
    }

    private fun getComplimentaryColor(color: Int): Int {
        return Color.rgb(
            rgbMAX - Color.red(color),
            rgbMAX - Color.green(color),
            rgbMAX - Color.blue(color)
        )
    }

}
