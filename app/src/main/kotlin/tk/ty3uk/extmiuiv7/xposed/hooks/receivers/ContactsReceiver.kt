package tk.ty3uk.extmiuiv7.xposed.hooks.receivers

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Process
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object ContactsReceiver {
    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!lpparam.packageName.equals("com.android.contacts"))
            return

        XposedHelpers.findAndHookMethod("com.android.contacts.ContactsActivity", lpparam.classLoader, "onCreate", Bundle::class.java, object : XC_MethodHook() {
            private var receiver: BroadcastReceiver? = null

            override fun afterHookedMethod(param: MethodHookParam?) {
                receiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        if (intent != null && intent.hasExtra("restart_contacts")) {
                            try {
                                System.exit(0)
                            } catch (e: Throwable) {
                                Process.sendSignal(Process.myPid(), 9)
                            }
                        }
                        context?.unregisterReceiver(receiver)
                    }
                }

                (param!!.thisObject as Activity).registerReceiver(receiver, IntentFilter("tk.ty3uk.extmiuiv7.action.RESTART"))
            }
        })
    }
}