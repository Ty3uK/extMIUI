package tk.ty3uk.extmiuiv7.xposed.hooks

import android.Manifest
import android.app.AndroidAppHelper
import android.content.Context
import android.content.res.XModuleResources
import android.view.WindowManager
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tk.ty3uk.extmiuiv7.R
import tk.ty3uk.extmiuiv7.xposed.Main

/**
 * Created by ty3uk on 12.08.16.
 */
class AnimationScale {
    companion object {
        @Throws(Throwable::class)
        fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
            if (!resparam.packageName.equals("com.android.settings"))
                return

            if (!Main.MODULE_PATH.equals("")) {
                val modRes = XModuleResources.createInstance(Main.MODULE_PATH, resparam.res)
                val values = modRes.getStringArray(R.array.transition_animation_scale_values)

                val animationScaleWindow = Main.preferences?.getString("animation_scale_window", "")
                val animationScaleTransition = Main.preferences?.getString("animation_scale_transition", "")
                val animationScaleDuration = Main.preferences?.getString("animation_scale_duration", "")

                replaceArray(resparam, "window_animation_scale", values, animationScaleWindow!!)
                replaceArray(resparam, "transition_animation_scale", values, animationScaleTransition!!)
                replaceArray(resparam, "animator_duration_scale", values, animationScaleDuration!!)

            }
        }

        private fun replaceArray(resparam: XC_InitPackageResources.InitPackageResourcesParam, name: String, values: Array<String>, item: String) {
            if (!item.equals("")) {
                val newValues = values.toMutableList()
                val asFloat = item.toFloat()
                if (asFloat < 1f && asFloat > 0f) {
                    newValues.add(newValues[1])
                    newValues[1] = item
                } else if (asFloat > 1f) {
                    newValues.add(item)
                }

                resparam.res.setReplacement("com.android.settings", "array", "${name}_entries", newValues.toTypedArray())
                resparam.res.setReplacement("com.android.settings", "array", "${name}_values", newValues.toTypedArray())
            }
        }
    }
}