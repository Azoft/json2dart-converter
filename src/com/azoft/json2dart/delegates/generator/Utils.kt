package com.azoft.json2dart.delegates.generator

fun toClassName(value: String) =
    value[0].toUpperCase() + value.slice(1 until value.length)


fun toSneakCase(value: String): String {
    return value.fold(StringBuilder()) { builder, c ->
        if (c.isUpperCase()) {
            builder.append("_").append(c.toLowerCase())
        } else {
            builder.append(c)
        }
    }.toString()
}