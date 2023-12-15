package com.skillhub.modules.MathModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.skillhub.R
import com.skillhub.configs.ModuleConfig
import com.skillhub.modules.*
import com.skillhub.sessions.Session

class MathModule(moduleID: Int, stub: ModuleStub) : Module(moduleID,
    stub,
    { module, session, question, answers, taskID, config, attempt -> MathTask(module, session, question, answers, taskID, config, attempt) },
    { attempt, id, userAnswer, judgement -> MathAttempt(attempt, id, userAnswer, judgement) },
    { text -> MathQuestion(text) },
    { id, text -> MathAnswer(id, text.toInt()) },
    { attempt, loadedUserAnswer -> MathUserAnswer(attempt, loadedUserAnswer) },
    { attempt, loadedJudgement -> MathJudgment(attempt, loadedJudgement) },
    { task -> MathFragment(task) }) {

    override fun makeTask(taskID: Int, config: ModuleConfig, session: Session): ModuleTask {
        val maxVal = config.getConfigData("Max value")!!.getValue() as Int
        val doNeg = config.getConfigData("Do negation")!!.getValue()
        val rand1 = (0..maxVal).random()
        val rand2 = (0..maxVal).random()
        var neg = (0..1).random()
        if (doNeg == false) {
            neg = 0
        }
        val question: String = if (neg == 1) {
            "$rand1-$rand2="
        } else {
            "$rand1+$rand2="
        }
        val answer: Int = if (neg == 1) {
            rand1 - rand2
        } else {
            rand1 + rand2
        }
        return MathTask(this, session, MathQuestion(question), listOf(MathAnswer(0, answer)), taskID, config)
    }

    override fun getAllSkills(): MutableMap<String, String> {
        return mutableMapOf()
    }
}

class MathTask(
    module: Module,
    session: Session,
    question: TaskQuestion,
    answers: List<TaskAnswer>,
    taskID: Int,
    config: ModuleConfig,
    loadedAttempt: Triple<Int, Any, Boolean>? = null
) : ModuleTask(module, session, question, answers, taskID, config, loadedAttempt)

class MathAttempt(
    task: ModuleTask, loadedAttemptID: Int? = null, loadedUserAnswer: Any? = null, loadedJudgment: Boolean? = null
) : TaskAttempt(task, loadedAttemptID, loadedUserAnswer, loadedJudgment)

class MathQuestion(question: String) : TaskQuestion(question)

class MathAnswer(answerID: Int, answer: Int) : TaskAnswer(answerID, answer.toString())

class MathUserAnswer(attempt: TaskAttempt, loadedUserAnswer: Any?) : TaskUserAnswer(attempt, loadedUserAnswer)

class MathJudgment(attempt: TaskAttempt, loadedJudgment: Boolean?) : TaskJudgment(attempt, loadedJudgment) {
    override fun checkAnswer() {
        judgement = attempt.userAnswer.getUserAnswer().toString().toInt() == attempt.task().getAnswers()[0].getAnswer().toInt()
    }
}

class MathFragment(task: ModuleTask) : TaskFragment(task) {
    private lateinit var view: View
    private lateinit var answerInput: EditText
    private lateinit var answerPreview: TextView
    private lateinit var questionView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.module_math, container, false)

        answerPreview = view.findViewById(R.id.module_math_text_preview) as TextView
        questionView = view.findViewById(R.id.module_math_text_question) as TextView
        answerInput = view.findViewById(R.id.module_math_input_answer) as EditText
        answerInput.addTextChangedListener { text ->
            if (text.toString() == "" || text.toString() == "-") {
                getTask().getCurrentAttempt().userAnswer.setUserAnswer(null)
                return@addTextChangedListener
            }
            getTask().getCurrentAttempt().userAnswer.setUserAnswer(text.toString().toInt())
        }

        updateUI()

        return view
    }

    override fun updateUI() {
        questionView.text = getTask().getQuestion().getQuestion().toString()
        val userAnswer = getTask().getCurrentAttempt().userAnswer.getUserAnswer()
        if (userAnswer != null) {
            answerInput.setText(userAnswer.toString())
            answerPreview.text = userAnswer.toString()
        }

        if (getTask().getState() == TaskState.AWAITING) {
            answerInput.visibility = View.VISIBLE
            answerPreview.visibility = View.INVISIBLE
        } else if (getTask().getState() == TaskState.LOCKED) {
            answerInput.visibility = View.INVISIBLE
            answerPreview.visibility = View.VISIBLE
        }
    }
}

class MathModuleStub : ModuleStub() {
    override val descriptionName: String = "Math Module"
    override val databaseName: String = "Math"
    override val moduleDirectory: String = "MathModule"

    override fun createModule(moduleID: Int): Module {
        return MathModule(moduleID, this)
    }
}
