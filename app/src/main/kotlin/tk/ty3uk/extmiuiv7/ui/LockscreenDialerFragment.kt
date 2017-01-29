package tk.ty3uk.extmiuiv7.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.SwitchPreferenceCompat

import tk.ty3uk.extmiuiv7.R
import tk.ty3uk.extmiuiv7.util.common

class LockscreenDialerFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = "${activity.packageName}_xposed"
        preferenceManager.sharedPreferencesMode = Context.MODE_WORLD_READABLE

        setPreferencesFromResource(R.xml.dialer_preferences, rootKey)

        val app = findPreference("lockscreen_dialer_app")

        app.setOnPreferenceClickListener { preference -> run {
            common.listApps(context, common.APP_TYPE.PHONE, { pkg, cls -> run {
                val prefs = this.preferenceManager.sharedPreferences
                val editor = prefs.edit()

                editor.putString("lockscreen_dialer_packageName", pkg)
                editor.putString("lockscreen_dialer_packageActivity", cls)

                preference?.summary = String.format(getString(R.string.select_app_summary), common.getApplicationName(activity.packageManager, pkg))

                editor.apply()
            }})

            true
        }}

        if (preferenceManager.sharedPreferences.contains("lockscreen_dialer_packageName"))
                app.summary = String.format(getString(R.string.select_app_summary), common.getApplicationName(activity.packageManager, preferenceManager.sharedPreferences.getString("lockscreen_dialer_packageName", null)))
    }

    override fun setDivider(divider: Drawable) {
        super.setDivider(ColorDrawable(Color.TRANSPARENT))
    }

    override fun setDividerHeight(height: Int) {
        super.setDividerHeight(0)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        StartActivity.putExtraToSave("restart_systemui", true)

        return true
    }
}
