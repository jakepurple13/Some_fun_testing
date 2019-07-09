package com.example.myapplication

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.*
import java.net.URL
import java.text.DecimalFormat
import kotlin.math.roundToLong


class CustomDownloader(
    var listener: DownloadListener = object : DownloadListener {}
) {

    fun downloadUrlAsync(url: String, storageLocation: String) = GlobalScope.async {
        var count = 0
        try {

            val startTime = System.nanoTime()
            val startTimeRate = System.currentTimeMillis()

            val urls = URL(url)
            val connection = urls.openConnection()
            connection.connect()

            // this will be useful so that you can show a typical 0-100%
            // progress bar
            val lengthOfFile = connection.contentLength
            listener.getLength(lengthOfFile)

            // download the file
            val input = DataInputStream(urls.openStream())

            val file = createFile(storageLocation)

            // Output stream
            val output = FileOutputStream(file)

            val data = ByteArray(lengthOfFile)

            var total: Long = 0

            while ((count) != -1) {
                count = input.read(data)
                total += count.toLong()
                // publishing the progress....
                // After this onProgressUpdate will be called
                //(total * 100.0) / lengthOfFile
                val endTime = System.currentTimeMillis()
                var rate = total / 1024 / ((endTime - startTimeRate) / 1000) * 8.0
                rate = (rate * 100.0).roundToLong() / 100.0

                val elapsedTime = System.nanoTime() - startTime
                val allTimeForDownloading = elapsedTime * lengthOfFile.toLong() / total
                val remainingTime = allTimeForDownloading - elapsedTime

                listener.onProgressUpdate(total, lengthOfFile, rate, remainingTime)

                // writing data to file
                output.write(data, 0, count)
            }

            // flushing output
            output.flush()

            // closing streams
            output.close()
            input.close()

        } catch (e: Exception) {
            Loged.e(e.message!!)
            e.printStackTrace()
        }
        listener.onFinished()
    }

    private fun createFile(filePath: String): File {
        val file = File(filePath)
        if (!file.exists()) {
            val parent = file.parentFile!!
            if (!parent.exists()) {
                parent.mkdirs()
            }
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }

    companion object {

        fun getDownloadSpeedString(downloadedBytesPerSecond: Double): String {
            if (downloadedBytesPerSecond < 0) {
                return ""
            }
            val kb = downloadedBytesPerSecond / 1000.toDouble()
            val mb = kb / 1000.toDouble()
            val gb = mb / 1000
            val tb = gb / 1000
            val decimalFormat = DecimalFormat(".##")
            return when {
                tb >= 1 -> "${decimalFormat.format(tb)} tb/s"
                gb >= 1 -> "${decimalFormat.format(gb)} gb/s"
                mb >= 1 -> "${decimalFormat.format(mb)} mb/s"
                kb >= 1 -> "${decimalFormat.format(kb)} kb/s"
                else -> "$downloadedBytesPerSecond b/s"
            }
        }

        fun getETAString(etaInMilliSeconds: Long, needLeft: Boolean = true): String {
            if (etaInMilliSeconds < 0) {
                return ""
            }
            var seconds = (etaInMilliSeconds / 1000).toInt()
            val hours = (seconds / 3600).toLong()
            seconds -= (hours * 3600).toInt()
            val minutes = (seconds / 60).toLong()
            seconds -= (minutes * 60).toInt()
            return when {
                hours > 0 -> String.format("%02d:%02d:%02d hours", hours, minutes, seconds)
                minutes > 0 -> String.format("%02d:%02d mins", minutes, seconds)
                else -> "$seconds secs"
            } + (if (needLeft) " left" else "")
        }
    }

}

interface DownloadListener {
    fun onFinished() {}
    fun getLength(length: Int) {}
    fun onProgressUpdate(current: Long, length: Int, speed: Double, timeLeft: Long) {}
}