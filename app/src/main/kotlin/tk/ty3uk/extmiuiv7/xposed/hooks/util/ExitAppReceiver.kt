package tk.ty3uk.extmiuiv7.xposed.hooks.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Process

/**
 * Created by ty3uk on 05.09.16.
 */
class ExitAppReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        try {
            System.exit(0)
        } catch (e: Throwable) {
            Process.sendSignal(Process.myPid(), 9)
        }
    }
}