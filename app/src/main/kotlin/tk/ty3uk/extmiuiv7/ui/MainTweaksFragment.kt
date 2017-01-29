package tk.ty3uk.extmiuiv7.ui

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.SwitchPreferenceCompat

import tk.ty3uk.extmiuiv7.R

/**
 * A placeholder fragment containing a simple view.
 */
class MainTweaksFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = "${activity.packageName}_xposed"
        preferenceManager.sharedPreferencesMode = Context.MODE_WORLD_READABLE

        setPreferencesFromResource(R.xml.main_preferences, rootKey)

        prepareStatusbarBlur()
        prepareStatusbarSearch()
    }

    override fun setDivider(divider: Drawable?) {
        super.setDivider(ColorDrawable(Color.TRANSPARENT))
    }

    override fun setDividerHeight(height: Int) {
        super.setDividerHeight(0)
    }

    private fun prepareStatusbarBlur() {
        val statusbarBlur = preferenceScreen.findPreference("statusbar_blur") as SwitchPreferenceCompat

        try {
            val systemUIRes = activity.applicationContext.packageManager.getResourcesForApplication("com.android.systemui")
            if (!preferenceManager.sharedPreferences.contains("statusbar_blur"))
                statusbarBlur.isChecked = systemUIRes.getBoolean(
                        systemUIRes.getIdentifier("config_show_statusbar_blur_bg", "bool", "com.android.systemui")
                )
        } catch(e: Resources.NotFoundException) {
            statusbarBlur.isEnabled = false
        }
    }

    private fun prepareStatusbarSearch() {
        val statusbarSearch = preferenceScreen.findPreference("statusbar_search") as SwitchPreferenceCompat

        try {
            val systemUIRes = activity.applicationContext.packageManager.getResourcesForApplication("com.android.systemui")
            if (!preferenceManager.sharedPreferences.contains("statusbar_search"))
                statusbarSearch.isChecked = systemUIRes.getBoolean(
                        systemUIRes.getIdentifier("config_show_statusbar_search", "bool", "com.android.systemui")
                )
        } catch(e: Resources.NotFoundException) {
            statusbarSearch.isEnabled = false
        }
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference?.key != "toggles_count")
            when ((preference as SwitchPreferenceCompat).key) {
                "statusbar_search" -> {
                    StartActivity.putExtraToSave("statusbar_search", preference.isChecked)
                    StartActivity.putExtraToSave("reload_theme", true)
                }
                "statusbar_blur" -> {
                    StartActivity.putExtraToSave("statusbar_blur", preference.isChecked)
                    StartActivity.putExtraToSave("reload_theme", true)
                }
                "multiline_contact_name" -> {
                    StartActivity.putExtraToSave("restart_contacts", true)
                    StartActivity.putExtraToSave("restart_incallui", true)
                }
                "call_end_desktop" -> {
                    StartActivity.putExtraToSave("restart_incallui", true)
                }
                "ram_fix" -> {
                    StartActivity.putExtraToSave("restart_systemui", true)
                }
                "russian_t9" -> {
                    StartActivity.putExtraToSave("restart_contacts", true)
                }
                "russian_alphabet" -> {
                    StartActivity.putExtraToSave("restart_contacts", true)
                }
                else -> {
                    Snackbar.make(view!!, R.string.saved_reboot, Snackbar.LENGTH_INDEFINITE).show()
                }
            }

        return true
    }
}
