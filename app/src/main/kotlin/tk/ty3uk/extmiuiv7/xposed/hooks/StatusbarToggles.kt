package tk.ty3uk.extmiuiv7.xposed.hooks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Space
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tk.ty3uk.extmiuiv7.xposed.Main
import java.util.*

object StatusbarToggles {
    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.android.systemui") {
            try {
                val ToggleManager = XposedHelpers.findClass("miui.app.ToggleManager", lpparam.classLoader)
                val getUserSelectedToggleOrder = object: XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam?) {
                        Main.preferences!!.reload()

                        val toggleOrder = param!!.result as ArrayList<Int>
                        val newOrder: ArrayList<Int> = arrayListOf()
                        val showMore = Main.preferences!!.getBoolean("toggle_list_more", false)

                        for (i in toggleOrder)
                            if (Main.preferences!!.getBoolean("toggle_$i", true)) {
                                if (i == 29 && showMore)
                                    continue
                                newOrder.add(i)
                            }

                        param.result = newOrder
                    }
                }

                XposedBridge.hookAllMethods(ToggleManager, "getUserSelectedToggleOrder", getUserSelectedToggleOrder)

                if (Main.preferences!!.contains("toggle_list_count")) {
                    try {
                        XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.phone.StatusBarToggles", lpparam.classLoader, "bindToggle", View::class.java, Int::class.java, object : XC_MethodHook() {
                            override fun afterHookedMethod(param: MethodHookParam?) {
                                val toggle = param!!.args[0] as View
                                val columns = Integer.parseInt(Main.preferences!!.getString("toggle_list_count", "4"))

                                if (columns == 5)
                                    toggle.layoutParams.width = 216
                                else if (columns == 6)
                                    toggle.layoutParams.width = 180
                            }
                        })

                        XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.phone.StatusBarToggles", lpparam.classLoader, "inflateToggles", ArrayList::class.java, LinearLayout::class.java, Int::class.java, Int::class.java, object : XC_MethodReplacement() {
                            override fun replaceHookedMethod(param: MethodHookParam?) {
                                val toggleIDs = param!!.args[0] as ArrayList<Int>
                                val toggleGroup = param.args[1] as LinearLayout
                                val columns = Integer.parseInt(Main.preferences!!.getString("toggle_list_count", (param!!.args[2] as Int).toString()))
                                var toggleSpaceWeight = param.args[3] as Int
                                var spaceWeight = 100f
                                val mContext = XposedHelpers.getObjectField(param.thisObject, "mContext") as Context
                                val layout = mContext.resources.getIdentifier("status_bar_expanded_toggle", "layout", "com.android.systemui")

                                if (columns == 5)
                                    spaceWeight = 50f
                                else if (columns == 6)
                                    spaceWeight = 0f

                                if (columns >= 0) {
                                    var i = 0

                                    while (i.toDouble() < Math.ceil(toggleIDs.size.toDouble() / columns.toDouble())) {
                                        val lp = LinearLayout.LayoutParams(-1, -2)
                                        val toggleList = LinearLayout(mContext)
                                        toggleGroup.addView(toggleList, lp)

                                        if (columns < 5)
                                            toggleList.addView(Space(mContext), LinearLayout.LayoutParams(0, 0, toggleSpaceWeight.toFloat()))

                                        for (j in 0..columns-1) {
                                            LayoutInflater.from(mContext).inflate(layout, toggleList)
                                            if (columns < 5 && j != columns - 1)
                                                toggleList.addView(Space(mContext), LinearLayout.LayoutParams(0, 0, spaceWeight))
                                        }

                                        if (columns < 5)
                                            toggleList.addView(Space(mContext), LinearLayout.LayoutParams(0, 0, toggleSpaceWeight.toFloat()))

                                        i += 1
                                    }
                                }
                            }
                        })
                    } catch (e: NoSuchMethodError) {
                        XposedBridge.log("NoSuchMethod: inflateToggles")
                    }

                    try {
                        XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.phone.NotificationPanelView", lpparam.classLoader, "addToggleToBar", Int::class.java, object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam?) {
                                val mContext = XposedHelpers.getObjectField(param!!.thisObject, "mContext") as Context
                                val minWidthId = mContext.resources.getIdentifier("expanded_status_bar_toggle_min_width", "dimen", "com.android.systemui")
                                val togglesSpaceId = mContext.resources.getIdentifier("single_page_toggles_space", "dimen", "com.android.systemui")

                                if (minWidthId > 0 && togglesSpaceId > 0) {
                                    val initialToggleWidth = mContext.resources.getDimensionPixelSize(minWidthId) + mContext.resources.getDimensionPixelSize(togglesSpaceId)
                                    val count = Integer.parseInt(Main.preferences!!.getString("toggle_list_count", "4"))
                                    var multiplier = 1.0

                                    when (count) {
                                        5 -> multiplier = 0.8
                                        6 -> multiplier = 0.7
                                    }

                                    XposedHelpers.setIntField(param.thisObject, "mToggleWidth", initialToggleWidth.times(multiplier).toInt())
                                }
                            }
                        })
                    } catch (e: NoSuchMethodError) {
                        XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.phone.NotificationPanelView", lpparam.classLoader, "getToggleWidth", object : XC_MethodHook() {
                            override fun afterHookedMethod(param: MethodHookParam?) {
                                val count = Integer.parseInt(Main.preferences!!.getString("toggle_list_count", "4"))
                                var multiplier = 1.0

                                when (count) {
                                    5 -> multiplier = 0.8
                                    6 -> multiplier = 0.7
                                }

                                val mToggleWidth = param!!.result as Int

                                param.result = (mToggleWidth * multiplier).toInt()
                            }
                        })

                        XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.phone.ExpandableTogglesLayout", lpparam.classLoader, "setToggles", List::class.java, Int::class.java, Int::class.java, Int::class.java, Int::class.java, Int::class.java, object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam?) {
                                val initialCount = param!!.args[1] as Int
                                param.args[1] = Integer.parseInt(Main.preferences!!.getString("toggle_list_count", initialCount.toString()))
                            }
                        })

                        XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.phone.NotificationPanelView\$TogglePanelTouchHelper", lpparam.classLoader, "setExpandTogglesButtonState", Int::class.java, object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam?) {
                                val self = XposedHelpers.getSurroundingThis(param!!.thisObject)
                                val mHeader = XposedHelpers.getObjectField(self, "mHeader")
                                val expandToggleButton = XposedHelpers.callMethod(mHeader, "getExpandTogglesButton")

                                val expandableTogglesLayoutHeight = param.args[0] as Int
                                val mSmallTogglePanelHeight = XposedHelpers.getObjectField(self, "mSmallTogglePanelHeight") as Int
                                val mLargeTogglePanelHeight = XposedHelpers.getObjectField(self, "mLargeTogglePanelHeight") as Int

                                if (mLargeTogglePanelHeight <= mSmallTogglePanelHeight) {
                                    XposedHelpers.callMethod(expandToggleButton, "setRowHeightPercent", ((expandableTogglesLayoutHeight - mSmallTogglePanelHeight) * 100) / mLargeTogglePanelHeight)
                                    param.result = Any()
                                }
                            }
                        })
                    }
                }

//                if (Main.preferences!!.getBoolean("toggle_list_more", false))
//                    try {
//                        XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.phone.NotificationPanelView", lpparam.classLoader, "setExpandableTogglesData", List::class.java, object: XC_MethodHook() {
//                            override fun beforeHookedMethod(param: MethodHookParam?) {
//                                val mContext = XposedHelpers.getObjectField(param!!.thisObject, "mContext") as Context?
//
//                                XposedBridge.log("setExpandableTogglesData = ${(param.args[0] as List<Int>).count()}")
//
//                                if (mContext != null)
//                                    param.args[0] = miui.app.ToggleManager.getUserSelectedToggleOrder(mContext)
//                            }
//                        })
//
//                        XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.phone.ExpandableTogglesLayout", lpparam.classLoader, "setToggles", List::class.java, Int::class.java, Int::class.java, Int::class.java, Int::class.java, Int::class.java, object: XC_MethodHook() {
//                            override fun beforeHookedMethod(param: MethodHookParam?) {
//                                val mContext = XposedHelpers.callMethod(param!!.thisObject, "getContext") as Context?
//
//                                XposedBridge.log("setToggles = ${(param.args[0] as List<Int>).count()}")
//
//                                if (mContext != null)
//                                    param.args[0] = miui.app.ToggleManager.getUserSelectedToggleOrder(mContext)
//                            }
//                        })
//
//                        XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.phone.NotificationPanelView", lpparam.classLoader, "OnToggleOrderChanged", Boolean::class.java, object: XC_MethodHook() {
//                            override fun afterHookedMethod(param: MethodHookParam?) {
//                                val mContext = XposedHelpers.callMethod(param!!.thisObject, "getContext") as Context?
//
//                                XposedBridge.log("OnToggleOrderChanged = ${(XposedHelpers.getObjectField(param.thisObject, "mToggleIDs") as List<Int>).count()}")
//
//                                if (mContext != null)
//                                    XposedHelpers.setObjectField(param.thisObject, "mToggleIDs", miui.app.ToggleManager.getUserSelectedToggleOrder(mContext))
//                            }
//                        })
//                    } catch (e: NoSuchMethodError) {}
            } catch (e: XposedHelpers.ClassNotFoundError) { }
        }
    }
}