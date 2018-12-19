package com.azoft.json2dart.generator

import com.fasterxml.jackson.databind.JsonNode

data class NodeWrapper(
    val node: JsonNode?,
    val fieldName: String,
    val sneakCaseName: String = toSneakCase(fieldName),
    val className: String = toClassName(fieldName)
)