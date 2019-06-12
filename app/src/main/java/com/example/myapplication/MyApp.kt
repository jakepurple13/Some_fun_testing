package com.example.myapplication

import android.app.Application
import crestron.com.deckofcards.Deck
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Loged.FILTER_BY_CLASS_NAME = "com.example"

        val appMod = module {
            single<MainActivity.HelloRepo> { MainActivity.HelloRepoImpl(5, Deck(shuffler = true)) }
            factory { MainActivity.MySimplePresenter(get()) }
        }

        startKoin {
            androidLogger()
            androidContext(this@MyApp)
            modules(appMod)
        }
    }
}