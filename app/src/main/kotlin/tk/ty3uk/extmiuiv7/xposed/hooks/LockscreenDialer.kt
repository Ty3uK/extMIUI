package tk.ty3uk.extmiuiv7.xposed.hooks

import android.animation.ObjectAnimator
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.XModuleResources
import android.util.Property
import android.view.View
import android.view.animation.DecelerateInterpolator

import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tk.ty3uk.extmiuiv7.R
import tk.ty3uk.extmiuiv7.xposed.Main

/**
 * Created by maxka on 17.04.2016.
 */
class LockscreenDialer {

    @Throws(Throwable::class)
    fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        MODULE_PATH = startupParam.modulePath
    }

    @Throws(Throwable::class)
    fun initPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        if (resparam.packageName != "com.android.keyguard")
            return

        val modRes = XModuleResources.createInstance(MODULE_PATH, resparam.res)
        resparam.res.setReplacement("com.android.keyguard", "drawable", "remote_center_img", modRes.fwd(R.mipmap.remote_center_img))
        resparam.res.setReplacement("com.android.keyguard", "drawable", "remote_center_img_dark", modRes.fwd(R.mipmap.remote_center_img_dark))
    }

    @Throws(Throwable::class)
    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.android.keyguard") {
            try {
                val MiuiDefaultLockScreen = XposedHelpers.findClass(MIUI_DEFAULT_LOCKSCREEN_CLASSNAME, lpparam.classLoader)
                val MiuiKeyguardUpdateMonitor = XposedHelpers.findClass(MIUI_KEYGUARD_UPDATE_MONITOR_CLASSNAME, lpparam.classLoader)
                val MiuiKeyguardScreenCallback = XposedHelpers.findClass(MIUI_KEYGUARD_SCREEN_CALLBACK_CLASSNAME, lpparam.classLoader)
                val LockPatternUtils = XposedHelpers.findClass(LOCK_PATTERN_UTILS_CLASSNAME, lpparam.classLoader)

                try {
                    XposedHelpers.findAndHookConstructor(MiuiDefaultLockScreen, Context::class.java, Configuration::class.java, LockPatternUtils, MiuiKeyguardUpdateMonitor, MiuiKeyguardScreenCallback, object : XC_MethodHook() {
                        @Throws(Throwable::class)
                        override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                            try {
                                XposedHelpers.setBooleanField(param!!.thisObject, "mRemoteCenterAvailable", true)
                                val mRemoteCenterIntent = XposedHelpers.getObjectField(param.thisObject, "mRemoteCenterIntent") as Intent
                                mRemoteCenterIntent.component = ComponentName(Main.preferences!!.getString("lockscreen_dialer_packageName", DIALER_PACKAGE_NAME),
                                        Main.preferences!!.getString("lockscreen_dialer_packageActivity", DIALER_PACKAGE_ACTIVITY))
                            } catch (e: NoSuchFieldError) {
                                XposedBridge.log("No such field: mRemoteCenterAvailable")
                                return
                            }
                        }
                    })
                } catch (e: NoSuchMethodError) {
                    XposedBridge.log("No such constructor: MiuiDefaultLockScreen")
                    return
                }

                try {
                    XposedHelpers.findAndHookMethod(MiuiDefaultLockScreen, "triggerStartRemoteCenterAction", object : XC_MethodReplacement() {
                        @Throws(Throwable::class)
                        override fun replaceHookedMethod(param: XC_MethodHook.MethodHookParam): Any? {
                            val mKeyguardScreenCallback = XposedHelpers.getObjectField(param.thisObject, "mKeyguardScreenCallback")
                            XposedHelpers.callMethod(mKeyguardScreenCallback, "goToUnlockScreen")

                            XposedHelpers.setBooleanField(param.thisObject, "mStartingRemoteCenter", true)

                            val localMainLockView = XposedHelpers.getObjectField(param.thisObject, "mLockView")
                            val localProperty = View.TRANSLATION_X

                            val arrayOfFloat = FloatArray(2)
                            arrayOfFloat[0] = XposedHelpers.callMethod(localMainLockView, "getTranslationX") as Float
                            arrayOfFloat[1] = (XposedHelpers.callMethod(param.thisObject, "getWidth") as Int).toFloat()

                            val localObjectAnimator = ObjectAnimator.ofFloat(localMainLockView, localProperty as Property<Any, Float>, *arrayOfFloat)
                            localObjectAnimator.setDuration(100L)
                            localObjectAnimator.setInterpolator(DecelerateInterpolator())
                            localObjectAnimator.start()

                            val mContext = XposedHelpers.getObjectField(param.thisObject, "mContext") as Context
                            val mRemoteCenterIntent = XposedHelpers.getObjectField(param.thisObject, "mRemoteCenterIntent") as Intent

                            mContext.startActivity(mRemoteCenterIntent)
                            return null
                        }
                    })
                } catch (e: NoSuchMethodError) {
                    XposedBridge.log("No such method: triggerStartRemoteCenterAction")
                    return
                }

            } catch (e: XposedHelpers.ClassNotFoundError) {
                return
            }

        }
    }

    companion object {
        private var MODULE_PATH: String? = null
        private val PACKAGE_NAME = "tk.ty3uk.extmiuiv7"
        private val MIUI_DEFAULT_LOCKSCREEN_CLASSNAME = "com.android.keyguard.MiuiDefaultLockScreen"
        private val MIUI_KEYGUARD_UPDATE_MONITOR_CLASSNAME = "com.android.keyguard.MiuiKeyguardUpdateMonitor"
        private val MIUI_KEYGUARD_SCREEN_CALLBACK_CLASSNAME = "com.android.keyguard.MiuiKeyguardScreenCallback"
        private val LOCK_PATTERN_UTILS_CLASSNAME = "com.android.internal.widget.LockPatternUtils"
        private val DIALER_PACKAGE_NAME = "com.android.contacts"
        private val DIALER_PACKAGE_ACTIVITY = "com.android.contacts.activities.TwelveKeyDialer"
    }
}