package tk.ty3uk.extmiuiv7.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import tk.ty3uk.extmiuiv7.R
import tk.ty3uk.extmiuiv7.BuildConfig

/**
 * Created by ty3uk on 01.09.16.
 */
class AboutFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.about_fragment, container, false)

        val toolbar = activity.findViewById(R.id.toolbar) as Toolbar
        toolbar.setTitle(R.string.about)

        val appName = view.findViewById(R.id.app_name) as TextView
        val thanksTo = view.findViewById(R.id.thanks_to) as TextView
        val telegram = view.findViewById(R.id.telegram) as ImageView
        val forpda = view.findViewById(R.id.forpda) as ImageView
        val gmail = view.findViewById(R.id.gmail) as ImageView

        appName.text = String.format("%1\$s %2\$s", appName.text, BuildConfig.VERSION_NAME)
        thanksTo.movementMethod = LinkMovementMethod.getInstance()
        telegram.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://telegram.me/xxxTy3uKxxx"))) }
        forpda.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://4pda.ru/forum/index.php?showtopic=741043"))) }
        gmail.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("mailto:max.karelov@gmail.com"))) }

        return view
    }
}
