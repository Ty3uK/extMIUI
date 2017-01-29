package tk.ty3uk.extmiuiv7.util

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.anjlab.android.iab.v3.BillingProcessor
import miui.preference.PreferenceActivity
import tk.ty3uk.extmiuiv7.BuildConfig
import tk.ty3uk.extmiuiv7.R
import tk.ty3uk.extmiuiv7.ui.StartActivity
import java.util.*

object common {
    fun IntArrayToString(input: IntArray): String {
        val result = Arrays.toString(input)
        return result.substring(1, result.length - 1)
    }

    @Throws(NumberFormatException::class)
    fun StringToIntArray(input: String): IntArray {
        val stringArray = input.split(",".toRegex()).dropLastWhile { it.isEmpty() }
        val result: MutableList<Int> = mutableListOf()


        stringArray.mapTo(result) { Integer.parseInt(it.trim()) }

        return result.toIntArray()
    }

    fun isActive(): Boolean { return false }


    fun getAppActivity(context: Context, phoneFilter: Boolean, callback: (pkg: String, cls: String) -> Unit) {
        val pm = context.packageManager
        val progress = ProgressDialog.show(context, context.resources.getString(R.string.please_wait), context.resources.getString(R.string.listing_packages), true)

        val packageNameList: MutableList<String> = mutableListOf()
        val appNameList: MutableList<String> = mutableListOf()
        val appIconList: MutableList<Drawable> = mutableListOf()
        val activitiesList: MutableList<List<CharSequence>> = mutableListOf()

        val async = object: AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void?): Void? {
                for (pkg in pm.getInstalledPackages(PackageManager.GET_META_DATA)) {
                    try {
                        if (pkg != null) {
                            val permissions = pm.getPackageInfo(pkg.packageName, PackageManager.GET_PERMISSIONS).requestedPermissions
                            if ((permissions != null && !phoneFilter) || (permissions != null && phoneFilter &&
                                    permissions.contains(Manifest.permission.READ_CONTACTS) &&
                                    permissions.contains(Manifest.permission.WRITE_CONTACTS))) {

                                val activities = pm.getPackageInfo(pkg.packageName, PackageManager.GET_ACTIVITIES).activities
                                if (activities != null) {
                                    packageNameList.add(pkg.packageName)
                                    appNameList.add(pm.getApplicationLabel(pkg.applicationInfo) as String)
                                    appIconList.add(pm.getApplicationIcon(pkg.packageName))
                                    activitiesList.add(activities.map { it -> it.name.replace(pkg.packageName, "") })
                                }
                            }
                        }
                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                    }

                }
                return null
            }

            override fun onPostExecute(result: Void?) {
                progress.dismiss()
                val arrayAdapter: ListAdapter = object: ArrayAdapter<String>(context, R.layout.list_apps, appNameList)  {
                    inner class ViewHolder {
                        var icon: ImageView? = null
                        var title: TextView? = null
                    }

                    var holder: ViewHolder? = null

                    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                        var view = convertView
                        if (view == null) {
                            view = inflater.inflate(R.layout.list_apps, null)
                            holder = ViewHolder()
                            holder?.icon = view.findViewById(R.id.select_app_icon) as ImageView
                            holder?.title = view.findViewById(R.id.select_app_title) as TextView
                            view?.tag = holder
                        } else {
                            holder = view.tag as ViewHolder
                        }

                        holder?.title?.text = appNameList[position]
                        holder?.icon?.setImageDrawable(appIconList[position])

                        return view
                    }
                }
                AlertDialog.Builder(context)
                        .setTitle(R.string.select_package)
                        .setNegativeButton(R.string.cancel, { dialogInterface, i -> dialogInterface.dismiss()})
                        .setAdapter(arrayAdapter, { dialogInterface, which -> run {
                            dialogInterface.dismiss()
                            AlertDialog.Builder(context)
                                    .setTitle(appNameList[which])
//                                    .setIcon(-1)
//                                    .setIcon(appIconList[which])
                                    .setNegativeButton(R.string.cancel, { dialogInterface, i -> dialogInterface.dismiss()})
                                    .setItems(activitiesList[which].toTypedArray(), { di: DialogInterface, it: Int -> run {
                                        callback(packageNameList[which], activitiesList[which][it].toString())
                                    }})
                                    .create()
                                    .show()
                        }})
                        .create()
                        .show()
            }
        }
        async.execute()
    }

    fun checkXposed(context: Context) {
        if (!isActive()) {
            AlertDialog.Builder(context)
                    .setMessage(R.string.not_active)
                    .setPositiveButton(R.string.got_it, { dialogInterface, i -> dialogInterface.dismiss() })
                    .create()
                    .show()
        }
    }

    fun showChangelogAlert(context: Context) {
        AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.changelog_title))
                .setMessage(R.string.changelog_message)
                .setPositiveButton(R.string.got_it, { dialogInterface, i -> dialogInterface.dismiss() })
                .create()
                .show()
    }

    fun showDonateAlert(context: StartActivity, mBillingProcessor: BillingProcessor?): Boolean {
        if (BillingProcessor.isIabServiceAvailable(context)) {
            val donateList = arrayOf(
                    context.resources.getString(R.string.donate_small),
                    context.resources.getString(R.string.donate_medium),
                    context.resources.getString(R.string.donate_large),
                    context.resources.getString(R.string.donate_yamoney),
                    context.resources.getString(R.string.donate_qiwi)
            )
            AlertDialog.Builder(context)
                    .setTitle(R.string.donate_amount)
                    .setNegativeButton(R.string.cancel, {dialogInterface, i -> dialogInterface.dismiss()})
                    .setItems(donateList, {dialogInterface, which -> run {
                        when (which) {
                            0 -> mBillingProcessor?.purchase(context, SKU_1USD)
                            1 -> mBillingProcessor?.purchase(context, SKU_2USD)
                            2 -> mBillingProcessor?.purchase(context, SKU_3USD)
                            3 -> context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(YAMONEY_LINK)))
                            4 -> run {
                                dialogInterface.dismiss()

                                AlertDialog.Builder(context)
                                        .setTitle(R.string.donate_qiwi)
                                        .setMessage(R.string.donate_qiwi_message)
                                        .setNegativeButton(R.string.cancel, {dialogInterface, i -> dialogInterface.dismiss()})
                                        .setPositiveButton(R.string.donate_qiwi_goto, {dialogInterface, which -> run{
                                            val clipboard = context.getSystemService(PreferenceActivity.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.primaryClip = ClipData.newPlainText("QIWI email", QIWI_EMAIL)
                                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(QIWI_LINK)))
                                        }})
                                        .create()
                                        .show()
                            }
                            else -> {}
                        }

                        dialogInterface.dismiss()
                    }})
                    .create()
                    .show()

            return true
        } else {
            AlertDialog.Builder(context)
                    .setMessage(R.string.services_not_available)
                    .setNegativeButton(R.string.got_it, { dialogInterface, i -> dialogInterface.dismiss() })
                    .create()
                    .show()
            return true
        }
    }

    fun showDonateAlertInfo(context: Context) {
        AlertDialog.Builder(context)
                .setTitle(R.string.donate_alert_title)
                .setMessage(R.string.donate_alert_message)
                .setPositiveButton(R.string.got_it, {dialogInterface, i -> dialogInterface.dismiss()})
                .create()
                .show()
    }

    fun getApplicationName(packageManager: PackageManager, packageName: String): CharSequence {
        return packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA))
    }

    fun showApplicationsAlert(context: Context, appNames: List<String>, appActivities: List<String>, appIcons: List<Drawable>, callback: (String, String) -> Unit, needClear: Boolean = false, clearCallback: DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() }) {
        val arrayAdapter: ListAdapter = object: ArrayAdapter<String>(context, R.layout.list_apps, appNames)  {
            inner class ViewHolder {
                var icon: ImageView? = null
                var title: TextView? = null
                var activity: TextView? = null
            }

            var holder: ViewHolder? = null

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                var view = convertView
                if (view == null) {
                    view = inflater.inflate(R.layout.list_apps, null)
                    holder = ViewHolder()
                    holder?.icon = view.findViewById(R.id.select_app_icon) as ImageView
                    holder?.title = view.findViewById(R.id.select_app_title) as TextView
                    holder?.activity = view.findViewById(R.id.select_app_activity) as TextView
                    view?.tag = holder
                } else {
                    holder = view.tag as ViewHolder
                }

                holder?.title?.text = getApplicationName(context.packageManager, appNames[position])
                holder?.icon?.setImageDrawable(appIcons[position])
                holder?.activity?.text = appActivities[position].replace(appNames[position], "")

                return view
            }
        }
        val dialog = AlertDialog.Builder(context)
                .setTitle(R.string.select_package)
                .setNegativeButton(R.string.cancel, { dialogInterface, i -> dialogInterface.dismiss()})
                .setAdapter(arrayAdapter, { dialogInterface, which -> run {
                    callback(appNames[which], appActivities[which])
                }})

        if (needClear)
            dialog.setPositiveButton(R.string.external_media_player_clear, clearCallback)

        dialog
                .create()
                .show()
    }

    fun listApps(context: Context, type: APP_TYPE, callback: (String, String) -> Unit, needClear: Boolean = false, clearCallback: DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() }) {
        val classNames: MutableList<String> = mutableListOf()
        val packageNames: MutableList<String> = mutableListOf()
        val icons: MutableList<Drawable> = mutableListOf()

        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_VIEW)

        when (type) {
            APP_TYPE.VIDEO -> intent.setDataAndType(Uri.parse("https://example.com/01.mp4"), "video/*")
            APP_TYPE.MUSIC -> intent.setDataAndType(Uri.parse("file://01.mp4"), "audio/*")
            APP_TYPE.PHONE -> {
                intent.action = Intent.ACTION_DIAL
                intent.data = Uri.parse("tel:1122")
            }
        }

        val apps = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA)

        for (app in apps) {
            classNames.add(app.activityInfo.name)
            packageNames.add(app.activityInfo.packageName)
            icons.add(pm.getApplicationIcon(app.activityInfo.packageName))
        }

        common.showApplicationsAlert(context, packageNames, classNames, icons, callback, needClear, clearCallback)
    }

    fun checkPermissions(context: Context): Boolean {
        return PERMISSIONS.none { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_DENIED }
    }

    fun isMiui(): Boolean {
        try {
            Class.forName("miui.app.ToggleManager")
        } catch (e: ClassNotFoundException) {
            return false
        }

        return true
    }

    fun getMimePart(mime: String, isLeft: Boolean = true): String {
        val slashPosition = mime.indexOf('/')

        if (isLeft)
            return mime.substring(0, slashPosition)
        else
            return mime.substring(slashPosition + 1)
    }

    private val SKU_1USD = "1_usd"
    private val SKU_2USD = "2_usd"
    private val SKU_3USD = "3_usd"
    private val YAMONEY_LINK = "https://money.yandex.ru/to/410012095863336"
    private val QIWI_LINK = "https://qiwi.com/transfer/email.action"
    private val QIWI_EMAIL = "max.karelov@gmail.com"

    val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.READ_SYNC_SETTINGS,
            Manifest.permission.READ_PHONE_STATE

    )

    enum class APP_TYPE {
        MUSIC, VIDEO, PHONE
    }
}
