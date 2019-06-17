package com.example.myapplication

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Html
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import crestron.com.deckofcards.Card
import crestron.com.deckofcards.CardDescriptor
import crestron.com.deckofcards.Deck
import crestron.com.deckofcards.Suit
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import java.util.*


class MainActivity : AppCompatActivity() {

    interface HelloRepo {
        fun giveHello(): String
        fun addMore(multi: Int)
        fun drawFromDeck(): Card
    }

    class HelloRepoImpl(num: Int, val deck: Deck) : HelloRepo {
        var num: Int = num
            set(value) {
                Loged.i("It was changed from $field to $value", showPretty = false)
                field = value
            }

        init {
            deck.deckListener = object : Deck.DeckListener {
                override fun draw(c: Card, size: Int) {
                    if (size <= 0) {
                        Loged.e("Resetting deck")
                        deck += Deck(shuffler = true)
                    }
                }

            }
        }

        override fun giveHello(): String = "Hello World $num"
        override fun addMore(multi: Int) {
            num += (5 + multi)
        }

        override fun drawFromDeck(): Card {
            return deck.draw()
        }

    }

    class MySimplePresenter(val repo: HelloRepo) {
        fun sayHello() = "${repo.giveHello()} from $this"
        fun adding(multi: Int) = repo.addMore(multi)
        fun draw() = repo.drawFromDeck()

        override fun toString(): String {
            return "MySimplePresenter"
        }
    }

    val firstPres: MySimplePresenter by inject()
    var cardTotal = 0
        @SuppressLint("SetTextI18n")
        set(value) {
            field = value
            textView.text = "Card Total: $field"
        }

    interface Base {
        fun print()
    }

    class BaseImpl(val x: Int) : Base {
        override fun print() {
            print(x)
        }
    }

    class Derived(b: Base) : Base by b

    private var gen = Random()

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

    private var holdCard: Card? = null

    private fun ImageView.setResourceImage(card: Card) {
        if (holdCard != card) {
            rotationY = 0f
            animate().rotationY(90f).setDuration(100).setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    setImageResource(card.getImage(this@MainActivity))
                    rotationY = 270f
                    animate().rotationY(360f).setListener(null)
                }

                override fun onAnimationCancel(animation: Animator) {}
            })
            Loged.v("$card")
        }
        textView.text = "$card"
        holdCard = card
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cardTotal += 3
        firstPres.draw()

        AnnoTester().mained()

        val b = BaseImpl(10)
        Derived(b).print()

        Loged.a(firstPres.sayHello())

        button.setOnClickListener {
            /*val c = firstPres.draw()
            cardTotal+=c.value
            cardImage.setImageResource(c.getImage(this@MainActivity))*/

            val i = Intent(this@MainActivity, NewFeatureTest::class.java)
            i.putExtra("bgColor", Color.argb(alpha, red, green, blue))
            startActivity(i)
        }

        button3.setOnClickListener {
            val i = Intent(this@MainActivity, SecondActivity::class.java)
            i.putExtra("bgColor", Color.argb(alpha, red, green, blue))
            startActivity(i)
        }

        button4.setOnClickListener {
            val i = Intent(this@MainActivity, WarActivity::class.java)
            i.putExtra("bgColor", Color.argb(alpha, red, green, blue))
            startActivity(i)
        }

        cardImage.setOnClickListener {
            val suit = when {
                green < 63 -> Suit.SPADES
                green in 64..125 -> Suit.CLUBS
                green in 126..189 -> Suit.DIAMONDS
                green in 190..255 -> Suit.HEARTS
                else -> Suit.SPADES
            }
            cardImage.setResourceImage(Card(suit, (blue % 13) + 1))
        }

        cardImage.setOnLongClickListener {
            if(holdCard == null)
                cardImage.performClick()
            cardImage.setResourceImage(!holdCard!!)
            true
        }

        randomcolor.setOnClickListener {
            val cardColor = if (red > 128) crestron.com.deckofcards.Color.RED else crestron.com.deckofcards.Color.BLACK
            val c = Card.randomCardByColor(cardColor)
            cardImage.setResourceImage(c)
        }

        randomcard.setOnClickListener {
            cardImage.setResourceImage(Card.RandomCard)
        }

        randomcard.setOnLongClickListener {
            cardImage.setResourceImage(!Card.RandomCard)
            true
        }

        randomsuit.setOnClickListener {
            val suit = when {
                green < 63 -> Suit.SPADES
                green in 64..125 -> Suit.CLUBS
                green in 126..189 -> Suit.DIAMONDS
                green in 190..255 -> Suit.HEARTS
                else -> Suit.SPADES
            }
            val c = Card.randomCardBySuit(suit)
            cardImage.setResourceImage(c)
        }

        randomvalue.setOnClickListener {
            val c = Card.randomCardByValue((blue % 13) + 1)
            cardImage.setResourceImage(c)
        }

        redBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                firstPres.adding(redBar.progress)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Loged.a(firstPres.sayHello(), showPretty = false)
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                red = progress
            }
        })

        greenBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                firstPres.adding(greenBar.progress)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Loged.a(firstPres.sayHello(), showPretty = false)
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                green = progress
            }
        })

        blueBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                firstPres.adding(blueBar.progress)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Loged.a(firstPres.sayHello(), showPretty = false)
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                blue = progress
            }
        })

        alphaBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                firstPres.adding(alphaBar.progress)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Loged.a(firstPres.sayHello(), showPretty = false)
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                alpha = progress
            }
        })

        alphaBar.progress = gen.nextInt(255)
        redBar.progress = gen.nextInt(255)
        greenBar.progress = gen.nextInt(255)
        blueBar.progress = gen.nextInt(255)

        random_bar_color.setOnClickListener {
            alphaBar.progress = gen.nextInt(255)
            redBar.progress = gen.nextInt(255)
            greenBar.progress = gen.nextInt(255)
            blueBar.progress = gen.nextInt(255)
        }

        redBar.seekListener = SmoothSeekBar.SmoothSeekListener { value -> red = value }
        greenBar.seekListener = SmoothSeekBar.SmoothSeekListener { value -> green = value }
        blueBar.seekListener = SmoothSeekBar.SmoothSeekListener { value -> blue = value }
        alphaBar.seekListener = SmoothSeekBar.SmoothSeekListener { value -> alpha = value }

        writeout.setOnClickListener {
            Card.cardDescriptor = CardDescriptor.WRITTEN_OUT
            if (holdCard != null)
                cardImage.setResourceImage(holdCard!!)
        }

        symbolout.setOnClickListener {
            Card.cardDescriptor = CardDescriptor.SYMBOL
            if (holdCard != null)
                cardImage.setResourceImage(holdCard!!)
        }

        unicodeout.setOnClickListener {
            Card.cardDescriptor = CardDescriptor.UNICODE_SYMBOL
            if (holdCard != null)
                cardImage.setResourceImage(holdCard!!)
        }

        textView.setTextColor(Color.BLACK)

    }

    @SuppressLint("SetTextI18n")
    private fun bgChange() {
        val color = Color.argb(alpha, red, green, blue)
        val complimentColor =
            getComplimentaryColor(Color.rgb(red, green, blue))//Color.argb(if(alpha>128) 0 else 255, red, green, blue))
        background.setBackgroundColor(color)
        cardImage.setColorFilter(color, PorterDuff.Mode.SCREEN)
        val infoString = Html.fromHtml(
            "Random Color (R$red)" +
                    "<p${if (red > 128) " style=\"color:red;\">" else ">"}R&gt;128 == RED</p>" +
                    "<p${if (red <= 128) " style=\"color:red;\">" else ">"}R&lt;128 == BLACK</p>" +
                    "Random Suit (G$green)" +
                    "<p${if (green <= 63) " style=\"color:red;\">" else ">"}G&lt;63 == SPADES</p>" +
                    "<p${if (green in 64..125) " style=\"color:red;\">" else ">"}64&lt;G&lt;125 == CLUBS</p>" +
                    "<p${if (green in 126..189) " style=\"color:red;\">" else ">"}126&lt;G&lt;189 == DIAMONDS</p>" +
                    "<p${if (green >= 190) " style=\"color:red;\">" else ">"}G&gt;190 == HEARTS</p>" +
                    "Random Value (B$blue)" +
                    "<br>(B%13) + 1 = ${(blue % 13) + 1}" +
                    "<br>(A$alpha)", 0
        )
        main_info.text = infoString
        main_info.setTextColor(complimentColor)
        fun equalDigits(color: Int): String {
            return when {
                color < 10 -> "00"
                color < 100 -> "0"
                else -> ""
            } + "$color"
        }
        colorbarinfo.text = "R${equalDigits(red)}" +
                "\nG${equalDigits(green)}" +
                "\nB${equalDigits(blue)}" +
                "\nA${equalDigits(alpha)}"
        colorbarinfo.setTextColor(complimentColor)
        textView.setTextColor(complimentColor)
    }

    private fun getComplimentaryColor(color: Int): Int {
        val rgbMAX = 255
        return Color.rgb(
            rgbMAX - Color.red(color),
            rgbMAX - Color.green(color),
            rgbMAX - Color.blue(color)
        )
    }

}
