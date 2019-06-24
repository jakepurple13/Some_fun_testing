package com.example.cardviews

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar
import crestron.com.deckofcards.Card
import crestron.com.deckofcards.Deck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CardProgress : ProgressBar {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var card: Card = Card.BackCard
        set(value) {
            field = value
            indeterminateDrawable = context.getDrawable(field.getImage(context))
            progressDrawable = context.getDrawable(field.getImage(context))
            refreshDrawableState()
        }

    var cardAnimateInfo: CardAnimateInfo<CardProgress> =
        CardAnimateInfo(animate().duration, false, CardAnimationListener())

    var animateToReset = true

    var type: CardProgressType = CardProgressType.DECK

    private val deck: Deck by lazy {
        Deck(false).apply {
            sortToReset()
        }
    }

    /**
     * based on the Type you have you want to have a certain delay for them
     *
     * 1. [CardProgressType.ROTATE] -> does not need a delay
     * 2. [CardProgressType.DECK_FLIP] -> delay(card_progress.animate().duration*2)
     * 3. [CardProgressType.DECK] -> delay(100) (100 is minimum)
     */
    override fun setProgress(progress: Int) {
        super.setProgress(progress)
        try {
            when (type) {
                CardProgressType.ROTATE -> animateProgress(progress)
                CardProgressType.DECK -> animateDeckThrough(progress)
                CardProgressType.DECK_FLIP -> animateDeck(progress)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun animateDeckThrough(progress: Int) {
        card = deck[progress%52]
    }

    private fun animateDeck(progress: Int, animateInfo: CardAnimateInfo<CardProgress> = cardAnimateInfo) =
        GlobalScope.launch(
            Dispatchers.Main
        ) {
            val reverse = animateInfo.reverse
            val speed = animateInfo.speed
            val cardToChange = deck[progress % 52]
            rotationY = if (reverse) 360f else 0f
            animate().setDuration(speed).rotationY(if (reverse) 270f else 90f)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {
                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        card = cardToChange
                        rotationY = if (reverse) 90f else 270f
                        animate().rotationY(if (reverse) 0f else 360f).setListener(object : Animator.AnimatorListener {
                            override fun onAnimationRepeat(animation: Animator?) {
                                animateInfo.listener.repeat(this@CardProgress)
                            }

                            override fun onAnimationEnd(animation: Animator?) {
                                animateInfo.listener.end(this@CardProgress)
                            }

                            override fun onAnimationCancel(animation: Animator?) {
                                animateInfo.listener.cancel(this@CardProgress)
                            }

                            override fun onAnimationStart(animation: Animator?) {
                                animateInfo.listener.start(this@CardProgress)
                            }

                        })
                    }

                    override fun onAnimationStart(p0: Animator?) {
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                    }

                })
        }

    private fun animateProgress(progress: Int, animateInfo: CardAnimateInfo<CardProgress> = cardAnimateInfo) {
        val prog = ((progress * 360) / max).toFloat()
        animate().setDuration(animateInfo.speed).rotation(if (animateInfo.reverse) -prog else prog)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                    animateInfo.listener.repeat(this@CardProgress)
                }

                override fun onAnimationEnd(animation: Animator?) {
                    animateInfo.listener.end(this@CardProgress)
                    if (prog.toInt() == max && animateToReset) {
                        //this@CardProgress.progress = 0
                        rotation = 0f
                    }
                }

                override fun onAnimationCancel(animation: Animator?) {
                    animateInfo.listener.cancel(this@CardProgress)
                }

                override fun onAnimationStart(animation: Animator?) {
                    animateInfo.listener.start(this@CardProgress)
                }
            })
    }

}

enum class CardProgressType {
    DECK,
    ROTATE,
    DECK_FLIP
}