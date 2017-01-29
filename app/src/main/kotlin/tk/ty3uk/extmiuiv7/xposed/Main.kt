package tk.ty3uk.extmiuiv7.xposed
import android.content.Context
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tk.ty3uk.extmiuiv7.xposed.hooks.*
import tk.ty3uk.extmiuiv7.xposed.hooks.MultilineContactName
import tk.ty3uk.extmiuiv7.xposed.hooks.receivers.ContactsReceiver
import tk.ty3uk.extmiuiv7.xposed.hooks.receivers.InCallUiReceiver
import tk.ty3uk.extmiuiv7.xposed.hooks.receivers.SystemUIReceiver

class Main : IXposedHookLoadPackage, IXposedHookZygoteInit, IXposedHookInitPackageResources {
    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        MODULE_PATH = startupParam.modulePath

        preferences = XSharedPreferences(PACKAGE_NAME, "${PACKAGE_NAME}_xposed")

        preferences?.makeWorldReadable()

        lockscreenDialer = LockscreenDialer()

        separateNotificationControl = SeparateNotificationSound()

        extendedReboot = ExtendedReboot()

        multilineContactName = MultilineContactName()

        russianContactsSidebar = RussianContactsSidebar()

        statusbarMusic = StatusbarMusic()

        statusbarBlur = StatusbarBlur()

        statusbarSearch = StatusbarSearch()

        themeComponentsButton = ThemeComponentsButton()

        animationScale = AnimationScale()

        if (preferences!!.getBoolean("lockscreen_dialer", false))
            lockscreenDialer.initZygote(startupParam)
        if (preferences!!.getBoolean("brightness_fix", false))
            BrightnessFix.initZygote(startupParam)
        if (preferences!!.getBoolean("russian_alphabet", false))
            russianContactsSidebar.initZygote(startupParam)
        if (preferences!!.getBoolean("media_player", false))
            MediaPlayer.initZygote(startupParam)
    }

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam?) {
        if (preferences!!.getBoolean("lockscreen_dialer", false))
            lockscreenDialer.initPackageResources(resparam!!)
        statusbarBlur.handleInitPackageResources(resparam!!)
        statusbarSearch.handleInitPackageResources(resparam!!)

        if (preferences!!.getBoolean("animation_scale", false))
            AnimationScale.handleInitPackageResources(resparam!!)
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        preferences!!.reload()

        if (preferences!!.getBoolean("separate_notification_sound", false))
            separateNotificationControl.handleLoadPackage(lpparam)
        if (preferences!!.getBoolean("extended_reboot", false))
            extendedReboot.handleLoadPackage(lpparam)
        if (preferences!!.getBoolean("lockscreen_dialer", false))
            lockscreenDialer.handleLoadPackage(lpparam)
        if (preferences!!.getBoolean("brightness_fix", false))
            BrightnessFix.handleLoadPackage(lpparam)
        if (preferences!!.getBoolean("multiline_contact_name", false))
            multilineContactName.handleLoadPackage(lpparam)
        if (preferences!!.getBoolean("russian_t9", false))
            RussianT9.handleLoadPackage(lpparam)
        if (preferences!!.getBoolean("statusbar_music", false))
            statusbarMusic.handleLoadPackage(lpparam)
        if (preferences!!.getBoolean("call_end_desktop", false))
            CallEndDesktop.handleLoadPackage(lpparam)
        if (preferences!!.getBoolean("theme_components_button", false))
            themeComponentsButton.handleLoadPackage(lpparam)
        if (preferences!!.getBoolean("toggle_list", false))
            StatusbarToggles.handleLoadPackage(lpparam)
        if (preferences!!.getBoolean("ram_fix", false))
            RamFix.handleLoadPackage(lpparam)

        SystemUIReceiver.handleLoadPackage(lpparam)
        InCallUiReceiver.handleLoadPackage(lpparam)
        ContactsReceiver.handleLoadPackage(lpparam)

        if (lpparam.packageName == PACKAGE_NAME)
            XposedHelpers.findAndHookMethod("$PACKAGE_NAME.util.common", lpparam.classLoader, "isActive", XC_MethodReplacement.returnConstant(true))
    }

    companion object {
        @Volatile var preferences: XSharedPreferences? = null
        var systemUIContext: Context? = null
        var MODULE_PATH = ""
        
        val PACKAGE_NAME = "tk.ty3uk.extmiuiv7"

        lateinit private var lockscreenDialer : LockscreenDialer
        lateinit private var separateNotificationControl : SeparateNotificationSound
        lateinit private var extendedReboot : ExtendedReboot
        lateinit private var multilineContactName : MultilineContactName
        lateinit private var russianContactsSidebar : RussianContactsSidebar
        lateinit private var statusbarMusic : StatusbarMusic
        lateinit private var statusbarBlur : StatusbarBlur
        lateinit private var statusbarSearch : StatusbarSearch
        lateinit private var themeComponentsButton: ThemeComponentsButton
        lateinit private var animationScale: AnimationScale
    }
}