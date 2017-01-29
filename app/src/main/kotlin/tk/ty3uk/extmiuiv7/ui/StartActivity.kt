package tk.ty3uk.extmiuiv7.ui

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import kotlinx.android.synthetic.main.app_bar_main.view.*

import tk.ty3uk.extmiuiv7.R
import tk.ty3uk.extmiuiv7.util.common

class StartActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, BillingProcessor.IBillingHandler {

    lateinit private var mCurrentFragment: Fragment
    lateinit private var mToolbar: Toolbar
    lateinit private var mSaveFabButton: FloatingActionButton
    lateinit private var mSaveIntent: Intent
    private var useDarkTheme: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        useDarkTheme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("use_dark_theme", false)

        if (useDarkTheme)
            this.setTheme(R.style.AppTheme_Dark_NoActionBar)

        setContentView(R.layout.activity_start)

        mToolbar = findViewById(R.id.toolbar) as Toolbar
        mToolbar.setTitle(R.string.app_name)

        if (useDarkTheme)
            mToolbar.popupTheme = R.style.AppTheme_Dark_PopupOverlay

        setSupportActionBar(mToolbar)

        if (useDarkTheme)
            (findViewById(R.id.appbar_layout) as AppBarLayout).context.setTheme(R.style.AppTheme_Dark_AppBarOverlay)

//        if (common.isMiui()) {
        if (true) {
            mBillingProcessor = BillingProcessor(this, PUBLIC_KEY_BASE64, this)

            mSaveFabButton = findViewById(R.id.fab_save) as FloatingActionButton
            mSaveFabButton.isEnabled = false
            mSaveFabButton.setOnClickListener { view ->
                run {
                    sendBroadcast(mSaveIntent)
                    createSaveIntent()
                    Snackbar.make(view, R.string.saved, Snackbar.LENGTH_LONG).show()
                    mSaveFabButton.hide()
                    mSaveFabButton.isEnabled = false
                }
            }

            val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
            val toggle = ActionBarDrawerToggle(
                    this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
            drawer.addDrawerListener(toggle)
            toggle.syncState()

            val navigationView = findViewById(R.id.nav_view) as NavigationView
            navigationView.setNavigationItemSelectedListener(this)
            navigationView.setCheckedItem(R.id.nav_main)

            if (savedInstanceState == null) {
                mCurrentFragment = MainTweaksFragment()

                supportFragmentManager.beginTransaction().replace(R.id.content, mCurrentFragment).commit()
            }

            mInstance = this
            createSaveIntent()

            common.checkXposed(this)

            if (Build.VERSION.SDK_INT >= 23)
                if (!common.checkPermissions(this))
                    ActivityCompat.requestPermissions(this, common.PERMISSIONS, 1)

            if (Build.VERSION.SDK_INT >= 21) {
                // clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

                // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

                // finally change the color
//                window.statusBarColor = resources.getColor(R.color.colorPrimary, null)
            }

//            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        } else {
            AlertDialog.Builder(this)
                    .setMessage(R.string.not_miui)
                    .setPositiveButton(R.string.got_it, { dialogInterface, i -> this.finish() })
                    .show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            val drawer = findViewById(R.id.drawer_layout) as DrawerLayout

            if (!drawer.isDrawerOpen(GravityCompat.START))
                drawer.openDrawer(GravityCompat.START)
            else
                drawer.closeDrawer(GravityCompat.START)

            return true
        }

        return super.onKeyUp(keyCode, event)
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        menu.findItem(R.id.action_switch_theme)?.isChecked = useDarkTheme

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        when (id) {
            R.id.action_donate -> common.showDonateAlert(this, mBillingProcessor)
            R.id.action_switch_theme -> {
                item.isChecked = !item.isChecked

                val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()
                editor.putBoolean("use_dark_theme", item.isChecked)
                editor.apply()

                AlertDialog.Builder(this)
                        .setMessage(R.string.switch_theme_message)
                        .setPositiveButton(R.string.got_it, { dialogInterface, i -> run{
                            val intent = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        }})
                        .show()

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        when (id) {
            R.id.nav_main -> if ((mCurrentFragment !is MainTweaksFragment)) {
                try {
                    mCurrentFragment.onDestroy()
                } catch (e: NullPointerException) {
                }

                mCurrentFragment = MainTweaksFragment()
            }

            R.id.nav_dialer -> if (mCurrentFragment !is LockscreenDialerFragment) {
                try {
                    mCurrentFragment.onDestroy()
                } catch (e: NullPointerException) {
                }

                mCurrentFragment = LockscreenDialerFragment()
            }

            R.id.nav_music -> if (mCurrentFragment !is MusicPlayerFragment) {
                try {
                    mCurrentFragment.onDestroy()
                } catch (e: NullPointerException) {
                }

                mCurrentFragment = MusicPlayerFragment()
            }

            R.id.nav_video -> if (mCurrentFragment !is MediaPlayerFragment) {
                try {
                    mCurrentFragment.onDestroy()
                } catch (e: NullPointerException) {
                }

                mCurrentFragment = MediaPlayerFragment()
            }

            R.id.nav_brightness -> if (mCurrentFragment !is BrightnessFixFragment) {
                try {
                    mCurrentFragment.onDestroy()
                } catch (e: NullPointerException) {
                }

                mCurrentFragment = BrightnessFixFragment()
            }

            R.id.nav_animation -> if (mCurrentFragment !is AnimationFragment) {
                try {
                    mCurrentFragment.onDestroy()
                } catch (e: NullPointerException) {
                }

                mCurrentFragment = AnimationFragment()
            }

            R.id.nav_toggles -> if (mCurrentFragment !is TogglesFragment) {
                try {
                    mCurrentFragment.onDestroy()
                } catch (e: NullPointerException) {
                }

                mCurrentFragment = TogglesFragment()
            }

            R.id.nav_about -> if (mCurrentFragment !is AboutFragment) {
                try {
                    mCurrentFragment.onDestroy()
                } catch (e: NullPointerException) {
                }

                mCurrentFragment = AboutFragment()
            }

            R.id.nav_changlelog -> {
                common.showChangelogAlert(this)
            }
        }

        if (id != R.id.nav_about && id != R.id.nav_changlelog)
            mToolbar.setTitle(R.string.app_name)

        if (id != R.id.nav_changlelog)
            supportFragmentManager.beginTransaction().replace(R.id.content, mCurrentFragment).commit()

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)

        return (id != R.id.nav_changlelog)
    }

    override fun onBillingInitialized() {
        //throw UnsupportedOperationException()
    }

    override fun onProductPurchased(productId: String?, details: TransactionDetails?) {
        AlertDialog.Builder(this)
                .setMessage(R.string.purchased)
                .setPositiveButton(R.string.got_it, {dialogInterface, i -> dialogInterface.dismiss()})
                .create()
                .show()
        //throw UnsupportedOperationException()
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        //throw UnsupportedOperationException()
    }

    override fun onPurchaseHistoryRestored() {
        //throw UnsupportedOperationException()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!mBillingProcessor!!.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        mBillingProcessor?.release()
        super.onDestroy()
    }

    fun createSaveIntent() {
        mSaveIntent = Intent("tk.ty3uk.extmiuiv7.action.RESTART")
        mSaveIntent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
    }

    companion object {
        private val PUBLIC_KEY_BASE64 = ""
        private var mBillingProcessor: BillingProcessor? = null

        private var mInstance: StartActivity? = null

        fun putExtraToSave(key: String, value: Boolean) {
            if (mInstance?.mSaveFabButton!!.visibility == View.GONE)
                mInstance?.mSaveFabButton?.show()
            if (!mInstance?.mSaveFabButton!!.isEnabled)
                mInstance?.mSaveFabButton?.isEnabled = true
            mInstance?.mSaveIntent?.putExtra(key, value)
        }

    }
}
