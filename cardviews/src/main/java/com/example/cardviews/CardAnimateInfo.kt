package com.example.cardviews

class CardAnimateInfo<T>(var speed: Long = 100, var reverse: Boolean = false, var listener: CardAnimationListener<T>)

class CardAnimationListener<T>(var start: (T) -> Unit = {},
                                 var end: (T) -> Unit = {},
                                 var repeat: (T) -> Unit = {},
                                 var cancel: (T) -> Unit = {})