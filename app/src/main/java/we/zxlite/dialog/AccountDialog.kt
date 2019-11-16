package we.zxlite.dialog

import android.app.Dialog
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import org.jetbrains.anko.db.replace
import org.jetbrains.anko.db.select
import org.jetbrains.anko.intentFor
import we.zxlite.R
import we.zxlite.activity.InitActivity
import we.zxlite.utils.BaseUtils.db
import we.zxlite.utils.SqlUtils.Helper.Companion.ITEM_NAME
import we.zxlite.utils.SqlUtils.Helper.Companion.ITEM_VALUE
import we.zxlite.utils.SqlUtils.Helper.Companion.SELECT_USER
import we.zxlite.utils.SqlUtils.Helper.Companion.TABLE_CFG
import we.zxlite.utils.SqlUtils.Helper.Companion.TABLE_RMB

class AccountDialog : BaseAlertDialog() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val users = ArrayList<String>()
        context!!.db.use {
            select(TABLE_RMB, ITEM_NAME).exec {
                if (moveToFirst()) while (true) {
                    users.add(getString(0))
                    if (isLast) break
                    moveToNext()
                }
            }
        }
        return AlertDialog.Builder(context!!)
            .setTitle(R.string.menuSwitchAccount)
            .setItems(users.toTypedArray()) { _, i ->
                context!!.db.use {
                    replace(TABLE_CFG, ITEM_NAME to SELECT_USER, ITEM_VALUE to users[i])
                }
                activity!!.startActivity(
                    activity!!.intentFor<InitActivity>()
                        .addFlags(FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(FLAG_ACTIVITY_NEW_TASK)
                )
            }.create()
    }

}