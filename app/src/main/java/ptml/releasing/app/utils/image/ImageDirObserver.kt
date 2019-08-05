package ptml.releasing.app.utils.image

import android.os.FileObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

import java.io.File

class ImageDirObserver(root: String, private val listener: ImageDirListener? = null) : FileObserver(
    root,
    mask
), LifecycleObserver {
    /**
     * should be end with File.separator
     */
    private val rootPath: String

    init {
        var root = root

        if (!root.endsWith(File.separator)) {
            root += File.separator
        }
        rootPath = root
    }

    override fun onEvent(event: Int, path: String?) {

        when (event) {
            CREATE -> {
                listener?.onCreate(path)
            }
            DELETE -> {
                listener?.onDelete(path)
            }
            DELETE_SELF -> {
            }
            else -> {
                // just ignore
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun start(){
        startWatching()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stop(){
        stopWatching()
    }


    companion object {
        internal val TAG = "FILEOBSERVER"
        private val mask = CREATE or
                DELETE or
                DELETE_SELF
    }

    interface ImageDirListener{
        fun onCreate(path: String?)
        fun onDelete(path: String?)
    }
}