package we.zxlite.dialog

import android.os.Bundle
import androidx.appcompat.app.AlertDialog

class SelectFriendsDialog(private val callback: (Int) -> Unit) : BaseAlertDialog() {

    companion object {
        private const val NAME_LIST = "nameList"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        AlertDialog.Builder(context!!)
            .setItems(arguments!!.getStringArray(NAME_LIST)) { _, which -> callback(which) }
            .create()
}