package com.azoft.json2dart.generator

data class NodeInfo(
    val stringRepresentation: String,
    val node: NodeWrapper?,
    val mapExtraction: String?
) {
    constructor(stringRepresentation: String): this(stringRepresentation, null, null)
}