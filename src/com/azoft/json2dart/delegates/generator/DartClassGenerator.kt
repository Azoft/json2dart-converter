package com.azoft.json2dart.delegates.generator

import com.azoft.json2dart.delegates.generator.data.NodeInfo
import com.azoft.json2dart.delegates.generator.data.NodeWrapper
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.*
import java.util.*


class DartClassGenerator {

    fun generateFromJson(source: String, destiny: File, rootName: String, isFinal: Boolean) {
        val nodesToProcessStack = Stack<NodeWrapper>()

        try {
            nodesToProcessStack.add(
                NodeWrapper(
                    node = jacksonObjectMapper().readTree(source),
                    fieldName = rootName,
                    sneakCaseName = rootName,
                    className = extractRootClassName(rootName)
                )
            )
        } catch (e: Exception) {
            throw SyntaxException()
        }

        val packageTemplate = extractPackageName(destiny)
        val finalMode = if (isFinal) "final " else ""
        var nodeWrapper: NodeWrapper
        var buffer: FileOutputStream
        var target: FileOutputStream
        var constructorStringBuilder: StringBuilder
        val importsList = mutableListOf<String>()
        var valueFromMap: String
        var bufferFile: File

        while (nodesToProcessStack.isNotEmpty()) {
            nodeWrapper = nodesToProcessStack.pop()
            bufferFile = File(destiny, "__${nodeWrapper.sneakCaseName}.dart")
            buffer = FileOutputStream(bufferFile)
            target = FileOutputStream(File(destiny, "${nodeWrapper.sneakCaseName}.dart"))
            constructorStringBuilder = createConstructorStart(nodeWrapper)

            buffer.writeText("\nclass ${nodeWrapper.className} {\n\n")
            try {
                nodeWrapper.node?.fields()?.forEach {
                    valueFromMap = "map[\"${it.key}\"],\n"
                    processNode(buffer, it.value, it.key, finalMode)?.let { nodeInfo ->
                        nodeInfo.node?.apply {
                            nodesToProcessStack.add(this)
                            target.writeText("import '$packageTemplate$sneakCaseName.dart';\n")
                        }

                        nodeInfo.mapExtraction?.let { extraction ->
                            valueFromMap = extraction
                        }
                    }
                    constructorStringBuilder.append("    ${it.key} = $valueFromMap")
                }
                constructorStringBuilder.apply {
                    deleteCharAt(length - 1).deleteCharAt(length - 1).append(";\n")
                }
                buffer.writeText(constructorStringBuilder.toString()).writeText("\n}")
                buffer.close()

                mergeBufferAndTarget(target, bufferFile)
            } finally {
                buffer.close()
                target.close()
            }
            importsList.clear()
        }
    }

    private fun processNode(
        fout: FileOutputStream, node: JsonNode, name: String, finalMode: String
    ): NodeInfo? {
        val nodeInfo = extractNodeInfo(node, name)
        fout.writeText("  $finalMode${nodeInfo.stringRepresentation} $name;\n")
        return nodeInfo
    }

    private fun extractNodeInfo(node: JsonNode, name: String): NodeInfo {
        return when {
            node.isDouble || node.isFloat || node.isBigDecimal ->
                NodeInfo("double")

            node.isShort || node.isInt || node.isLong || node.isBigInteger ->
                NodeInfo("int")

            node.isBoolean ->
                NodeInfo("bool")

            node.isTextual ->
                NodeInfo("String")

            node.isArray ->
                extractArrayData(node as ArrayNode, name)

            node.isObject ->
                NodeWrapper(node, name).let {
                    NodeInfo(it.className, it, "${it.className}.fromJsonMap(map[\"${it.fieldName}\"]),\n")
                }

            else -> NodeInfo("Object")
        }
    }

    private fun extractArrayData(node: ArrayNode, name: String): NodeInfo {
        val iterator = node.iterator()
        if (!iterator.hasNext()) {
            return NodeInfo("List<Object>")
        }
        val elementInfo = extractNodeInfo(iterator.next(), name)
        return NodeInfo(
            "List<${elementInfo.stringRepresentation}>",
            elementInfo.node,
            if (elementInfo.node != null) {
                "List<${elementInfo.node.className}>.from(map[\"${elementInfo.node.fieldName}\"].map((it) => ${elementInfo.node.className}.fromJsonMap(it))),\n"
            } else {
                "List<${elementInfo.stringRepresentation}>.from(map[\"$name\"]),\n"
            }
        )
    }

    private fun createConstructorStart(nodeWrapper: NodeWrapper) =
        StringBuilder()
            .append("\n  ${nodeWrapper.className}.fromJsonMap(Map<String, dynamic> map): \n")

    private fun mergeBufferAndTarget(targetStream: FileOutputStream, bufferFile: File) {
        BufferedReader(FileReader(bufferFile)).useLines { lines ->
            lines.forEach {
                targetStream.writeText(it).writeText("\n")
            }
        }

        bufferFile.delete()
    }

    private fun extractPackageName(dir: File): String {
        val absolutePath = dir.absolutePath
        val splitted = absolutePath.split(if (isWindows()) "\\" else "/")
        val libIndex = splitted.indexOf("lib")
        if (libIndex == -1) {
            throw NotAFlutterProject()
        }
        val fold = splitted
            .subList(libIndex + 1, splitted.size)
            .fold(StringBuilder()) { builder, s -> builder.append(s).append("/") }
        return "package:${splitted[libIndex - 1]}/$fold"
    }

    private fun FileOutputStream.writeText(text: String): FileOutputStream {
        write(text.toByteArray(Charsets.UTF_8))
        return this
    }

    private fun extractRootClassName(rootFileName: String): String {
        var needUp = true
        val builder = StringBuilder()
        val i = rootFileName.iterator()
        var element: Char

        while (i.hasNext()) {
            element = i.nextChar()
            if (element == '_') {
                needUp = true
                continue
            }
            if (needUp) {
                element = element.toUpperCase()
                needUp = false
            }

            builder.append(element)
        }
        return builder.toString()
    }
}