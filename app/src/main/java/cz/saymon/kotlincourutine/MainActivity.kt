package cz.saymon.kotlincourutine

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.*
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.CoroutineContext

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity(), CoroutineScope {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, exception ->
        val currentThreadName = Thread.currentThread().name
        println("Caught exception '$exception'")
        println("\tin context '$coroutineContext'")
        println("\ton thread '$currentThreadName'")
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + SupervisorJob() + coroutineExceptionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            Snackbar.make(it, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()

            loadText()
        }
    }

    private fun loadText() = launch {
        text.text = "Waiting for the response"
        val response = downloadData()
        text.text = response
    }

    private suspend fun downloadData() = withContext(Dispatchers.IO) {
        val url = URL("http://www.google.com")

        val urlConnection = url.openConnection() as HttpURLConnection
        val inputStreamReader = InputStreamReader(urlConnection.inputStream)

        val data = inputStreamReader.readText()

        urlConnection.disconnect()

        throw Error("test exception")

        "Hello! :: ${System.currentTimeMillis()}\n\n$data"
    }

    override fun onDestroy() {
        super.onDestroy()

        coroutineContext.cancelChildren()
    }

}
