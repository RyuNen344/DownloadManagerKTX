package io.github.ryunen344.donwload.manager.ktx

import android.app.DownloadManager
import android.app.DownloadManager.Request
import android.content.Context
import android.content.IntentFilter
import android.net.Uri
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class SuspendDownloadManager private constructor(private val context: Context) {

    private val delegate: DownloadManager by lazy {
        ContextCompat.getSystemService(context, DownloadManager::class.java) as DownloadManager
    }

    suspend fun download(request: Request): Uri = suspendCancellableCoroutine { cont ->
        val id = delegate.enqueue(request)
        val receiver = SuspendDownloadReceiver(DownloadId(id)) {
            val mimeType = delegate.getMimeTypeForDownloadedFile(id)
            val query = DownloadManager.Query().apply { setFilterById(id) }
            val cursor = delegate.query(query)

            if (!cursor.moveToFirst()) {
                return@SuspendDownloadReceiver
            }

            val status =
                cursor.getColumnIndex(DownloadManager.COLUMN_STATUS).takeUnless { it == OUT_OF_BOUND_COLUMN_INDEX }
                    ?.let(cursor::getInt) ?: DownloadManager.ERROR_UNKNOWN

            when (status) {
                DownloadManager.STATUS_PENDING,
                DownloadManager.STATUS_RUNNING,
                DownloadManager.STATUS_PAUSED,
                -> {
                    // noop
                }

                DownloadManager.STATUS_SUCCESSFUL -> {
                    val path = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                        .takeUnless { it == OUT_OF_BOUND_COLUMN_INDEX }?.let(cursor::getString)
                    cont.resume(Uri.parse(path))
                }

                DownloadManager.STATUS_FAILED,
                DownloadManager.ERROR_UNKNOWN -> {
                    cont.cancel()
                }
            }
        }

        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            ContextCompat.RECEIVER_EXPORTED
        )

        cont.invokeOnCancellation { context.unregisterReceiver(receiver) }
    }

    companion object {
        private const val OUT_OF_BOUND_COLUMN_INDEX = -1

        fun from(context: Context): SuspendDownloadManager {
            return SuspendDownloadManager(context.applicationContext)
        }
    }
}
