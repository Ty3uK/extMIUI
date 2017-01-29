package tk.ty3uk.extmiuiv7.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.preference.*

import tk.ty3uk.extmiuiv7.R
import tk.ty3uk.extmiuiv7.util.common

class BrightnessFixFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = "${activity.packageName}_xposed"
        preferenceManager.sharedPreferencesMode = Context.MODE_WORLD_READABLE

        setPreferencesFromResource(R.xml.brightness_preferences, rootKey)

        val autoBrightnessLevelsPreference = preferenceScreen.findPreference("brightness_fix_autoBrightnessLevels") as EditTextPreference
        val autoBrightnessLcdBacklightValuesPreference = preferenceScreen.findPreference("brightness_fix_autoBrightnessLcdBacklightValues") as EditTextPreference
        val brighteningLightDebouncePreference = preferenceScreen.findPreference("brightness_fix_brighteningLightDebounce") as EditTextPreference
        val darkeningLightDebouncePreference = preferenceScreen.findPreference("brightness_fix_darkeningLightDebounce") as EditTextPreference

        try {
            val autoBrightnessLevels = preferenceManager.sharedPreferences.getString(
                    "brightness_fix_autoBrightnessLevels",
                    common.IntArrayToString(resources.getIntArray(R.array.config_autoBrightnessLevels))
            )
            autoBrightnessLevelsPreference.text = autoBrightnessLevels

            val autoBrightnessLcdBacklightValues = preferenceManager.sharedPreferences.getString(
                    "brightness_fix_autoBrightnessLcdBacklightValues",
                    common.IntArrayToString(resources.getIntArray(R.array.config_autoBrightnessLcdBacklightValues))
            )
            autoBrightnessLcdBacklightValuesPreference.text = autoBrightnessLcdBacklightValues
        } catch (nfe: NumberFormatException) {}

        brighteningLightDebouncePreference.text = preferenceManager.sharedPreferences.getString("brightness_fix_brighteningLightDebounce", "2000").toString()
        darkeningLightDebouncePreference.text = preferenceManager.sharedPreferences.getString("brightness_fix_darkeningLightDebounce", "4000").toString()
    }

    override fun setDivider(divider: Drawable) {
        super.setDivider(ColorDrawable(Color.TRANSPARENT))
    }

    override fun setDividerHeight(height: Int) {
        super.setDividerHeight(0)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference?.key.equals("brightness_fix"))
            Snackbar.make(view!!, R.string.saved_reboot, Snackbar.LENGTH_INDEFINITE).show()

        return true
    }
}
