package com.azoft.json2dart


import com.azoft.json2dart.generator.DartClassGenerator
import com.azoft.json2dart.view.Json2DartForm
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.ui.DialogBuilder
import java.io.File

class JsonToDartAction : AnAction("Convert json to dart") {

    override fun actionPerformed(actionEvent: AnActionEvent?) {
        actionEvent?.let { event ->
            DialogBuilder().apply {
                val form = Json2DartForm()
                form.setOnGenerateListener { json, finalFields ->
                    ProgressManager.getInstance().run(
                        object : Task.Backgroundable(
                            event.project, "Dart file generating", false
                        ) {
                            override fun run(p0: ProgressIndicator) {
                                DartClassGenerator().generateFromJson(
                                    json,
                                    File(actionEvent.getData(CommonDataKeys.VIRTUAL_FILE)?.path),
                                    "Response",
                                        finalFields
                                )
                            }
                        }
                    )
                }
                setCenterPanel(form.rootView)
                setTitle("Json2Dart")
                removeAllActions()
                show()
            }
        }
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.isEnabledAndVisible = e.getData(CommonDataKeys.VIRTUAL_FILE)?.isDirectory ?: false
    }
}