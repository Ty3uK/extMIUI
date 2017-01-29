package tk.ty3uk.extmiuiv7.xposed.hooks

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tk.ty3uk.extmiuiv7.xposed.hooks.util.T9miuisu

object RussianT9 {
    @Throws(Throwable::class)
    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!lpparam.packageName.equals(PACKAGE_NAME))
            return

        XposedHelpers.findAndHookMethod(T9BUILDER_CLASSNAME, lpparam.classLoader, "convertNameToT9Key", String::class.java, object: XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                var paramString = param!!.args[0] as String
                var T9Utils = XposedHelpers.findClass(T9UTILS_CLASSNAME, lpparam.classLoader)
                var stringBuilder = XposedHelpers.callStaticMethod(T9Utils, "getReusableStringBuilder") as StringBuilder
                var c1 = ' '.toChar()
                var i = 0

                for (c2: Char in paramString) {
                    if (c2.equals(';'))
                        stringBuilder.append(';')
                    else {
                        var c3 = T9miuisu.formatCharToT9(c2)

                        if (c3 == 0.toChar())
                            c3 = ' '.toChar()
                        else {
                            if ((Character.isUpperCase(c2)) || (i == 0) || ((Character.isLetter(c2)) && (!Character.isLetter(c1)))) {
                                c3 = XposedHelpers.callStaticMethod(T9Utils, "converDigitToInitial", c3) as Char
                            } else if ((Character.isDigit(c2)) && (!Character.isDigit(c1))) {
                                c3 = XposedHelpers.callStaticMethod(T9Utils, "converDigitToInitial", c3) as Char
                            } else if (XposedHelpers.callStaticMethod(T9Utils, "isValidT9Key", c2) as Boolean) {
                                c3 = XposedHelpers.callStaticMethod(T9Utils, "converDigitToInitial", c3) as Char
                            }
                        }

                        stringBuilder.append(c3)
                        c1 = c2
                    }

                    i++
                }

                var str = stringBuilder.toString()
                XposedHelpers.callStaticMethod(T9Utils, "recyle", stringBuilder)
                param.result = str
            }
        })

        XposedHelpers.findAndHookMethod(T9BUILDER_CLASSNAME, lpparam.classLoader, "filterNonPinyinAndZhuyin", String::class.java, object: XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                var paramString = param!!.args[0] as String
                var T9Utils = XposedHelpers.findClass(T9UTILS_CLASSNAME, lpparam.classLoader)
                var stringBuilder = XposedHelpers.callStaticMethod(T9Utils, "getReusableStringBuilder") as StringBuilder
                stringBuilder.setLength(0)

                for (char: Char in paramString) {
                    if (T9miuisu.formatCharToT9(char) != 0.toChar())
                        stringBuilder.append(char)
                }

                var str = stringBuilder.toString()
                XposedHelpers.callStaticMethod(T9Utils, "recyle", stringBuilder)
                param.result = str
            }
        })
    }

    private val PACKAGE_NAME = "com.android.providers.contacts"
    private val T9BUILDER_CLASSNAME = "$PACKAGE_NAME.t9.T9Builder"
    private val T9UTILS_CLASSNAME = "$PACKAGE_NAME.t9.T9Utils"
}