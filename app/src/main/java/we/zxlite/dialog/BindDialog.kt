package we.zxlite.dialog

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import we.zxlite.R

class BindDialog : BaseSheetDialog() {

    override fun onCreateDialog(savedInstanceState: Bundle?) = BottomSheetDialog(context!!).apply {
        setContentView(R.layout.dialog_bind)
    }

}