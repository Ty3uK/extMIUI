package tk.ty3uk.extmiuiv7.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.preference.EditTextPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.SwitchPreferenceCompat

import tk.ty3uk.extmiuiv7.R

class AnimationFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = "${activity.packageName}_xposed"
        preferenceManager.sharedPreferencesMode = Context.MODE_WORLD_READABLE

        setPreferencesFromResource(R.xml.animation_preferences, rootKey)

        val enable = findPreference("animation_scale") as SwitchPreferenceCompat
        val window = findPreference("animation_scale_window") as EditTextPreference
        val transition = findPreference("animation_scale_transition") as EditTextPreference
        val duration = findPreference("animation_scale_duration") as EditTextPreference

        enable.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue -> run {
            window.isEnabled = newValue as Boolean
            transition.isEnabled = newValue
            duration.isEnabled = newValue
            true
        }}

        setAnimationScaleSummary(window, preferenceManager.sharedPreferences.getString(window.key, ""))
        window.setOnPreferenceChangeListener { preference, any -> onAnimationScaleChange(preference as EditTextPreference, any as String) }

        setAnimationScaleSummary(transition, preferenceManager.sharedPreferences.getString(transition.key, ""))
        transition.setOnPreferenceChangeListener { preference, any -> onAnimationScaleChange(preference as EditTextPreference, any as String) }

        setAnimationScaleSummary(duration, preferenceManager.sharedPreferences.getString(duration.key, ""))
        duration.setOnPreferenceChangeListener { preference, any -> onAnimationScaleChange(preference as EditTextPreference, any as String) }
    }

    override fun setDivider(divider: Drawable) {
        super.setDivider(ColorDrawable(Color.TRANSPARENT))
    }

    override fun setDividerHeight(height: Int) {
        super.setDividerHeight(0)
    }

    private fun setAnimationScaleSummary(preference: EditTextPreference, value: String) {
        if (!value.equals("")) {
            preference.summary = String.format(getString(R.string.current_value, value))
        } else {
            preference.summary = ""
        }
    }

    private fun onAnimationScaleChange(preference: EditTextPreference, newValue: String?) : Boolean {
        try {
            if (newValue != null) {
                val floatValue = newValue.toFloat()

                if (floatValue is Float)
                    preference.summary = String.format(getString(R.string.current_value, newValue))
                else
                    preference.summary = ""

                return newValue.equals("") || newValue.toFloat() is Float
            }
            return false
        } catch (e: NumberFormatException) {
//                preference.text = ""
            return false
        }
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference?.key.equals("animation_scale"))
            Snackbar.make(view!!, R.string.saved_reboot, Snackbar.LENGTH_INDEFINITE).show()

        return true
    }
}
