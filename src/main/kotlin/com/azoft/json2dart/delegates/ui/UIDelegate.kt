package com.azoft.json2dart.delegates.ui

import com.azoft.json2dart.view.SquashDialog
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.DialogBuilder

interface UIDelegate {
    fun showDialogSquash(
        firstSample: String,
        firstName: String,
        secondSample: String,
        secondName: String,
        onEnterName: (String, String) -> Unit,
        onNotSquash: () -> Unit
    )
}