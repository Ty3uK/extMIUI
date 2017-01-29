package tk.ty3uk.extmiuiv7.xposed.hooks

import android.app.AndroidAppHelper
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import tk.ty3uk.extmiuiv7.xposed.Main

/**
 * Created by Ty3uK on 02.05.16.
 */
class StatusbarSearch {
    @Throws(Throwable::class)
    fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        if (!resparam.packageName.equals(HOOK_PACKAGE_NAME))
            return

        if (Main.preferences!!.contains("statusbar_search")) {
            resparam.res.setReplacement(
                    "com.android.systemui",
                    "bool",
                    "config_show_statusbar_search",
                    Main.preferences?.getBoolean("statusbar_search", true)
            )
        }
    }

    companion object {
        private val HOOK_PACKAGE_NAME = "com.android.systemui"
    }
}