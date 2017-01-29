package tk.ty3uk.extmiuiv7.xposed.hooks.receivers

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.XResources
import android.os.Process
import android.telecom.Call
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tk.ty3uk.extmiuiv7.xposed.Main

/**
 * Created by ty3uk on 25.08.16.
 */
object SystemUIReceiver {
    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!lpparam.packageName.equals("com.android.systemui"))
            return

        XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.phone.PhoneStatusBar", lpparam.classLoader, "makeStatusBarView", object: XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                val mContext = XposedHelpers.getObjectField(param!!.thisObject, "mContext") as Context
                Main.systemUIContext = mContext

                val receiver = object: BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        if (intent != null) {
                            if (intent.hasExtra("statusbar_search")) {
                                val resources = mContext.resources as XResources
                                resources.setReplacement(
                                        "com.android.systemui",
                                        "bool",
                                        "config_show_statusbar_search",
                                        intent.getBooleanExtra("statusbar_search", true)
                                )
                            }

                            if (intent.hasExtra("statusbar_blur")) {
                                val resources = mContext.resources as XResources
                                resources.setReplacement(
                                        "com.android.systemui",
                                        "bool",
                                        "config_show_statusbar_blur_bg",
                                        intent.getBooleanExtra("statusbar_blur", true)
                                )
                            }

                            if (intent.getBooleanExtra("restart_systemui", false)) {
                                try {
                                    System.exit(0)
                                } catch (e: Throwable) {
                                    Process.sendSignal(Process.myPid(), 9)
                                }
                            }

                            if (intent.getBooleanExtra("reload_theme", false)) {
                                val MiuiConfiguration = XposedHelpers.findClass("android.content.res.MiuiConfiguration", lpparam.classLoader)
                                XposedHelpers.callStaticMethod(MiuiConfiguration, "sendThemeConfigurationChangeMsg", 8192)
                            }

                            Main.preferences!!.reload()
                        }
                    }
                }

                mContext.registerReceiver(receiver, IntentFilter("tk.ty3uk.extmiuiv7.action.RESTART"))
            }
        })
    }
}