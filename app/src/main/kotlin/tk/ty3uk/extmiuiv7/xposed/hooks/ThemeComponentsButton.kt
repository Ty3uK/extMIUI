package tk.ty3uk.extmiuiv7.xposed.hooks

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Created by Ty3uK on 28.04.16.
 */
class ThemeComponentsButton {
    @Throws(Throwable::class)
    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!lpparam.packageName.equals(HOOK_PACKAGE_NAME))
            return

        try {
            XposedHelpers.findAndHookMethod(THEME_TAB_ACTIVITY_CLASSNAME, lpparam.classLoader, "onCreate", Bundle::class.java, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    val actionBarView = XposedHelpers.callMethod(XposedHelpers.callMethod(param?.thisObject, "getActionBar"), "getCustomView") as LinearLayout
                    if (actionBarView.childCount > 0) {
                        val mContext = param?.thisObject as Context?
                        val componentsButton = ImageView(mContext)
                        val customizeDrawableId = mContext!!.resources!!.getIdentifier("resource_multiple_button_customize", "drawable", HOOK_PACKAGE_NAME)
                        if (customizeDrawableId > 0) {
                            componentsButton.setImageResource(customizeDrawableId)
                            val layoutParams = LinearLayout.LayoutParams(80, 80)
                            layoutParams.marginEnd = 40
                            componentsButton.layoutParams = layoutParams
                            componentsButton.setOnClickListener { view ->
                                run {
                                    try {
                                        val ComponentActivity = XposedHelpers.findClass(COMPONENT_ACTIVITY_CLASSNAME, lpparam.classLoader)
                                        mContext.startActivity(Intent(mContext, ComponentActivity))
                                    } catch (e: XposedHelpers.ClassNotFoundError) {
                                        XposedBridge.log("No such class: $COMPONENT_ACTIVITY_CLASSNAME")
                                    }
                                }
                            }
                            actionBarView.addView(componentsButton)
                        }
                    }
                }
            })
        } catch (e: ClassNotFoundException) {
            XposedBridge.log("No such class: $THEME_TAB_ACTIVITY_CLASSNAME")
        } catch (e: NoSuchMethodError) {
            XposedBridge.log("No such method: $THEME_TAB_ACTIVITY_CLASSNAME.onCreate")
        }
    }

    companion object {
        private val PACKAGE_NAME = "tk.ty3uk.extmiuiv7"
        private val HOOK_PACKAGE_NAME = "com.android.thememanager"
        private val THEME_TAB_ACTIVITY_CLASSNAME = "$HOOK_PACKAGE_NAME.activity.ThemeTabActivity"
        private val COMPONENT_ACTIVITY_CLASSNAME = "$HOOK_PACKAGE_NAME.activity.ComponentActivity"
    }
}