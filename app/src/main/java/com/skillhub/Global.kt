package com.skillhub

import android.icu.text.DateFormat
import com.skillhub.modules.Module
import java.util.*

val globalModules = mutableMapOf<Int, Module>() // ModuleID -> Module

var ranInit = false

const val databaseFilename = "database.db"

fun getModule(moduleName: String): Module {
    for (module in globalModules.values) {
        if (module.getStub().descriptionName == moduleName) {
            return module
        }
    }
    throw Exception("Module $moduleName not found")
}

fun getTimestamp(): String {
    val dateFormat = DateFormat.getDateTimeInstance()
    return dateFormat.format(Date())
}

enum class ColumnType {
    INT, TEXT, BOOL, FLOAT, TIMESTAMP, BLOB
}