package tk.ty3uk.extmiuiv7.xposed.hooks

import android.content.res.XModuleResources
import android.content.res.XResources
import android.hardware.SensorEvent
import android.os.SystemClock

import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tk.ty3uk.extmiuiv7.R
import tk.ty3uk.extmiuiv7.util.common
import tk.ty3uk.extmiuiv7.xposed.Main
import java.util.*

/**
 * Created by maxka on 18.04.2016.
 */
object BrightnessFix {
    @Throws(Throwable::class)
    fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        val modRes = XModuleResources.createInstance(startupParam.modulePath, null)

        try {
            XResources.setSystemWideReplacement(
                    "android",
                    "array",
                    "config_autoBrightnessLevels",
                    if (Main.preferences!!.contains("brightness_fix_autoBrightnessLevels"))
                        common.StringToIntArray(Main.preferences!!.getString("brightness_fix_autoBrightnessLevels", ""))
                    else
                        modRes.fwd(R.array.config_autoBrightnessLevels)
            )
            XResources.setSystemWideReplacement(
                    "android",
                    "array",
                    "config_autoBrightnessLcdBacklightValues",
                    if (Main.preferences!!.contains("brightness_fix_autoBrightnessLcdBacklightValues"))
                        common.StringToIntArray(Main.preferences!!.getString("brightness_fix_autoBrightnessLcdBacklightValues", ""))
                    else
                        modRes.fwd(R.array.config_autoBrightnessLcdBacklightValues)
            )
        } catch (nfe: NumberFormatException) {
            nfe.printStackTrace()
        }
    }

    @Throws(Throwable::class)
    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "android")
            return

        var AutomaticBrightnessController: Class<*>? = null
        var Anonymous: Class<*>? = null
        var DisplayPowerController: Class<*>? = null

        try {
            DisplayPowerController = XposedHelpers.findClass(DISPLAY_POWER_CONTROLLER_CLASSNAME, lpparam.classLoader)
        } catch (e: XposedHelpers.ClassNotFoundError) {
            XposedBridge.log("Class not found: $ANONYMOUS_CLASSNAME")
        }

        try {
            AutomaticBrightnessController = XposedHelpers.findClass(AUTOMATIC_BRIGHTNESS_CONTROLLER_CLASSNAME, lpparam.classLoader)
        } catch (e: XposedHelpers.ClassNotFoundError) {
            XposedBridge.log("Class not found: $AUTOMATIC_BRIGHTNESS_CONTROLLER_CLASSNAME")
        }
        try {
            Anonymous = XposedHelpers.findClass(ANONYMOUS_CLASSNAME, lpparam.classLoader)
        } catch (e: XposedHelpers.ClassNotFoundError) {
            XposedBridge.log("Class not found: $ANONYMOUS_CLASSNAME")
        }

        if (AutomaticBrightnessController != null && Anonymous != null) {
            BRIGHTENING_LIGHT_DEBOUNCE = Main.preferences!!.getString("brightness_fix_brighteningLightDebounce", "2000").toLong()
            DARKENING_LIGHT_DEBOUNCE = Main.preferences!!.getString("brightness_fix_darkeningLightDebounce", "4000").toLong()

//            XposedBridge.log("BRIGHTENING_LIGHT_DEBOUNCE = $BRIGHTENING_LIGHT_DEBOUNCE")
//            XposedBridge.log("DARKENING_LIGHT_DEBOUNCE = $DARKENING_LIGHT_DEBOUNCE")

            try {
                XposedHelpers.findAndHookMethod(Anonymous, "onSensorChanged", SensorEvent::class.java, object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                        try {
                            val parent = XposedHelpers.getSurroundingThis(param!!.thisObject)
                            val mLightSensorEnabled = XposedHelpers.getBooleanField(parent, "mLightSensorEnabled")
                            val sensorEvent = param.args[0] as SensorEvent

                            if (mLightSensorEnabled) {
                                val time = SystemClock.uptimeMillis()
                                val lux = sensorEvent.values[0]

                                XposedHelpers.setStaticFloatField(AutomaticBrightnessController, "last_lux", lux)
                                XposedHelpers.callMethod(parent, "handleLightSensorEvent", time, lux)

                                val mPrevLogTime = XposedHelpers.getLongField(param.thisObject, "mPrevLogTime")
                                val mPrevLogLux = XposedHelpers.getFloatField(param.thisObject, "mPrevLogLux")

                                if (time - mPrevLogTime >= 500L || 1.2f * mPrevLogLux <= lux || lux * 1.2f <= mPrevLogLux) {
                                    XposedHelpers.setLongField(param.thisObject, "mPrevLogTime", time)
                                    XposedHelpers.setFloatField(param.thisObject, "mPrevLogLux", lux)
                                }
                            }

//                            val `this$0` = XposedHelpers.getObjectField(param!!.thisObject, "this$0")
//                            val paramSensorEvent = param.args[0] as SensorEvent
//
//                            val mLightSensorEnabled = XposedHelpers.callStaticMethod(AutomaticBrightnessController, "access$200", `this$0`) as Boolean
//
//                            if (mLightSensorEnabled) {
//                                val time = SystemClock.uptimeMillis()
//                                val lux = paramSensorEvent.values[0]
//
//                                XposedHelpers.callStaticMethod(AutomaticBrightnessController, "access$302", lux)
//                                XposedHelpers.callStaticMethod(AutomaticBrightnessController, "access$400", `this$0`, time, lux)
//
//                                var mPrevLogTime: Long = XposedHelpers.getLongField(param.thisObject, "mPrevLogTime")
//                                var mPrevLogLux: Float = XposedHelpers.getFloatField(param.thisObject, "mPrevLogLux")
//
//                                if (time - mPrevLogTime >= 500L || 1.2f * mPrevLogLux <= lux || lux * 1.2f <= mPrevLogLux) {
//                                    //XposedBridge.log("time: " + time + " | mPrevLogTime: " + mPrevLogTime);
//
//                                    XposedHelpers.setLongField(param.thisObject, "mPrevLogTime", time)
//                                    XposedHelpers.setFloatField(param.thisObject, "mPrevLogLux", lux)
//                                }
//                            }
                        } catch (e: NoSuchFieldError) {
                            XposedBridge.log("No such fields: mPrevLogTime, mPrevLogLux")
                        }

                    }
                })
            } catch (e: NoSuchMethodError) {
                XposedBridge.log("No such method: onSensorChanged")
            }

            try {
                XposedHelpers.findAndHookMethod(AutomaticBrightnessController, "nextAmbientLightBrighteningTransition", java.lang.Long.TYPE, object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                        val mAmbientLightRingBuffer = XposedHelpers.getObjectField(param!!.thisObject, "mAmbientLightRingBuffer")

                        val size = XposedHelpers.callMethod(mAmbientLightRingBuffer, "size") as Int
                        var time = param.args[0] as Long

                        for (i in size.minus(1) downTo 0) {
                            //                            XposedBridge.log("nextAmbientLightBrighteningTransition: $i")
                            val lux = XposedHelpers.callMethod(mAmbientLightRingBuffer, "getLux", i) as Float
                            val mBrighteningLuxThreshold = XposedHelpers.getObjectField(param.thisObject, "mBrighteningLuxThreshold") as Float
                            if (i <= 0 || lux <= mBrighteningLuxThreshold) {
                                param.result = time + BRIGHTENING_LIGHT_DEBOUNCE
                                return
                            }

                            time = XposedHelpers.callMethod(mAmbientLightRingBuffer, "getTime", i) as Long
                        }
                    }
                })
            } catch (e: NoSuchMethodError) {
                XposedBridge.log("No such method: nextAmbientLightBrighteningTransition")
            }

            try {
                XposedHelpers.findAndHookMethod(AutomaticBrightnessController, "nextAmbientLightDarkeningTransition", java.lang.Long.TYPE, object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                        val mAmbientLightRingBuffer = XposedHelpers.getObjectField(param!!.thisObject, "mAmbientLightRingBuffer")

                        val size = XposedHelpers.callMethod(mAmbientLightRingBuffer, "size") as Int
                        var time = param.args[0] as Long

                        for (i in size.minus(1) downTo 0) {
                            //                            XposedBridge.log("nextAmbientLightDarkeningTransition: $i")
                            val lux = XposedHelpers.callMethod(mAmbientLightRingBuffer, "getLux", i) as Float
                            val mDarkeningLuxThreshold = XposedHelpers.getObjectField(param.thisObject, "mDarkeningLuxThreshold") as Float
                            if (i <= 0 || lux >= mDarkeningLuxThreshold) {
                                param.result = time + DARKENING_LIGHT_DEBOUNCE
                                return
                            }

                            time = XposedHelpers.callMethod(mAmbientLightRingBuffer, "getTime", i) as Long
                        }
                    }
                })
            } catch (e: NoSuchMethodError) {
                XposedBridge.log("No such method: nextAmbientLightDarkeningTransition")
            }
        }

        if (DisplayPowerController != null)
            try {
                XposedHelpers.findAndHookMethod(DisplayPowerController, "protectedMinimumBrightness", XC_MethodReplacement.returnConstant(LOW_DIMMING_PROTECTION_THRESHOLD))
            } catch (e: NoSuchMethodError) {
                XposedBridge.log("No such method: protectedMinimumBrightness")
            }
    }

    private val AUTOMATIC_BRIGHTNESS_CONTROLLER_CLASSNAME = "com.android.server.display.AutomaticBrightnessController"
    private val ANONYMOUS_CLASSNAME = "com.android.server.display.AutomaticBrightnessController$1"
    private val DISPLAY_POWER_CONTROLLER_CLASSNAME = "com.android.server.display.DisplayPowerController"

    private var BRIGHTENING_LIGHT_DEBOUNCE: Long = 2000L
    private var DARKENING_LIGHT_DEBOUNCE: Long = 4000L
    private val LOW_DIMMING_PROTECTION_THRESHOLD = 1
}
