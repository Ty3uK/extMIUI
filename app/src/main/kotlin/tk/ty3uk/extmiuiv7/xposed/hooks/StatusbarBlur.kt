package tk.ty3uk.extmiuiv7.xposed.hooks

import android.content.res.XModuleResources
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import tk.ty3uk.extmiuiv7.R
import tk.ty3uk.extmiuiv7.xposed.Main

/**
 * Created by Ty3uK on 23.04.16.
 */
class StatusbarBlur {
    @Throws(Throwable::class)
    fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        if (!resparam.packageName.equals(HOOK_PACKAGE_NAME))
            return

        if (Main.preferences!!.contains("statusbar_blur"))
            resparam.res.setReplacement(
                "com.android.systemui",
                "bool",
                "config_show_statusbar_blur_bg",
                Main.preferences!!.getBoolean("statusbar_blur", false)
            )
    }

    companion object {
        private val PACKAGE_NAME = "tk.ty3uk.extmiuiv7"
        private val HOOK_PACKAGE_NAME = "com.android.systemui"
    }
}