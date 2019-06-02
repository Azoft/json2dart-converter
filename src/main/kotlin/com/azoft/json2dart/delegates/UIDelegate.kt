package com.azoft.json2dart.delegates

import com.azoft.json2dart.view.SquashDialog
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.DialogBuilder

class UIDelegate {
    fun showDialogSquash(
        firstSample: String,
        secondSample: String,
        onEnterName: (String) -> Unit,
        onNotSquash: () -> Unit
    ) {
        ApplicationManager.getApplication().invokeAndWait {
            DialogBuilder().apply {
                val form = SquashDialog()
                form.setOnDontSquashListener {
                    window.dispose()
                    onNotSquash()
                }
                form.setOnEnterListener {
                    window.dispose()
                    onEnterName(it)
                }
                setCenterPanel(form.rootView)
                setTitle("Json2Dart")
                removeAllActions()
                show()
            }
        }
    }
}