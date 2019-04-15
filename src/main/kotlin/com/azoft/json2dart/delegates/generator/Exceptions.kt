package com.azoft.json2dart.delegates.generator

import java.io.IOException

class SyntaxException: Exception("Wrong json syntax")

class FileIOException: IOException("Cannot read or write file")

class NotAFlutterProject: Exception("Oooops! Looks like plugin cannot find 'lib' folder. This is a flutter project, isn't it?")