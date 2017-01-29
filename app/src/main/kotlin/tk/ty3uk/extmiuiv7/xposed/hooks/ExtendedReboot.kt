package tk.ty3uk.extmiuiv7.xposed.hooks

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

import android.app.AndroidAppHelper
import android.content.Context

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

import android.os.Build
import android.os.PowerManager
import de.robv.android.xposed.XposedBridge

class ExtendedReboot {
    @Throws(Throwable::class)
    fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (lpparam.packageName == "android") {
            val MiuiGlobalActions: Class<*>
            val `MiuiGlobalActions$1`: Class<*>
            val `MiuiGlobalActions$1$1`: Class<*>

            val isMarshMallow = Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1

            MiuiGlobalActions = XposedHelpers.findClass(if (isMarshMallow) MIUI_GLOBAL_ACTIONS_CLASSNAME_MM else MIUI_GLOBAL_ACTIONS_CLASSNAME, lpparam.classLoader)
            `MiuiGlobalActions$1` = XposedHelpers.findClass(if (isMarshMallow)`MIUI_GLOBAL_ACTIONS$1_CLASSNAME_MM` else `MIUI_GLOBAL_ACTIONS$1_CLASSNAME`, lpparam.classLoader)
            `MiuiGlobalActions$1$1` = XposedHelpers.findClass(if (isMarshMallow) `MIUI_GLOBAL_ACTIONS$1$1_CLASSNAME_MM` else `MIUI_GLOBAL_ACTIONS$1$1_CLASSNAME`, lpparam.classLoader)

            val WindowManagerFuncs = XposedHelpers.findClass(WINDOW_MANAGER_FUNCS_CLASSNAME, lpparam.classLoader)
            val ResourceManager = XposedHelpers.findClass(RESOURCE_MANAGER_CLASSNAME, lpparam.classLoader)
            val ZipResourceLoader = XposedHelpers.findClass(ZIP_RESOURCE_LOADER_CLASSNAME, lpparam.classLoader)
            val ScreenContext = XposedHelpers.findClass(SCREEN_CONTEXT_CLASSNAME, lpparam.classLoader)
            val ScreenElementRoot = XposedHelpers.findClass(SCREEN_ELEMENT_ROOT_CLASSNAME, lpparam.classLoader)

            XposedHelpers.findAndHookConstructor(MiuiGlobalActions, Context::class.java, WindowManagerFuncs, object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                    val contextImpl = XposedHelpers.getObjectField(param!!.thisObject, "mContext") as Context
                    val powermenu = File(POWERMENU_PATH)
                    val inputStream: InputStream
                    val outputStream: FileOutputStream
                    val fileBytes: ByteArray

                    if (!powermenu.exists()) {
                        val context = contextImpl.createPackageContext(PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY)
                        inputStream = context.resources.openRawResource(
                                context.resources.getIdentifier("powermenu_patched", "raw", PACKAGE_NAME))
                        fileBytes = ByteArray(inputStream.available())
                        inputStream.read(fileBytes)
                        outputStream = FileOutputStream(powermenu)
                        outputStream.write(fileBytes)
                        outputStream.close()
                        inputStream.close()
                    }

                    val mContext = XposedHelpers.getObjectField(param.thisObject, "mContext") as Context
                    XposedHelpers.setObjectField(param.thisObject, "mResourceManager", XposedHelpers.newInstance(
                            ResourceManager,
                            XposedHelpers.newInstance(ZipResourceLoader, powermenu.path)))
                    val mResourceManager = XposedHelpers.getObjectField(param.thisObject, "mResourceManager")
                    XposedHelpers.setObjectField(param.thisObject, "mScreenElementRoot", XposedHelpers.newInstance(
                            ScreenElementRoot,
                            XposedHelpers.newInstance(ScreenContext, mContext, mResourceManager)))
                    val mScreenElementRoot = XposedHelpers.getObjectField(param.thisObject, "mScreenElementRoot")
                    XposedHelpers.callMethod(mScreenElementRoot, "setOnExternCommandListener", XposedHelpers.getObjectField(param.thisObject, "mCommandListener"))
                    XposedHelpers.callMethod(mScreenElementRoot, "setKeepResource", true)
                    XposedHelpers.callMethod(mScreenElementRoot, "load")
                    XposedHelpers.callMethod(mScreenElementRoot, "init")
                }
            })

            XposedHelpers.findAndHookMethod(`MiuiGlobalActions$1`, "onCommand", String::class.java, Double::class.javaObjectType, String::class.java, object : XC_MethodReplacement() {
                @Throws(Throwable::class)
                override fun replaceHookedMethod(param: XC_MethodHook.MethodHookParam): Any? {
                    val `this$0` = XposedHelpers.getObjectField(param.thisObject, "this\$0")
                    val paramString1 = param.args[0] as String

                    if ("airplane" == paramString1) {
                        XposedHelpers.callStaticMethod(MiuiGlobalActions, "access\$000", `this$0`, 9)
                        return null
                    }

                    do {
                        if ("silent" == paramString1) {
                            XposedHelpers.callStaticMethod(MiuiGlobalActions, "access\$000", `this$0`, 5)
                            return null
                        }

                        if ("reboot" == paramString1) {
                            val pm = AndroidAppHelper.currentApplication().getSystemService(Context.POWER_SERVICE) as PowerManager
                            pm.reboot(null)
                            return null
                        }

                        if ("recovery" == paramString1) {
                            val pm = AndroidAppHelper.currentApplication().getSystemService(Context.POWER_SERVICE) as PowerManager
                            pm.reboot("recovery")
                            return null
                        }

                        if ("bootloader" == paramString1) {
                            val pm = AndroidAppHelper.currentApplication().getSystemService(Context.POWER_SERVICE) as PowerManager
                            pm.reboot("bootloader")
                            return null
                        }

                        if ("shutdown" == paramString1) {
                            val instanse = XposedHelpers.newInstance(`MiuiGlobalActions$1$1`, param.thisObject, "ShutdownThread")
                            XposedHelpers.callMethod(instanse, "start")
                            return null
                        }
                    } while ("dismiss" != paramString1)

                    val mHandler = XposedHelpers.callStaticMethod(MiuiGlobalActions, "access\$200", `this$0`)
                    XposedHelpers.callMethod(mHandler, "sendEmptyMessage", 0)

                    return null
                }
            })
        }
    }

    companion object {
        private val PACKAGE_NAME = "tk.ty3uk.extmiuiv7"

        private val MIUI_GLOBAL_ACTIONS_CLASSNAME = "com.android.internal.policy.impl.MiuiGlobalActions"
        private val `MIUI_GLOBAL_ACTIONS$1_CLASSNAME` = "com.android.internal.policy.impl.MiuiGlobalActions\$1"
        private val `MIUI_GLOBAL_ACTIONS$1$1_CLASSNAME` = "com.android.internal.policy.impl.MiuiGlobalActions\$1\$1"

        private val MIUI_GLOBAL_ACTIONS_CLASSNAME_MM = "com.android.server.policy.MiuiGlobalActions"
        private val `MIUI_GLOBAL_ACTIONS$1_CLASSNAME_MM` = "com.android.server.policy.MiuiGlobalActions\$1"
        private val `MIUI_GLOBAL_ACTIONS$1$1_CLASSNAME_MM` = "com.android.server.policy.MiuiGlobalActions\$1\$1"

        private val WINDOW_MANAGER_FUNCS_CLASSNAME = "android.view.WindowManagerPolicy\$WindowManagerFuncs"
        private val RESOURCE_MANAGER_CLASSNAME = "miui.maml.ResourceManager"
        private val ZIP_RESOURCE_LOADER_CLASSNAME = "miui.maml.util.ZipResourceLoader"
        private val SCREEN_CONTEXT_CLASSNAME = "miui.maml.ScreenContext"
        private val SCREEN_ELEMENT_ROOT_CLASSNAME = "miui.maml.ScreenElementRoot"

        private val POWERMENU_PATH = "/cache/powermenu_patched"
    }
}