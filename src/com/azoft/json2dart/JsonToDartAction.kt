package com.azoft.json2dart


import com.azoft.json2dart.generator.DartClassGenerator
import com.azoft.json2dart.view.Json2DartForm
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
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
                form.setOnGenerateListener { fileName, json, finalFields ->
                    window.dispose()
                    ProgressManager.getInstance().run(
                        object : Task.Backgroundable(
                            event.project, "Dart file generating", false
                        ) {
                            override fun run(indicator: ProgressIndicator) {
                                try {
                                    DartClassGenerator().generateFromJson(
                                        json,
                                        File(actionEvent.getData(CommonDataKeys.VIRTUAL_FILE)?.path),
                                        fileName.takeIf { it.isNotBlank() } ?: "response",
                                        finalFields
                                    )
                                } finally {
                                    indicator.stop()
                                    ProjectView.getInstance(event.project).refresh()
                                    event.getData(LangDataKeys.VIRTUAL_FILE)?.refresh(false, true)
                                }
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