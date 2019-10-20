package com.azoft.json2dart.delegates.ui

import com.azoft.json2dart.view.CollisionDialog
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.DialogBuilder

class IntellijUIDelegate : UIDelegate {
    override fun showCollisionDialog(
        leftSample: String,
        leftName: String,
        rightSample: String,
        rightName: String,
        onEnterName: (String, String) -> Unit,
        onAutomaticResolve: () -> Unit
    ) {
        ApplicationManager.getApplication().invokeAndWait {
            DialogBuilder().apply {
                val form = CollisionDialog(leftSample, rightSample, leftName, rightName)
                form.setOnAutoResolveListener {
                    window.dispose()
                    onAutomaticResolve()
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