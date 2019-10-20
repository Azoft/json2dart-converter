package com.azoft.json2dart.delegates.ui

interface UIDelegate {
    fun showCollisionDialog(
        leftSample: String,
        leftName: String,
        rightSample: String,
        rightName: String,
        onEnterName: (String, String) -> Unit,
        onAutomaticResolve: () -> Unit
    )
}