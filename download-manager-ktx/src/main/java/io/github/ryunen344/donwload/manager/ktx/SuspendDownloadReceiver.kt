package io.github.ryunen344.donwload.manager.ktx

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

internal class SuspendDownloadReceiver(
    private val id: DownloadId,
    private val onDownload: () -> Unit,
) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        println("onReceive ${intent.action}")
        if (intent.action != DownloadManager.ACTION_DOWNLOAD_COMPLETE) return
        val receivedId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, DEFAULT_DOWNLOAD_ID)
        if (receivedId != DEFAULT_DOWNLOAD_ID && receivedId == id.value) {
            onDownload()
        }
    }

    private companion object {
        const val DEFAULT_DOWNLOAD_ID = 0L
    }
}
