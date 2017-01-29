package tk.ty3uk.extmiuiv7.xposed.hooks.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Process
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Created by ty3uk on 07.09.16.
 */
object InCallUiReceiver {
    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!lpparam.packageName.equals("com.android.incallui"))
            return

        XposedHelpers.findAndHookMethod("com.android.incallui.InCallApp", lpparam.classLoader, "onCreate", object: XC_MethodHook() {
            private var receiver: BroadcastReceiver? = null

            override fun afterHookedMethod(param: MethodHookParam?) {
                val mContext = param!!.thisObject as Context

                receiver = object: BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        if (intent != null && intent.hasExtra("restart_incallui")) {
                            try {
                                System.exit(0)
                            } catch (e: Throwable) {
                                Process.sendSignal(Process.myPid(), 9)
                            }

                            context?.unregisterReceiver(receiver)
                        }
                    }
                }

                mContext.registerReceiver(receiver, IntentFilter("tk.ty3uk.extmiuiv7.action.RESTART"))
            }
        })
    }
}