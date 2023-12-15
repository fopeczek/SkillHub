package com.skillhub.configs

import com.skillhub.database.Database
import com.skillhub.modules.Module

class ModuleConfig(
    private val configID: Int,
    private val module: Module,
    private val database: Database,
    private var name: String = "Config $configID",
    private var configData: MutableList<ConfigData> = mutableListOf()
) {
    fun getConfigID(): Int {
        return configID
    }

    fun getModule(): Module {
        return module
    }

    fun getName(): String {
        return name
    }

    fun setName(name: String) {
        this.name = name
        database.updateConfig(this)
    }

    fun getConfigData(): MutableList<ConfigData> {
        return configData
    }

    fun getConfigData(name: String): ConfigData? {
        for (data in configData) {
            if (data.getName() == name) {
                return data
            }
        }
        return null
    }

    fun addConfigData(configData: ConfigData) {
        this.configData.add(configData)
        database.updateConfig(this)
    }

    fun setConfigData(configData: MutableList<ConfigData>) {
        this.configData = configData
        database.updateConfig(this)
    }

    fun contains(subConfig: ModuleConfig): Boolean {
        return this.configData.containsAll(subConfig.configData)
    }
}

class ConfigData(
    private val configDataID: Int,
    private val configID: Int,
    private val name: String,
    private val type: String,
    private val value: Any
) {
    fun getConfigDataID(): Int {
        return configDataID
    }

    fun getConfigID(): Int {
        return configID
    }

    fun getName(): String {
        return name
    }

    fun getType(): String {
        return type
    }

    fun getValue(): Any {
        return value
    }
}