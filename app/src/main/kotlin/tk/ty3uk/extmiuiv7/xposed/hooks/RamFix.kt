package tk.ty3uk.extmiuiv7.xposed.hooks

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import miui.os.MiuiProcessUtil

object RamFix {
    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!lpparam.packageName.equals("com.android.systemui"))
            return

        try {
            val HardwareInfo = XposedHelpers.findClass("miui.util.HardwareInfo", lpparam.classLoader)

            XposedHelpers.findAndHookMethod(HardwareInfo, "getFreeMemory", object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    val cacheMemory = XposedHelpers.callStaticMethod(HardwareInfo, "getAndroidCacheMemory") as Long
                    val totalPhysicalMemory = XposedHelpers.callStaticMethod(HardwareInfo, "getTotalPhysicalMemory") as Long
                    val totalMemory = XposedHelpers.callStaticMethod(HardwareInfo, "getTotalMemory") as Long

                    param!!.result = ((((MiuiProcessUtil.getFreeMemory() / 1024L) + cacheMemory) * ((2 * totalPhysicalMemory) - totalMemory)) / totalPhysicalMemory) * 1024L

                    return
                }
            })
        } catch (e: XposedHelpers.ClassNotFoundError) {
            XposedBridge.log("ClassNotFoundError: miui.util.HardwareInfo")
        }
    }
}