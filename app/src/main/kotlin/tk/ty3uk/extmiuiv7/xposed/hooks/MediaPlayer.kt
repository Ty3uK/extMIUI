package tk.ty3uk.extmiuiv7.xposed.hooks

import android.app.Activity
import android.app.AndroidAppHelper
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import de.robv.android.xposed.*
import tk.ty3uk.extmiuiv7.util.common
import tk.ty3uk.extmiuiv7.xposed.Main

/**
 * Created by ty3uk on 10.08.16.
 */
object MediaPlayer {
    fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {
        try {
            val method = XposedHelpers.findMethodBestMatch(Activity::class.java, "startActivityForResult", Intent::class.java, Int::class.java, Bundle::class.java)

            XposedBridge.hookMethod(method, object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    var mIntent = param!!.args[0] as Intent?

                    if (mIntent != null && mIntent.type != null) {
                        val mimePart = common.getMimePart(mIntent.type)

                        if (mimePart == "video" || mimePart == "audio") {
                            val component = mIntent.resolveActivity(AndroidAppHelper.currentApplication().packageManager)
                            if (component != null && (component.packageName == "com.miui.video" || component.packageName == "com.android.providers.downloads.ui") || component.packageName == "com.miui.music") {
                                val context = AndroidAppHelper.currentApplication().createPackageContext("tk.ty3uk.extmiuiv7", Context.CONTEXT_IGNORE_SECURITY)
                                val pkg: String?
                                val cls: String?
                                Main.preferences?.reload()

                                if (mimePart == "video") {
                                    pkg = Main.preferences!!.getString("media_player_video_packageName", null)
                                    cls = Main.preferences!!.getString("media_player_video_packageActivity", null)
                                } else {
                                    pkg = Main.preferences!!.getString("media_player_music_packageName", null)
                                    cls = Main.preferences!!.getString("media_player_music_packageActivity", null)
                                }

                                if (pkg != null && cls != null)
                                    mIntent.component = ComponentName(pkg, cls)
                                else
                                    mIntent = Intent.createChooser(mIntent, context.resources.getString(context.resources.getIdentifier("select_app", "string", "tk.ty3uk.extmiuiv7")))

                                mIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                param.args[0] = mIntent
                            }
                        }
                    }
                }
            })
        } catch(e: XposedHelpers.ClassNotFoundError) {
            XposedBridge.log("ExternalMediaPlayer: no such class")
        } catch (e: NoSuchMethodError) {
            XposedBridge.log("ExternalMediaPlayer: no such method")
        }
    }
}