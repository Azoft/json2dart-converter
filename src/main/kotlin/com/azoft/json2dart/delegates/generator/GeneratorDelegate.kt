package com.azoft.json2dart.delegates.generator

import com.azoft.json2dart.delegates.MessageDelegate
import com.azoft.json2dart.delegates.ui.UIDelegate
import com.azoft.json2dart.delegates.generator.old.DartClassGenerator
import com.azoft.json2dart.delegates.ui.IntellijUIDelegate
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import java.io.File
import java.io.IOException

class GeneratorDelegate(
    private val messageDelegate: MessageDelegate = MessageDelegate(),
    private val uiDelegate: UIDelegate = IntellijUIDelegate()
) {

    fun runGeneration(event: AnActionEvent, fileName: String, json: String, finalFields: Boolean) {
        ProgressManager.getInstance().run(
            object : Task.Backgroundable(
                event.project, "Dart file generating", false
            ) {
                override fun run(indicator: ProgressIndicator) {
                    try {
                        DartClassGenerator().generateFromJson(
                            json,
                            File(event.getData(CommonDataKeys.VIRTUAL_FILE)?.path!!),
                            fileName.takeIf(String::isNotBlank) ?: "response",
                            finalFields
                        )
                        messageDelegate.sendNotification("Dart class has been generated")
                    } catch (e: Throwable) {
                        when(e) {
                            is IOException -> messageDelegate.onException(FileIOException())
                            else -> messageDelegate.onException(e)
                        }
                    } finally {
                        indicator.stop()
                        ProjectView.getInstance(event.project).refresh()
                        event.getData(LangDataKeys.VIRTUAL_FILE)?.refresh(false, true)
                    }
                }
            }
        )
    }
}