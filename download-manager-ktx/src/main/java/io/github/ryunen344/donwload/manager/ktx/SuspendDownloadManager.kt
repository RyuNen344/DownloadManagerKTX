package io.github.ryunen344.donwload.manager.ktx

import android.app.DownloadManager
import android.app.DownloadManager.Request
import android.content.Context
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Suspendable [DownloadManager] wrapper
 *
 * <p>
 * <pre>{@code
 *     coroutineScope {
 *         val request = DownloadManager.Request(Uri.parse("uri"))
 *             .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
 *             .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "file.png")
 *         val uri = manager.download(request)
 *     }
 * }</pre>
 * </p>
 */
class SuspendDownloadManager private constructor(private val context: Context) {

    private val delegate: DownloadManager by lazy {
        ContextCompat.getSystemService(context, DownloadManager::class.java) as DownloadManager
    }

    /**
     * download resource
     * continuation will cancel when IllegalArgumentException occurred by [Cursor.getColumnIndexOrThrow]
     *
     * @param request [DownloadManager.Request]
     * @return content [Uri] (e.g. content://downloads/all_downloads/55)
     */
    suspend fun download(request: Request): Uri = suspendCancellableCoroutine { cont ->
        val id = delegate.enqueue(request)
        val receiver = SuspendDownloadReceiver(DownloadId(id)) {
            try {
                cont.resume(delegate.getUriForDownloadedFile(id) ?: Uri.EMPTY)
            } catch (e: IllegalArgumentException) {
                cont.cancel(e)
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
        fun from(context: Context): SuspendDownloadManager {
            return SuspendDownloadManager(context.applicationContext)
        }
    }
}
