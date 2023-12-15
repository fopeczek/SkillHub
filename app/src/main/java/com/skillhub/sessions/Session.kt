package com.skillhub.sessions

import com.skillhub.configs.ModuleConfig
import com.skillhub.database.Database
import com.skillhub.modules.ModuleTask

class Session( //Here are the defaults for session creation
    private val sessionID: Int,
    private val database: Database,
    private var configs: MutableList<ModuleConfig> = mutableListOf(),
    private var tasks: MutableList<ModuleTask> = mutableListOf(),
    private var name: String = "Session $sessionID",
    private var reset: Boolean = false,
    private var pointPenalty: Int = 2,
    private var targetPoints: Int = 10,
    private var answeredTaskAmount: Int = 0,
    private var points: Int = 0
) {
    fun answerTask() {
        answeredTaskAmount++
        database.updateSession(this)
    }

    fun isFinished(): Boolean {
        return points >= targetPoints
    }

    fun getConfigs(): MutableList<ModuleConfig> {
        return configs
    }

    fun setConfigs(configs: MutableList<ModuleConfig>) {
        this.configs = configs
        database.updateSession(this)
    }

    fun getTasks(): MutableList<ModuleTask> {
        return tasks
    }

    fun setTasks(tasks: MutableList<ModuleTask>) {
        this.tasks = tasks
        database.updateSession(this)
    }

    fun addTask(task: ModuleTask) {
        tasks.add(task)
    }

    fun getSessionID(): Int {
        return sessionID
    }

    fun getName(): String {
        return name
    }

    fun setName(name: String) {
        this.name = name
        database.updateSession(this)
    }

    fun getReset(): Boolean {
        return reset
    }

    fun setReset(reset: Boolean) {
        this.reset = reset
        database.updateSession(this)
    }

    fun getPointPenalty(): Int {
        return pointPenalty
    }

    fun setPointPenalty(pointPenalty: Int) {
        this.pointPenalty = pointPenalty
        database.updateSession(this)
    }

    fun getTargetPoints(): Int {
        return targetPoints
    }

    fun setTargetPoints(targetPoints: Int) {
        this.targetPoints = targetPoints
        database.updateSession(this)
    }

    fun getAnsweredTaskAmount(): Int {
        return answeredTaskAmount
    }

    fun getPoints(): Int {
        return points
    }

    fun setPoints(points: Int) {
        this.points = points
        if (this.points < 0) {
            this.points = 0
        }
        database.updateSession(this)
    }

    fun remove(){
        database.removeSession(this)
    }
}
