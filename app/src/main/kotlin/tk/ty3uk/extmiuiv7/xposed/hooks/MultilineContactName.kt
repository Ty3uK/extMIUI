package tk.ty3uk.extmiuiv7.xposed.hooks

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Process
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tk.ty3uk.extmiuiv7.xposed.hooks.util.ExitAppReceiver

class MultilineContactName {
    @Throws(Throwable::class)
    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName.equals(MMS_PACKAGE_NAME)) {
            try {
                XposedHelpers.findAndHookMethod(CONVERSATION_LIST_ITEM_CLASSNAME, lpparam.classLoader, "onFinishInflate", object: XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam?) {
                        var mFromView = XposedHelpers.getObjectField(param?.thisObject, "mFromView") as TextView?
                        mFromView?.setSingleLine(false)
                    }
                })
            } catch (e: NoSuchMethodError) {
                XposedBridge.log("No such method: ${CONVERSATION_LIST_ITEM_CLASSNAME}")
            } catch (e: NoSuchFieldError) {
                XposedBridge.log("No such field: mFromView")
            }
        }

        if (lpparam.packageName.equals(CONTACTS_PACKAGE_NAME)) {
            XposedHelpers.findAndHookMethod("com.android.contacts.ContactsActivity", lpparam.classLoader, "onCreate", Bundle::class.java, object : XC_MethodHook() {
                private var receiver: BroadcastReceiver? = null

                override fun afterHookedMethod(param: MethodHookParam?) {
                    receiver = object: BroadcastReceiver() {
                        override fun onReceive(p0: Context?, p1: Intent?) {
                            if (p1 != null && p1.hasExtra("multiline_contact_name")) {
                                XposedBridge.log("contacts receiver called")
                                try {
                                    System.exit(0)
                                } catch (e: Throwable) {
                                    Process.sendSignal(Process.myPid(), 9)
                                }
                            }
                        }
                    }

                    XposedHelpers.callMethod(param?.thisObject, "registerReceiver", receiver, IntentFilter("tk.ty3uk.extmiuiv7.action.RESTART"))
                }
            })

            try {
                XposedHelpers.findAndHookMethod(DIALER_LIST_CONTACTS_NORMAL_ITEM_CLASSNAME, lpparam.classLoader, "onFinishInflate", MultilineContactNameHook())
            } catch (e: ClassNotFoundException) {
                XposedBridge.log("No such class: ${DIALER_LIST_CONTACTS_NORMAL_ITEM_CLASSNAME}")
            } catch (e: NoSuchMethodError) {
                XposedBridge.log("No such method: ${DIALER_LIST_CONTACTS_NORMAL_ITEM_CLASSNAME}.onFinishInflate")
            }

            try {
                XposedHelpers.findAndHookMethod(DIALER_LIST_CONTACTS_SIMPLE_ITEM_CLASSNAME, lpparam.classLoader, "onFinishInflate", MultilineContactNameHook())
            } catch (e: ClassNotFoundException) {
                XposedBridge.log("No such class: ${DIALER_LIST_CONTACTS_SIMPLE_ITEM_CLASSNAME}")
            } catch (e: NoSuchMethodError) {
                XposedBridge.log("No such method: ${DIALER_LIST_CONTACTS_SIMPLE_ITEM_CLASSNAME}.onFinishInflate")
            }

            try {
                XposedHelpers.findAndHookMethod(DIALER_LIST_CALL_NORMAL_ITEM_CLASSNAME, lpparam.classLoader, "onFinishInflate", MultilineContactNameHook())
            } catch (e: ClassNotFoundException) {
                XposedBridge.log("No such class: ${DIALER_LIST_CALL_NORMAL_ITEM_CLASSNAME}")
            } catch (e: NoSuchMethodError) {
                XposedBridge.log("No such method: ${DIALER_LIST_CALL_NORMAL_ITEM_CLASSNAME}.onFinishInflate")
            }

            try {
                XposedHelpers.findAndHookMethod(DIALER_LIST_CALL_SIMPLE_ITEM_CLASSNAME, lpparam.classLoader, "onFinishInflate", MultilineContactNameHook())
            } catch (e: ClassNotFoundException) {
                XposedBridge.log("No such class: ${DIALER_LIST_CALL_SIMPLE_ITEM_CLASSNAME}")
            } catch (e: NoSuchMethodError) {
                XposedBridge.log("No such method: ${DIALER_LIST_CALL_SIMPLE_ITEM_CLASSNAME}.onFinishInflate")
            }

            try {
                XposedHelpers.findAndHookMethod(CONTACT_LIST_ITEM_VIEW_CLASSNAME, lpparam.classLoader, "getNameTextView", object: XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam?) {
                        var mNameTextView = XposedHelpers.getObjectField(param?.thisObject, "mNameTextView") as TextView
                        mNameTextView.setSingleLine(false)
                    }
                })
            } catch (e: ClassNotFoundException) {
                XposedBridge.log("No such class: ${CONTACT_LIST_ITEM_VIEW_CLASSNAME}");
            } catch (e: NoSuchMethodError) {
                XposedBridge.log("No such method: ${DIALER_LIST_CALL_SIMPLE_ITEM_CLASSNAME}.getNameTextView");
            }
        }

        if (lpparam.packageName.equals(INCALLUI_PACKAGE_NAME)) {
            try {
                XposedHelpers.findAndHookMethod("com.android.incallui.CallCardFragment", lpparam.classLoader, "onViewCreated", View::class.java, Bundle::class.java, MultlineInCallNameHook())
            } catch (e: XposedHelpers.ClassNotFoundError) {
                try {
                    XposedHelpers.findAndHookMethod(ANSWER_FRAGMENT_CLASSNAME, lpparam.classLoader, "onViewCreated", View::class.java, Bundle::class.java, MultlineInCallNameHook())
                } catch (e: ClassNotFoundException) {
                    XposedBridge.log("No such class: ${ANSWER_FRAGMENT_CLASSNAME}")
                } catch (e: NoSuchMethodError) {
                    XposedBridge.log("No such method: ${ANSWER_FRAGMENT_CLASSNAME}.onViewCreated")
                }
            }
        }
    }

    private class MultilineContactNameHook: XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            val mName = XposedHelpers.getObjectField(param?.thisObject, "mName") as TextView?
            mName?.setSingleLine(false)
        }
    }
    
    private class MultlineInCallNameHook: XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            try {
                val mPrimaryName = XposedHelpers.getObjectField(param?.thisObject, "mPrimaryName") as TextView?
                mPrimaryName?.setSingleLine(false)
            } catch (e: NoSuchFieldError) {
                XposedBridge.log("No such field: mPrimaryName")
            }
        }
    }

    companion object {
        private val CONTACTS_PACKAGE_NAME = "com.android.contacts"
        private val MMS_PACKAGE_NAME = "com.android.mms"
        private val INCALLUI_PACKAGE_NAME = "com.android.incallui"
        private val CONVERSATION_LIST_ITEM_CLASSNAME = "${MMS_PACKAGE_NAME}.ui.ConversationListItem"
        private val ANSWER_FRAGMENT_CLASSNAME = "${INCALLUI_PACKAGE_NAME}.AnswerFragment"
        private val DIALER_LIST_CONTACTS_NORMAL_ITEM_CLASSNAME = "${CONTACTS_PACKAGE_NAME}.calllog.DialerListContactNormalItem"
        private val DIALER_LIST_CONTACTS_SIMPLE_ITEM_CLASSNAME = "${CONTACTS_PACKAGE_NAME}.calllog.DialerListContactSimpleItem"
        private val DIALER_LIST_CALL_NORMAL_ITEM_CLASSNAME = "${CONTACTS_PACKAGE_NAME}.calllog.DialerListCallNormalItem"
        private val DIALER_LIST_CALL_SIMPLE_ITEM_CLASSNAME = "${CONTACTS_PACKAGE_NAME}.calllog.DialerListCallSimpleItem"
        private val CONTACT_LIST_ITEM_VIEW_CLASSNAME = "${CONTACTS_PACKAGE_NAME}.list.ContactListItemView"
    }
}