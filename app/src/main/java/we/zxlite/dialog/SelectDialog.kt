package we.zxlite.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog

class SelectDialog(private val callback: (Int) -> Unit) : BaseAlertDialog() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!).apply {
            setItems(arguments!!.getStringArray("nameList")) { _, which ->
                callback(which)
            }
        }.create()
    }
}