package mchehab.com.customlistviewimage

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v4.content.LocalBroadcastManager
import java.lang.ref.WeakReference

/**
 * Created by muhammadchehab on 11/5/17.
 */

class AsyncTaskWait(private var context: WeakReference<Context>) : AsyncTask<Void, Void, Void>() {

    override fun doInBackground(vararg params: Void): Void? {
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        return null
    }

    override fun onPostExecute(nothing: Void?) {
        val intent = Intent("done")
        LocalBroadcastManager.getInstance(context.get()?.applicationContext).sendBroadcast(intent)
    }
}