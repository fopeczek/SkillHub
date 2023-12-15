package com.skillhub.modules.HubModule

import com.skillhub.modules.HubModuleInterface
import com.skillhub.modules.Module
import com.skillhub.sessions.Session

class HubModule(moduleID: Int) : HubModuleInterface(moduleID) {
    override fun getNextModule(session: Session): Module {
        val configs = session.getConfigs()
        val rand = (0 until configs.size).random()
        return configs[rand].getModule()
    }
}