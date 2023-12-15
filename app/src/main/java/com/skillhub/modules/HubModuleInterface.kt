package com.skillhub.modules

import com.skillhub.sessions.Session

abstract class HubModuleInterface(
    private val moduleID: Int,
) {
    fun getModuleID(): Int {
        return moduleID
    }

    abstract fun getNextModule(session: Session): Module
}