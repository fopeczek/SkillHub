package com.skillhub.configs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.skillhub.R
import com.skillhub.database.Database
import com.skillhub.database.QueryHelper
import com.skillhub.globalModules

class ConfigEditor : AppCompatActivity() {
    private lateinit var database: Database
    private lateinit var config: ModuleConfig
    private var newConfig = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_editor)

        database = Database(QueryHelper(applicationContext))

        if (intent.hasExtra(Database.configID)) {

            val configId = intent.getIntExtra(Database.configID, -1)
            config = database.loadConfig(configId)
        } else {
            if (intent.hasExtra(Database.moduleID)) {
                val configId = database.makeNewConfigID()
                val moduleID = intent.getIntExtra(Database.moduleID, -1)
                val module = globalModules[moduleID]!!
                config = ModuleConfig(configId, module, database)
                newConfig = true
            } else {
                finish()
            }
        }

        val done = findViewById<Button>(R.id.config_editor_button_done)
        done.setOnClickListener { onDone() }
    }

    private fun onDone(){
        finish()
    }
}