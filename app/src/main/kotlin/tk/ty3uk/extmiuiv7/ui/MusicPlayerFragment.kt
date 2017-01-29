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

class MusicPlayerFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = "${activity.packageName}_xposed"
        preferenceManager.sharedPreferencesMode = Context.MODE_WORLD_READABLE

        setPreferencesFromResource(R.xml.music_player_preferences, rootKey)

        val app = findPreference("statusbar_music_app")

        app.setOnPreferenceClickListener { preference -> run {
            common.listApps(context, common.APP_TYPE.MUSIC, { pkg, cls -> run {
                val prefs = this.preferenceManager.sharedPreferences
                val editor = prefs.edit()

                editor.putString("statusbar_music_packageName", pkg)
                editor.putString("statusbar_music_packageActivity", cls)

                preference?.summary = String.format(getString(R.string.select_app_summary), common.getApplicationName(activity.packageManager, pkg))

                editor.apply()
            }})

            true
        }}

        if (preferenceManager.sharedPreferences.contains("statusbar_music_packageName"))
            app.summary = String.format(getString(R.string.select_app_summary), common.getApplicationName(activity.packageManager, preferenceManager.sharedPreferences.getString("statusbar_music_packageName", null)))
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
