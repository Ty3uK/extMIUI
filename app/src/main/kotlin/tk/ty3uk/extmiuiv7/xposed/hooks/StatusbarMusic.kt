package tk.ty3uk.extmiuiv7.xposed.hooks

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.w3c.dom.Element
import tk.ty3uk.extmiuiv7.xposed.Main

class StatusbarMusic {
    @Throws(Throwable::class)
    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!lpparam.packageName.equals(HOOK_PACKAGE_NAME))
            return

        var screenElementClass = XposedHelpers.findClass(SCREEN_ELEMENT_CLASSNAME, lpparam.classLoader)
        var expressionClass = XposedHelpers.findClass(EXPRESSION_CLASSNAME, lpparam.classLoader)

        try {
            XposedHelpers.findAndHookMethod(COMMAND_TRIGGER_CLASSNAME, lpparam.classLoader, "load", Element::class.java, screenElementClass, object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    var element = param!!.args[0] as Element
                    element.removeAttribute("condition")
                }
            })
        } catch (e: ClassNotFoundException) {
            XposedBridge.log("No such class: $COMMAND_TRIGGER_CLASSNAME");
        } catch (e: NoSuchMethodError) {
            XposedBridge.log("No such method: $COMMAND_TRIGGER_CLASSNAME.load");
        }

        try {
            XposedHelpers.findAndHookMethod(ACTION_COMMAND_INTENT_COMMAND_CLASSNAME, lpparam.classLoader, "doPerform", object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    var mIntent = XposedHelpers.getObjectField(param!!.thisObject, "mIntent") as Intent?
                    val oldPkgName = mIntent?.component?.packageName
                    if ((oldPkgName != null && oldPkgName.equals("com.miui.player")) || mIntent?.getStringExtra("intent_sender").equals("notification_bar")) {
                        var variables = XposedHelpers.callMethod(param.thisObject, "getVariables")
                        var pkg = XposedHelpers.callMethod(
                                XposedHelpers.callStaticMethod(expressionClass, "build", variables, "@music_control.package"),
                                "evaluateStr"
                        ) as String?
                        var cls = XposedHelpers.callMethod(
                                XposedHelpers.callStaticMethod(expressionClass, "build", variables, "@music_control.class"),
                                "evaluateStr"
                        ) as String?
                        if (pkg != null && cls != null) {
                            if (pkg.equals("com.perm.kate") || pkg.equals("com.perm.kate.pro"))
                                cls = "com.perm.kate.PlayerActivity"
                        }
                        if (pkg == null || cls == null) {
                            pkg = Main.preferences!!.getString("statusbar_music_packageName", "com.miui.player")
                            cls = Main.preferences!!.getString("statusbar_music_packageActivity", "com.miui.player.ui.MusicBrowserActivity")
                        }
                        //XposedBridge.log("$PACKAGE_NAME: --- | $pkg | $cls | ---")
                        mIntent!!.action = "android.intent.action.MAIN"
                        mIntent.component = ComponentName(pkg, cls)
                        var mContext = XposedHelpers.callMethod(param.thisObject, "getContext") as Context
                        mContext.startActivity(mIntent)
                        param.result = null
                    }
                }
            })
        } catch (e: ClassNotFoundException) {
            XposedBridge.log("No such class: $ACTION_COMMAND_INTENT_COMMAND_CLASSNAME")
        } catch (e: NoSuchMethodError) {
            XposedBridge.log("No such method: $ACTION_COMMAND_INTENT_COMMAND_CLASSNAME.doPerform")
        }
    }

    companion object {
        private val PACKAGE_NAME = "tk.ty3uk.extmiuiv7"
        private val HOOK_PACKAGE_NAME = "com.android.systemui"
        private val ACTION_COMMAND_INTENT_COMMAND_CLASSNAME = "miui.maml.ActionCommand\$IntentCommand"
        private val COMMAND_TRIGGER_CLASSNAME = "miui.maml.CommandTrigger"
        private val SCREEN_ELEMENT_CLASSNAME = "miui.maml.elements.ScreenElement"
        private val EXPRESSION_CLASSNAME = "miui.maml.data.Expression"
    }
}