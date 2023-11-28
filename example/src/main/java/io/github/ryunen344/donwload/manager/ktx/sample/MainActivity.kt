package io.github.ryunen344.donwload.manager.ktx.sample

import android.app.DownloadManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.ryunen344.donwload.manager.ktx.SuspendDownloadManager
import io.github.ryunen344.donwload.manager.ktx.sample.ui.theme.DonwloadManagerKTXTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val manager: SuspendDownloadManager by lazy {
        SuspendDownloadManager.from(this)
    }

    private val request: DownloadManager.Request by lazy {
        DownloadManager.Request(Uri.parse("https://developer.android.com/static/images/brand/Android_Robot.png"))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Android_Robot.png")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scope = rememberCoroutineScope()
            DonwloadManagerKTXTheme {
                // A surface container using the 'background' color from the theme
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {

                    Greeting("Android")

                    Button(
                        onClick = {
                            scope.launch(Dispatchers.Default) {
                                val ids = awaitAll(
                                    async { manager.download(request) },
                                    async { manager.download(request) },
                                    async { manager.download(request) },
                                    async { manager.download(request) },
                                )
                                ids.forEach {
                                    Log.d(TAG, "downloaded uri $it")
                                }
                                val uri = manager.download(request)
                                Log.d(TAG, "downloaded uri $uri")
                            }
                        }
                    ) {
                        Text(text = "download file")
                    }
                }
            }
        }
    }

    private companion object {
        const val TAG = "MainActivity"
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DonwloadManagerKTXTheme {
        Greeting("Android")
    }
}
