package com.mobilegiants.megila

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import com.airbnb.lottie.LottieAnimationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mobilegiants.megila.databinding.ActivityQuizBinding
import com.mobilegiants.megila.managers.RemoteConfigManager
import kotlin.random.Random

class QuizActivity : AppCompatActivity() {
    // View binding
    private lateinit var binding: ActivityQuizBinding

    // Quiz state
    private var currentQuestionIndex = 0
    private var score = 0
    private var quizQuestions = listOf<QuizQuestion>()
    private val gson = Gson()
    private val questionsToShow = 10

    companion object {
        // Constants
        private const val REMOTE_CONFIG_QUIZ_KEY = "purim_quiz_questions"

        // Bundle keys
        private const val KEY_CURRENT_QUESTION = "current_question_index"
        private const val KEY_SCORE = "current_score"
        private const val KEY_QUIZ_QUESTIONS = "quiz_questions"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up app to support right-to-left text
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL

        // Initialize views and settings
        initTopBar()
        setupButtonListeners()

        // Set title for the quiz
        supportActionBar?.title = getString(R.string.quiz_title)

        // Restore state or initialize quiz
        if (savedInstanceState != null) {
            restoreState(savedInstanceState)
        } else {
            setupRemoteConfig()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_CURRENT_QUESTION, currentQuestionIndex)
        outState.putInt(KEY_SCORE, score)
        outState.putString(KEY_QUIZ_QUESTIONS, gson.toJson(quizQuestions))
    }

    private fun restoreState(savedInstanceState: Bundle) {
        currentQuestionIndex = savedInstanceState.getInt(KEY_CURRENT_QUESTION, 0)
        score = savedInstanceState.getInt(KEY_SCORE, 0)

        val questionsJson = savedInstanceState.getString(KEY_QUIZ_QUESTIONS)
        if (!questionsJson.isNullOrEmpty()) {
            val type = object : TypeToken<List<QuizQuestion>>() {}.type
            quizQuestions = gson.fromJson(questionsJson, type)

            if (currentQuestionIndex < quizQuestions.size) {
                displayQuestion()
            } else {
                showFinalScore()
            }
        } else {
            setupRemoteConfig()
        }
    }

    //region Initialization
    private fun initTopBar() {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = getColor(R.color.light_beige_quiz)
        }
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    private fun setupButtonListeners() {
        with(binding) {
            nextButton.setOnClickListener { handleNextButtonClick() }
            exitButton.setOnClickListener { finish() }
            exitXButton.setOnClickListener { finish() }
            retryButton.setOnClickListener { resetQuiz() }
        }
    }
    //endregion

    //region Quiz Logic
    private fun setupRemoteConfig() {
        showLoading(true)
        fetchRemoteConfig()
    }

    private fun fetchRemoteConfig() {
        RemoteConfigManager.getInstance().fetchRemoteConfigValues()
        if (parseQuizQuestions()) {
            showLoading(false)
            displayQuestion()
        } else {
            showError(getString(R.string.quiz_fetch_error_message))
            finish()
        }
    }

    private fun parseQuizQuestions(): Boolean {
        return try {
            val quizJson = RemoteConfigManager.getInstance().getParameter(REMOTE_CONFIG_QUIZ_KEY)
            val type = object : TypeToken<List<QuizQuestion>>() {}.type
            val allQuestions: List<QuizQuestion> = gson.fromJson(quizJson, type)

            // Shuffle and limit questions
            quizQuestions = allQuestions.shuffled(Random).take(questionsToShow)
            quizQuestions.isNotEmpty()
        } catch (e: Exception) {
            showError(getString(R.string.quiz_parse_error_message))
            false
        }
    }

    private fun showLoading(show: Boolean) {
        with(binding) {
            loadingView.visibility = if (show) View.VISIBLE else View.GONE
            quizContent.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun handleNextButtonClick() {
        // Check if an option is selected
        if (binding.radioGroup.checkedRadioButtonId == -1) {
            showError(getString(R.string.quiz_no_answer_selected_error_message))
            return
        }

        // Process the answer
        processAnswer()

        // Move to next question
        advanceToNextQuestion()
    }

    private fun processAnswer() {
        val selectedOptionIndex = getSelectedOptionIndex()
        if (selectedOptionIndex == -1) return

        val currentQuestion = quizQuestions[currentQuestionIndex]
        val selectedAnswer = currentQuestion.options[selectedOptionIndex]

        if (selectedAnswer == currentQuestion.correctAnswer) {
            handleCorrectAnswer()
        } else {
            handleWrongAnswer()
        }
    }

    private fun getSelectedOptionIndex(): Int {
        return when (binding.radioGroup.checkedRadioButtonId) {
            R.id.optionA -> 0
            R.id.optionB -> 1
            R.id.optionC -> 2
            R.id.optionD -> 3
            else -> -1
        }
    }

    private fun handleCorrectAnswer() {
        score++
        updateScoreDisplay()
        animateCorrectAnswer()
    }

    private fun handleWrongAnswer() {
        animateWrongAnswer()
    }

    private fun updateScoreDisplay() {
        binding.scoreTextView.text = buildString {
            append(getString(R.string.quiz_score_title))
            append(score)
            append("/")
            append(quizQuestions.size)
        }
    }

    private fun advanceToNextQuestion() {
        currentQuestionIndex++
        if (currentQuestionIndex < quizQuestions.size) {
            animateQuestionTransition { displayQuestion() }
        } else {
            showFinalScore()
        }
    }

    private fun displayQuestion() {
        val question = quizQuestions[currentQuestionIndex]

        with(binding) {
            // Set question text
            questionTextView.text = question.questionText

            // Shuffle options for randomness
            val shuffledOptions = question.options.shuffled().toMutableList()
            question.options = shuffledOptions

            // Set option texts
            optionA.text = question.options[0]
            optionB.text = question.options[1]
            optionC.text = question.options[2]
            optionD.text = question.options[3]

            // Clear previous selection
            radioGroup.clearCheck()
        }

        // Update score display
        updateScoreDisplay()
    }

    private fun resetQuiz() {
        // Reset quiz state
        currentQuestionIndex = 0
        score = 0

        // Reset UI
        with(binding) {
            questionCardView.visibility = View.VISIBLE
            nextButton.visibility = View.VISIBLE
            finalScoreCard.visibility = View.GONE
        }

        // Reload questions
        setupRemoteConfig()
    }
    //endregion

    //region Animations
    private fun animateQuestionTransition(onAnimationEnd: () -> Unit) {
        // Slide out animation
        val slideOut =
            ObjectAnimator.ofFloat(binding.questionCardView, View.TRANSLATION_X, 0f, -1000f).apply {
                duration = 300
                interpolator = AccelerateDecelerateInterpolator()
            }

        // Slide in animation
        val slideIn =
            ObjectAnimator.ofFloat(binding.questionCardView, View.TRANSLATION_X, 1000f, 0f).apply {
                duration = 300
                interpolator = AccelerateDecelerateInterpolator()
            }

        slideOut.doOnEnd {
            onAnimationEnd()
            slideIn.start()
        }

        slideOut.start()
    }

    private fun animateCorrectAnswer() {
        // Scale animation for correct answer
        val scaleX = ObjectAnimator.ofFloat(binding.questionCardView, View.SCALE_X, 1f, 1.05f, 1f)
        val scaleY = ObjectAnimator.ofFloat(binding.questionCardView, View.SCALE_Y, 1f, 1.05f, 1f)

        AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            duration = 300
            start()
        }

        playLottieAnimation(binding.correctAnimationView, "correct_answer.json")
    }

    private fun animateWrongAnswer() {
        // Shake animation for wrong answer
        ObjectAnimator.ofFloat(
            binding.questionCardView,
            View.TRANSLATION_X,
            0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f
        ).apply {
            duration = 500
            start()
        }

        playLottieAnimation(binding.wrongAnimationView, "wrong_answer.json")
    }

    private fun playLottieAnimation(animationView: LottieAnimationView, animationFile: String) {
        animationView.apply {
            visibility = View.VISIBLE
            setAnimation(animationFile)
            playAnimation()
            addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator) {}
                override fun onAnimationEnd(p0: Animator) {
                    visibility = View.GONE
                }

                override fun onAnimationCancel(p0: Animator) {
                    visibility = View.GONE
                }

                override fun onAnimationRepeat(p0: Animator) {}
            })
        }
    }

    private fun showFinalScore() {
        // Hide quiz UI
        with(binding) {
            questionCardView.visibility = View.GONE
            nextButton.visibility = View.GONE

            // Calculate score percentage
            val percentage = (score.toFloat() / quizQuestions.size) * 100

            // Set final score message
            finalScoreText.text = buildString {
                append(getString(R.string.quiz_your_final_score_message_part_1))
                append(score)
                append(getString(R.string.quiz_your_final_score_message_part_2))
                append(quizQuestions.size)
                append("\n(")
                append(percentage.toInt())
                append("%)")
            }
        }

        // Show and animate final score card
        showFinalScoreAnimation()
    }

    private fun showFinalScoreAnimation() {
        binding.finalScoreCard.apply {
            visibility = View.VISIBLE
            alpha = 0f
            scaleX = 0.8f
            scaleY = 0.8f
        }

        val fadeIn = ObjectAnimator.ofFloat(binding.finalScoreCard, View.ALPHA, 0f, 1f)
        val scaleUpX = ObjectAnimator.ofFloat(binding.finalScoreCard, View.SCALE_X, 0.8f, 1f)
        val scaleUpY = ObjectAnimator.ofFloat(binding.finalScoreCard, View.SCALE_Y, 0.8f, 1f)

        AnimatorSet().apply {
            playTogether(fadeIn, scaleUpX, scaleUpY)
            duration = 500
            start()
        }
    }
    //endregion
}

// Data class for quiz questions - must match the JSON structure
data class QuizQuestion(
    val questionText: String,
    var options: MutableList<String>,
    val correctAnswer: String
)