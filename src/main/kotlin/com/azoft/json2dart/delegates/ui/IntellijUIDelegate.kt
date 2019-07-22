package com.azoft.json2dart.delegates.ui

import com.azoft.json2dart.view.SquashDialog
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.DialogBuilder

class IntellijUIDelegate : UIDelegate {
    override fun showDialogSquash(
        firstSample: String,
        firstName: String,
        secondSample: String,
        secondName: String,
        onEnterName: (String, String) -> Unit,
        onNotSquash: () -> Unit
    ) {
        ApplicationManager.getApplication().invokeAndWait {
            DialogBuilder().apply {
                val form = SquashDialog()
                form.setOnDontSquashListener {
                    window.dispose()
                    onNotSquash()
                }
                form.setOnEnterListener { leftName, rightName ->
                    window.dispose()
                    onEnterName(leftName, rightName)
                }
                setCenterPanel(form.rootView)
                removeAllActions()
                show()
            }
        }
    }
}