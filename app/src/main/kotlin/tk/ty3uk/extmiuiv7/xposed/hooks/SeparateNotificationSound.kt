package tk.ty3uk.extmiuiv7.xposed.hooks

import de.robv.android.xposed.XposedHelpers

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

import de.robv.android.xposed.XposedBridge

import android.os.Bundle
import android.view.View
import android.os.Build

import java.util.Arrays

class SeparateNotificationSound {
    @Throws(Throwable::class)
    fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (lpparam.packageName == SETTINGS_PACKAGE) {
            try {
                XposedHelpers.findAndHookMethod(SETTINGS_RINGER_VOLUME_FRAGMENT_CLASS, lpparam.classLoader, SETTINGS_RINGER_VOLUME_FRAGMENT_METHOD, View::class.java, Bundle::class.java, object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                        val view = param!!.args[0] as View
                        val notificationSection = view.resources.getIdentifier(SETTINGS_PACKAGE + ":id/notification_section", null, null)

                        if (notificationSection > 0) {
                            XposedBridge.log("notification_section visibility: " + view.findViewById(notificationSection).visibility)
                            view.findViewById(notificationSection).visibility = View.VISIBLE
                        }
                    }
                })
            } catch (e: NoSuchMethodError) {
                XposedBridge.log("No such method: " + SETTINGS_RINGER_VOLUME_FRAGMENT_METHOD)
            }
        }

        if (lpparam.packageName == "android") {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                try {
                    XposedHelpers.findAndHookMethod(AUDIO_SERVICE_CLASS_MM, lpparam.classLoader, AUDIO_SERVICE_METHOD, Boolean::class.java, String::class.java, object : XC_MethodHook() {
                        @Throws(Throwable::class)
                        override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                            val mStreamVolumeAlias = XposedHelpers.getObjectField(param!!.thisObject, AUDIO_SERVICE_STREAM_VOLUME_ALIAS) as IntArray

                            mStreamVolumeAlias[STREAM_NOTIFICATION] = STREAM_NOTIFICATION

                            XposedBridge.log("mStreamVolumeAlias levels after: " + Arrays.toString(mStreamVolumeAlias))
                        }
                    })
                } catch (e: NoSuchMethodError) {
                    XposedBridge.log("No such method: " + AUDIO_SERVICE_METHOD)
                }
            } else {
                try {
                    XposedHelpers.findAndHookMethod(AUDIO_SERVICE_CLASS, lpparam.classLoader, AUDIO_SERVICE_METHOD, Boolean::class.java, object : XC_MethodHook() {
                        @Throws(Throwable::class)
                        override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                            val mStreamVolumeAlias = XposedHelpers.getObjectField(param!!.thisObject, AUDIO_SERVICE_STREAM_VOLUME_ALIAS) as IntArray
                            mStreamVolumeAlias[STREAM_NOTIFICATION] = STREAM_NOTIFICATION
                        }
                    })
                } catch (e: NoSuchMethodError) {
                    XposedBridge.log("No such method: " + AUDIO_SERVICE_METHOD)
                }
            }
        }
    }

    companion object {
        internal val STREAM_NOTIFICATION = 5
        internal val SETTINGS_PACKAGE = "com.android.settings"
        internal val SETTINGS_RINGER_VOLUME_FRAGMENT_CLASS = "$SETTINGS_PACKAGE.sound.RingerVolumeFragment"
        internal val SETTINGS_RINGER_VOLUME_FRAGMENT_METHOD = "onViewCreated"
        internal val AUDIO_SERVICE_CLASS = "android.media.AudioService"
        internal val AUDIO_SERVICE_CLASS_MM = "com.android.server.audio.AudioService"
        internal val AUDIO_SERVICE_METHOD = "updateStreamVolumeAlias"
        internal val AUDIO_SERVICE_STREAM_VOLUME_ALIAS = "mStreamVolumeAlias"
    }
}