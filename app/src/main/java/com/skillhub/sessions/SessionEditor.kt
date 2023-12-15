package com.skillhub.sessions

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.skillhub.R
import com.skillhub.configs.ConfigEditor
import com.skillhub.database.Database
import com.skillhub.database.QueryHelper
import com.skillhub.globalModules
import com.skillhub.configs.ModuleConfig
import com.skillhub.configs.ModuleConfigAdapter
import com.skillhub.modules.Module


class SessionEditor : AppCompatActivity() {
    private lateinit var database: Database
    private lateinit var session: Session
    private var newSession = false

    private var sessionName = ""
    private var penalty = 0
    private var target = 0
    private var reset = false
    private var configs = mutableListOf<ModuleConfig>()

    private lateinit var sessionNameInput: EditText
    private lateinit var penaltyInput: EditText
    private lateinit var targetInput: EditText
    private lateinit var resetInput: CheckBox
    private lateinit var configList: ExpandableListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_session_config_select)
        database = Database(QueryHelper(applicationContext))

        if (intent.hasExtra(Database.sessionID)) {
            val sessionID = intent.getIntExtra(Database.sessionID, -1)
            session = database.loadSession(sessionID)
        } else {
            val sessionID = database.makeNewSessionID()
            session = Session(sessionID, database)
            newSession = true
        }

        penalty = session.getPointPenalty()
        target = session.getTargetPoints()
        reset = session.getReset()

        configList = findViewById(R.id.session_config_select_list_config)
        sessionNameInput = findViewById(R.id.session_config_select_input_name)

        sessionNameInput.setText(session.getName())

        configs = session.getConfigs()

        val allConfigs: MutableMap<Module, MutableList<ModuleConfig>> = mutableMapOf()
        val selectedConfigs: MutableMap<Module, MutableList<ModuleConfig>> = mutableMapOf()
        for (module in globalModules.values) {
            allConfigs[module] = database.loadConfigs(module.getModuleID())
            selectedConfigs[module] = mutableListOf()
        }
        for (config in configs) {
            selectedConfigs[config.getModule()]!!.add(config)
        }

        val adapter = ModuleConfigAdapter(this,
            allConfigs,
            selectedConfigs,
            { checkBox: CheckBox, config: ModuleConfig -> onConfigClicked(checkBox, config) },
            { checkBox: CheckBox, config: ModuleConfig -> onConfigHeld(checkBox, config) })

        configList.setAdapter(adapter)

        val next = findViewById<Button>(R.id.session_config_select_button_next)
        next.setOnClickListener { onNext() }
    }

    private fun onConfigClicked(checkBox: CheckBox, config: ModuleConfig) {
        if (checkBox.isChecked) {
            configs.add(config)
        } else {
            for (i in 0 until configs.size) {
                if (configs[i].getConfigID() == config.getConfigID()) {
                    configs.removeAt(i)
                    break
                }
            }
        }
    }

    private fun onConfigHeld(checkBox: CheckBox, config: ModuleConfig): Boolean {
        val intent = Intent(this, ConfigEditor::class.java)
        intent.putExtra(Database.configID, config.getConfigID())
        startActivity(intent)
        return true
    }

    private fun onBack() {
        sessionName = sessionNameInput.text.toString()
        if (penaltyInput.text.toString() == "" || targetInput.text.toString() == "") {
            return
        }
        penalty = penaltyInput.text.toString().toInt()
        target = targetInput.text.toString().toInt()
        reset = resetInput.isChecked

        setContentView(R.layout.activity_session_config_select)
        configList = findViewById(R.id.session_config_select_list_config)
        sessionNameInput = findViewById(R.id.session_config_select_input_name)

        sessionNameInput.setText(sessionName)
        configs = session.getConfigs()

        val allConfigs: MutableMap<Module, MutableList<ModuleConfig>> = mutableMapOf()
        val selectedConfigs: MutableMap<Module, MutableList<ModuleConfig>> = mutableMapOf()
        for (module in globalModules.values) {
            allConfigs[module] = database.loadConfigs(module.getModuleID())
            selectedConfigs[module] = mutableListOf()
        }
        for (config in configs) {
            selectedConfigs[config.getModule()]!!.add(config)
        }

        val adapter = ModuleConfigAdapter(this,
            allConfigs,
            selectedConfigs,
            { checkBox, config -> onConfigClicked(checkBox, config) },
            { checkBox, config -> onConfigHeld(checkBox, config) })
        configList.setAdapter(adapter)

        val next = findViewById<Button>(R.id.session_config_select_button_next)
        next.setOnClickListener { onNext() }
    }

    private fun onNext() {
        if (configs.size == 0) {
            return
        }
        sessionName = sessionNameInput.text.toString()

        setContentView(R.layout.activity_session_settings)
        sessionNameInput = findViewById(R.id.session_settings_input_name)
        penaltyInput = findViewById(R.id.session_settings_input_penalty)
        targetInput = findViewById(R.id.session_settings_input_target)
        resetInput = findViewById(R.id.session_settings_checkbox_reset)
        resetInput.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                penaltyInput.isEnabled = false
                findViewById<TextView>(R.id.session_settings_text_penalty).isEnabled = false
            } else {
                penaltyInput.isEnabled = true
                findViewById<TextView>(R.id.session_settings_text_penalty).isEnabled = true
            }
        }

        sessionNameInput.setText(sessionName)
        penaltyInput.setText(penalty.toString())
        targetInput.setText(target.toString())
        resetInput.isChecked = reset

        val back = findViewById<Button>(R.id.session_settings_button_back)
        back.setOnClickListener { onBack() }
        val done = findViewById<Button>(R.id.session_settings_button_done)
        done.setOnClickListener { onDone() }
    }

    private fun onDone() {
        if (penaltyInput.text.toString() == "" || targetInput.text.toString() == "") {
            return
        }
        sessionName = sessionNameInput.text.toString()
        penalty = penaltyInput.text.toString().toInt()
        target = targetInput.text.toString().toInt()
        reset = resetInput.isChecked
        if (sessionName == "" || configs.size == 0) {
            return
        }

        if (targetInput.text.toString().toInt() < 1) {
            return
        }

        if (penaltyInput.text.toString().toInt() < 0) {
            return
        }

        if (targetInput.text.toString().toInt() <= penaltyInput.text.toString().toInt()) {
            return
        }

        if (targetInput.text.toString().toInt() <= session.getPoints()) {
            return
        }

        if (newSession) {
            database.saveSession(session)
        }

        session.setName(sessionNameInput.text.toString())
        session.setPointPenalty(penaltyInput.text.toString().toInt())
        session.setTargetPoints(targetInput.text.toString().toInt())
        session.setReset(resetInput.isChecked)
        session.setConfigs(configs)

        if (!newSession) {
            val intent = Intent(this, SessionViewer::class.java)
            intent.putExtra(Database.sessionID, session.getSessionID())
            startActivity(intent)
        }
        finish()
    }
}
