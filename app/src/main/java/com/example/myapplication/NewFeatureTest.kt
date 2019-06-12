package com.example.myapplication

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Icon
import android.hardware.biometrics.BiometricPrompt
import android.os.Bundle
import android.os.CancellationSignal
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_new_feature_test.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.util.Base64.URL_SAFE
import androidx.fragment.app.FragmentActivity
import crestron.com.deckofcards.Deck
import java.security.Signature


class NewFeatureTest : AppCompatActivity() {

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

        }
    }

    fun sendNoti(context: Context) {
        val channel = NotificationChannel("asdf1", "asdf1", NotificationManager.IMPORTANCE_HIGH)
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
}
