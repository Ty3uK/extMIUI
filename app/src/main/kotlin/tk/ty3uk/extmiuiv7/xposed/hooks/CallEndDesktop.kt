package tk.ty3uk.extmiuiv7.xposed.hooks

import android.content.Context
import android.content.Intent
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object CallEndDesktop {
    @Throws(Throwable::class)
    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!lpparam.packageName.equals(HOOK_PACKAGE_NAME))
            return

        try {
            val Call = XposedHelpers.findClass(CALL_CLASSNAME, lpparam.classLoader)

            XposedBridge.log("Call class founded")

            try {
                XposedHelpers.findAndHookMethod(INCALLPRESENTER_CLASSNAME, lpparam.classLoader, "onDisconnect", Call, object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam?) {
                        val call = param!!.args[0]
                        if (call != null) {
                            val capabilities = XposedHelpers.callMethod(call, "getCapabilities") as Int
                            val isIncoming = XposedHelpers.callMethod(call, "getIsIncoming") as Boolean

                            XposedBridge.log("isIncoming = $isIncoming | capabilities = $capabilities")

                            if (!isIncoming && capabilities != 0) {
                                val mContext = XposedHelpers.getObjectField(param.thisObject, "mContext") as Context?
                                val intent = Intent(Intent.ACTION_MAIN)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.addCategory(Intent.CATEGORY_HOME)
                                mContext?.startActivity(intent)
                            }
                        }
                    }
                })
            } catch (e: XposedHelpers.ClassNotFoundError) {
                XposedBridge.log("Class not found: $INCALLPRESENTER_CLASSNAME")
            } catch (e: NoSuchMethodError) {
                XposedBridge.log("No such method: $INCALLPRESENTER_CLASSNAME.onDisconnect")
            }
        } catch (e: XposedHelpers.ClassNotFoundError) {
            XposedBridge.log("Class not found: $CALL_CLASSNAME")
        }
    }

    private val HOOK_PACKAGE_NAME = "com.android.incallui"
    private val CALL_CLASSNAME = "$HOOK_PACKAGE_NAME.Call"
    private val INCALLPRESENTER_CLASSNAME = "$HOOK_PACKAGE_NAME.InCallPresenter"
}