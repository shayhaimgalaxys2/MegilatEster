package com.mobilegiants.megila.v2.viewmodels

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mobilegiants.megila.v2.data.QuizQuestion
import com.mobilegiants.megila.v2.managers.RemoteConfigManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class QuizViewModel : ViewModel() {

    data class UiState(
        val questions: List<QuizQuestion> = emptyList(),
        val currentQuestionIndex: Int = 0,
        val score: Int = 0,
        val isLoading: Boolean = true,
        val error: String? = null,
        val isFinished: Boolean = false
    ) {
        val currentQuestion: QuizQuestion? get() = questions.getOrNull(currentQuestionIndex)
        val totalQuestions: Int get() = questions.size
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val gson = Gson()

    companion object {
        private const val REMOTE_CONFIG_QUIZ_KEY = "purim_quiz_questions"
        private const val QUESTIONS_TO_SHOW = 10
    }

    fun loadQuestions() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        RemoteConfigManager.fetchRemoteConfigValues()

        try {
            val quizJson = RemoteConfigManager.getParameter(REMOTE_CONFIG_QUIZ_KEY)
            val type = object : TypeToken<List<QuizQuestion>>() {}.type
            val allQuestions: List<QuizQuestion> = gson.fromJson(quizJson, type)
            val selected = allQuestions.shuffled().take(QUESTIONS_TO_SHOW)

            if (selected.isEmpty()) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "No questions available")
                return
            }

            _uiState.value = UiState(
                questions = selected,
                isLoading = false
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
        }
    }

    fun submitAnswer(selectedOptionIndex: Int): Boolean {
        val state = _uiState.value
        val question = state.currentQuestion ?: return false
        val isCorrect = question.options[selectedOptionIndex] == question.correctAnswer

        if (isCorrect) {
            _uiState.value = state.copy(score = state.score + 1)
        }
        return isCorrect
    }

    fun advanceToNextQuestion() {
        val state = _uiState.value
        val nextIndex = state.currentQuestionIndex + 1
        if (nextIndex >= state.questions.size) {
            _uiState.value = state.copy(isFinished = true)
        } else {
            _uiState.value = state.copy(currentQuestionIndex = nextIndex)
        }
    }

    fun resetQuiz() {
        _uiState.value = UiState()
        loadQuestions()
    }

    fun restoreState(questionIndex: Int, score: Int, questionsJson: String) {
        try {
            val type = object : TypeToken<List<QuizQuestion>>() {}.type
            val questions: List<QuizQuestion> = gson.fromJson(questionsJson, type)
            _uiState.value = UiState(
                questions = questions,
                currentQuestionIndex = questionIndex,
                score = score,
                isLoading = false,
                isFinished = questionIndex >= questions.size
            )
        } catch (e: Exception) {
            loadQuestions()
        }
    }

    fun getQuestionsJson(): String = gson.toJson(_uiState.value.questions)
}
