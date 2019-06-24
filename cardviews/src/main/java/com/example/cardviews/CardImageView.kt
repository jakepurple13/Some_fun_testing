package com.example.cardviews

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import crestron.com.deckofcards.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CardImageView : ImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var card: Card? = null
        set(value) {
            field = value
            if (!showBack) {
                animateCard()
            }
        }

    var showBack: Boolean = false
        set(value) {
            field = value
            if (value) {
                animateCard(cardToChange = Card.BackCard)
            } else {
                animateCard()
            }
        }

    var animateOnChange: Boolean = true

    var cardAnimateInfo: CardAnimateInfo<CardImageView> =
        CardAnimateInfo(animate().duration, false, CardAnimationListener())

    private fun animateCard(
        cardToChange: Card = card!!,
        animateInfo: CardAnimateInfo<CardImageView> = cardAnimateInfo
    ) = GlobalScope.launch(Dispatchers.Main) {
        if (animateOnChange) {
            val reverse = animateInfo.reverse
            val speed = animateInfo.speed
            rotationY = if (reverse) 360f else 0f
            animate().setDuration(speed).rotationY(if (reverse) 270f else 90f)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {
                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        setImageResource(cardToChange.getImage(context))
                        rotationY = if (reverse) 90f else 270f
                        animate().rotationY(if (reverse) 0f else 360f).setListener(object : Animator.AnimatorListener {
                            override fun onAnimationRepeat(animation: Animator?) {
                                animateInfo.listener.repeat(this@CardImageView)
                            }

                            override fun onAnimationEnd(animation: Animator?) {
                                animateInfo.listener.end(this@CardImageView)
                            }

                            override fun onAnimationCancel(animation: Animator?) {
                                animateInfo.listener.cancel(this@CardImageView)
                            }

                            override fun onAnimationStart(animation: Animator?) {
                                animateInfo.listener.start(this@CardImageView)
                            }

                        })
                    }

                    override fun onAnimationStart(p0: Animator?) {
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                    }

                })
        } else {
            setImageResource(cardToChange.getImage(context))
        }
    }

}