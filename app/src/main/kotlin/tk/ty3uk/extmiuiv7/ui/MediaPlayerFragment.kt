package tk.ty3uk.extmiuiv7.ui

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.SwitchPreferenceCompat

import tk.ty3uk.extmiuiv7.R
import tk.ty3uk.extmiuiv7.util.common

class MediaPlayerFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = "${activity.packageName}_xposed"
        preferenceManager.sharedPreferencesMode = Context.MODE_WORLD_READABLE

        setPreferencesFromResource(R.xml.media_player_preferences, rootKey)

        val videoApp = findPreference("media_player_video_app")
        val musicApp = findPreference("media_player_music_app")

        videoApp.setOnPreferenceClickListener { preference -> run {
            common.listApps(context, common.APP_TYPE.VIDEO, { pkg, cls -> run {
                val prefs = this.preferenceManager.sharedPreferences
                val editor = prefs.edit()

                editor.putString("media_player_video_packageName", pkg)
                editor.putString("media_player_video_packageActivity", cls)

                videoApp.summary = String.format(getString(R.string.select_app_summary), common.getApplicationName(activity.packageManager, pkg))
                editor.apply()
            }}, true, DialogInterface.OnClickListener { dialogInterface, i ->  run{
                clearCallback(videoApp)
                dialogInterface.dismiss()
            }})

            true
        }}

        musicApp.setOnPreferenceClickListener { preference -> run {
            common.listApps(context, common.APP_TYPE.MUSIC, { pkg, cls -> run {
                val prefs = this.preferenceManager.sharedPreferences
                val editor = prefs.edit()

                editor.putString("media_player_music_packageName", pkg)
                editor.putString("media_player_music_packageActivity", cls)

                musicApp.summary = String.format(getString(R.string.select_app_summary), common.getApplicationName(activity.packageManager, pkg))
                editor.apply()
            }}, true, DialogInterface.OnClickListener { dialogInterface, i ->  run{
                clearCallback(musicApp)
                dialogInterface.dismiss()
            }})

            true
        }}

        if (preferenceManager.sharedPreferences.contains("media_player_video_packageName"))
            videoApp.summary = String.format(getString(R.string.select_app_summary), common.getApplicationName(activity.packageManager, preferenceManager.sharedPreferences.getString("media_player_video_packageName", null)))
        if (preferenceManager.sharedPreferences.contains("media_player_music_packageName"))
            musicApp.summary = String.format(getString(R.string.select_app_summary), common.getApplicationName(activity.packageManager, preferenceManager.sharedPreferences.getString("media_player_music_packageName", null)))
    }

    override fun setDivider(divider: Drawable) {
        super.setDivider(ColorDrawable(Color.TRANSPARENT))
    }

    override fun setDividerHeight(height: Int) {
        super.setDividerHeight(0)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference?.key != "media_player")
            StartActivity.putExtraToSave("restart_systemui", true)
        else
            Snackbar.make(view!!, R.string.saved_reboot, Snackbar.LENGTH_INDEFINITE).show()

        return true
    }

    fun clearCallback(preference: Preference) {
        val prefs = this.preferenceManager.sharedPreferences
        val editor = prefs.edit()

        if (preference.key == "media_player_video_app") {
            editor.remove("media_player_video_packageName")
            editor.remove("media_player_video_packageActivity")
        } else {
            editor.remove("media_player_music_packageName")
            editor.remove("media_player_music_packageActivity")
        }

        preference.summary = ""

        editor.apply()
    }
}
