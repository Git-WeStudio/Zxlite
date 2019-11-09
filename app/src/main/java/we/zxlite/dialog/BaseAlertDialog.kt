package we.zxlite.dialog

import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

abstract class BaseAlertDialog :DialogFragment(), CoroutineScope by MainScope(){

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

}