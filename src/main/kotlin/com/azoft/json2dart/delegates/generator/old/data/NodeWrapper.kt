package com.azoft.json2dart.delegates.generator.old.data

import com.azoft.json2dart.delegates.generator.toClassName
import com.azoft.json2dart.delegates.generator.toSneakCase
import com.fasterxml.jackson.databind.JsonNode

data class NodeWrapper(
    val node: JsonNode?,
    val fieldName: String,
    val sneakCaseName: String = toSneakCase(fieldName),
    val className: String = toClassName(fieldName)
)