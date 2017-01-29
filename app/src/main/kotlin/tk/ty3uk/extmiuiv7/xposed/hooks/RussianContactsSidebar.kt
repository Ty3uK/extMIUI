package tk.ty3uk.extmiuiv7.xposed.hooks

import android.content.res.XModuleResources
import android.content.res.XResources
import de.robv.android.xposed.IXposedHookZygoteInit
import tk.ty3uk.extmiuiv7.R

class RussianContactsSidebar {
    @Throws(Throwable::class)
    fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        val modRes = XModuleResources.createInstance(startupParam.modulePath, null)

        XResources.setSystemWideReplacement(
                "miui",
                "array",
                "alphabet_table",
                modRes.fwd(R.array.alphabet_table)
        )
        XResources.setSystemWideReplacement(
                "miui",
                "array",
                "alphabet_table_with_starred",
                modRes.fwd(R.array.alphabet_table_with_starred)
        )
    }
}
