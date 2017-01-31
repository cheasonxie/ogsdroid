package com.ogs

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection

class NotificationService() : Service() {
	val ogs = OGS("82ff83f2631a55273c31", "cd42d95fd978348d57dc909a9aecd68d36b17bd2")
	var notificationConnection: NotificationConnection? = null

	init {
		println("NotificationService init")
	}

	override fun onBind(Intent intent) = null

	override fun onCreate() {
		println("NotificationService.onCreate()")
		Toast.makeText(this, "NotificationService onCreate", Toast.LENGTH_SHORT).show()

		val t = thread(start=true) {
			println("NJ NotificationService getting me")
			ogs.me()

			println("NJ NotificationService getting ui config")
			val config = ogs.uiConfig()
			val auth = config.getString("notification_auth")

			println("NJ NotificationService opening socket")
			ogs.openSocket()
			println("NJ NotificationService opening notification connection")
			notificationConnection = ogs.openNotificationConnection(auth, { obj ->
				println("NJ in service notification = $obj")
			})
			println("NJ NotificationService created and waiting!")
		}

		return START_NOT_STICKY
	}

	override fun onStartCommand(Intent intent, int flags, int startId) {
		println("NotificationService.onStartCommand(intent=$intent, flags=$flags, startId=$startId)")
		Toast.makeText(this, "NotificationService onStartCommand", Toast.LENGTH_SHORT).show()
	}

	override fun onDestroy() {
		notificationConnection?.disconnect()
		ogs.closeSocket()
		println("NotificationService.onDestroy()")
		Toast.makeText(this, "NotificationService onDestroy", Toast.LENGTH_SHORT).show()
	}
}
