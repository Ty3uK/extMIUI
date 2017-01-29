package tk.ty3uk.extmiuiv7.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.util.Pair
import android.support.v7.preference.*

import java.util.ArrayList

import miui.app.ToggleManager
import tk.ty3uk.extmiuiv7.R
import tk.ty3uk.extmiuiv7.util.common

class TogglesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = "${activity.packageName}_xposed"
        preferenceManager.sharedPreferencesMode = Context.MODE_WORLD_READABLE

        setPreferencesFromResource(R.xml.toggles_preferences, rootKey)

        if (common.isMiui()) {
            val prefs = preferenceManager.sharedPreferences
            val category = findPreference("toggle_list_category") as PreferenceCategory
            val count = findPreference("toggle_list_count") as ListPreference

            if (prefs.contains("toggle_list_count"))
                count.summary = String.format(resources.getString(R.string.current_value), prefs.getString("toggle_list_count", ""))

            count.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, any ->
                run {
                    preference.summary = String.format(resources.getString(R.string.current_value), any as String)
                    true
                }
            }

            val context = preferenceScreen.context
            val toggleIds = ToggleManager.getAllToggles(context)
            val toggles = ArrayList<Pair<String, Int>>()

            for (i in toggleIds.indices)
                toggles.add(Pair(
                        getContext().resources.getString(ToggleManager.getName(toggleIds[i])),
                        ToggleManager.getImageResource(toggleIds[i], false)))

            var key: String
            var iconId: Int
            var preference: CheckBoxPreference
            var bitmap: Bitmap
            var matrix: Matrix
            var icon: Drawable

            for (i in toggles.indices) {
                key = "toggle_" + toggleIds[i]
                iconId = toggles[i].second
                preference = CheckBoxPreference(context)

                bitmap = BitmapFactory.decodeResource(context.resources, iconId)
                matrix = Matrix()
                matrix.postScale(100.0f / bitmap.width, 100.0f / bitmap.height)
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                icon = BitmapDrawable(context.resources, bitmap)
                icon.setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY)

                preference.key = key
                preference.title = toggles[i].first
                preference.icon = icon
                preference.isChecked = prefs.getBoolean(key, true)

                category.addPreference(preference)
            }
        } else {
            findPreference("toggle_list").isEnabled = false
        }
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
