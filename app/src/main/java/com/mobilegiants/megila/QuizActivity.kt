package com.mobilegiants.megila

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.animation.doOnEnd
import com.airbnb.lottie.LottieAnimationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mobilegiants.megila.managers.RemoteConfigManager
import kotlin.random.Random

class QuizActivity : AppCompatActivity() {
    private lateinit var questionTextView: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var optionA: RadioButton
    private lateinit var optionB: RadioButton
    private lateinit var optionC: RadioButton
    private lateinit var optionD: RadioButton
    private lateinit var nextButton: Button
    private lateinit var questionCardView: CardView
    private lateinit var scoreTextView: TextView
    private lateinit var loadingView: View
    private lateinit var quizContent: View
    private lateinit var correctAnswerAnimation: LottieAnimationView
    private lateinit var wrongAnswerAnimation: LottieAnimationView

    private var currentQuestionIndex = 0
    private var score = 0
    private var quizQuestions = listOf<QuizQuestion>()
    private val gson = Gson()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // Set up app to support right-to-left text
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL

        initViews()

        // Set title for the quiz
        supportActionBar?.title = "חידון לפורים"


        // Initialize Firebase Remote Config
        setupRemoteConfig()

        // Setup button click listener
        setupNextButton()
        initTopBar()
    }

    private fun initViews() {
        // Initialize views
        questionTextView = findViewById(R.id.questionTextView)
        radioGroup = findViewById(R.id.radioGroup)
        optionA = findViewById(R.id.optionA)
        optionB = findViewById(R.id.optionB)
        optionC = findViewById(R.id.optionC)
        optionD = findViewById(R.id.optionD)
        nextButton = findViewById(R.id.nextButton)
        questionCardView = findViewById(R.id.questionCardView)
        scoreTextView = findViewById(R.id.scoreTextView)
        loadingView = findViewById(R.id.loadingView)
        quizContent = findViewById(R.id.quizContent)
        correctAnswerAnimation = findViewById(R.id.correctAnimationView)
        wrongAnswerAnimation = findViewById(R.id.wrongAnimationView)
    }

    private fun initTopBar() {
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(R.color.light_beige_quiz)
        val decorView = getWindow().decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }


    private fun setupRemoteConfig() {
        // Show loading state
        showLoading(true)

        // Fetch remote config
        fetchRemoteConfig()
    }

    private fun fetchRemoteConfig() {
        RemoteConfigManager.getInstance().fetchRemoteConfigValues()
        parseQuizQuestions()
        // Hide loading, show quiz
        showLoading(false)

        // Display first question
        if (quizQuestions.isNotEmpty()) {
            displayQuestion()
        } else {
            Toast.makeText(this, "אירעה שגיאה בטעינת החידון", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun parseQuizQuestions() {
        try {
            val quizJson = RemoteConfigManager.getInstance().getParameter(Companion.REMOTE_CONFIG_QUIZ_KEY)
            val type = object : TypeToken<List<QuizQuestion>>() {}.type
            quizQuestions = gson.fromJson(quizJson, type)
            quizQuestions = quizQuestions.shuffled(Random)
            quizQuestions = quizQuestions.take(10)
        } catch (e: Exception) {
            Toast.makeText(this, "שגיאה בפענוח נתוני החידון", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(show: Boolean) {
        loadingView.visibility = if (show) View.VISIBLE else View.GONE
        quizContent.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun setupNextButton() {
        nextButton.setOnClickListener {
            // Check if an option is selected
            if (radioGroup.checkedRadioButtonId == -1) {
                Toast.makeText(this, "אנא בחר תשובה", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check answer
            val selectedOption = when (radioGroup.checkedRadioButtonId) {
                R.id.optionA -> 0
                R.id.optionB -> 1
                R.id.optionC -> 2
                R.id.optionD -> 3
                else -> -1
            }

            val selectedAnswer = quizQuestions[currentQuestionIndex].options[selectedOption]

            if (selectedAnswer == quizQuestions[currentQuestionIndex].correctAnswer) {
                score++
                scoreTextView.text = "ניקוד: $score/${quizQuestions.size}"
                animateCorrectAnswer()
            } else {
                animateWrongAnswer()
            }

            // Move to next question with animation
            currentQuestionIndex++
            if (currentQuestionIndex < quizQuestions.size) {
                animateQuestionTransition { displayQuestion() }
            } else {
                showFinalScore()
            }
        }

        val exitButton = findViewById<Button>(R.id.exitButton)
        val exitXButton = findViewById<ImageButton>(R.id.exitXButton)
        exitButton.setOnClickListener {
            finish()
        }
        exitXButton.setOnClickListener {
            finish()
        }
    }

    private fun displayQuestion() {
        val question = quizQuestions[currentQuestionIndex]
        questionTextView.text = question.questionText
        val mutableShuffledList =question.options.shuffled().toMutableList()
        question.options = mutableShuffledList
        optionA.text = question.options[0]
        optionB.text = question.options[1]
        optionC.text = question.options[2]
        optionD.text = question.options[3]

        // Clear previous selection
        radioGroup.clearCheck()

        // Update the score display
        scoreTextView.text = "ניקוד: $score/${quizQuestions.size}"
    }

    private fun animateQuestionTransition(onAnimationEnd: () -> Unit) {
        // Slide out animation
        val slideOut = ObjectAnimator.ofFloat(questionCardView, View.TRANSLATION_X, 0f, -1000f)
        slideOut.duration = 300
        slideOut.interpolator = AccelerateDecelerateInterpolator()

        // Slide in animation
        val slideIn = ObjectAnimator.ofFloat(questionCardView, View.TRANSLATION_X, 1000f, 0f)
        slideIn.duration = 300
        slideIn.interpolator = AccelerateDecelerateInterpolator()

        slideOut.doOnEnd {
            onAnimationEnd()
            slideIn.start()
        }

        slideOut.start()
    }

    private fun animateCorrectAnswer() {
        // Scale animation for correct answer
        val scaleX = ObjectAnimator.ofFloat(questionCardView, View.SCALE_X, 1f, 1.05f, 1f)
        val scaleY = ObjectAnimator.ofFloat(questionCardView, View.SCALE_Y, 1f, 1.05f, 1f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY)
        animatorSet.duration = 300
        animatorSet.start()
        correctAnswerAnimation.visibility = View.VISIBLE
        // 1. Set the animation JSON file
        correctAnswerAnimation.setAnimation("correct_answer.json") // Replace with your animation file name
        // 2. Start the animation
        correctAnswerAnimation.playAnimation()
        correctAnswerAnimation
        // 3. Add animation end listener
        correctAnswerAnimation.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
            }

            override fun onAnimationEnd(p0: Animator) {
                correctAnswerAnimation.visibility = View.GONE
            }

            override fun onAnimationCancel(p0: Animator) {
                correctAnswerAnimation.visibility = View.GONE
            }

            override fun onAnimationRepeat(p0: Animator) {
            }
        })
    }

    private fun animateWrongAnswer() {
        // Shake animation for wrong answer
        val shake = ObjectAnimator.ofFloat(
            questionCardView,
            View.TRANSLATION_X,
            0f,
            25f,
            -25f,
            25f,
            -25f,
            15f,
            -15f,
            6f,
            -6f,
            0f
        )
        shake.duration = 500
        shake.start()

        wrongAnswerAnimation.visibility = View.VISIBLE
        // 1. Set the animation JSON file
        wrongAnswerAnimation.setAnimation("wrong_answer.json") // Replace with your animation file name
        // 2. Start the animation
        wrongAnswerAnimation.playAnimation()
        // 3. Add animation end listener
        wrongAnswerAnimation.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
            }

            override fun onAnimationEnd(p0: Animator) {
                wrongAnswerAnimation.visibility = View.GONE
            }

            override fun onAnimationCancel(p0: Animator) {
                wrongAnswerAnimation.visibility = View.GONE
            }

            override fun onAnimationRepeat(p0: Animator) {
            }
        })
    }

    private fun showFinalScore() {
        // Hide the quiz UI
        questionCardView.visibility = View.GONE
        nextButton.visibility = View.GONE

        // Show a new card with final score
        val finalScoreCard = findViewById<CardView>(R.id.finalScoreCard)
        val finalScoreText = findViewById<TextView>(R.id.finalScoreText)
        val retryButton = findViewById<Button>(R.id.retryButton)

        // Calculate score percentage
        val percentage = (score.toFloat() / quizQuestions.size) * 100

        // Set final score message
        finalScoreText.text =
            "הניקוד הסופי שלך: $score מתוך ${quizQuestions.size}\n(${percentage.toInt()}%)"

        // Show final score card with animation
        finalScoreCard.visibility = View.VISIBLE
        finalScoreCard.alpha = 0f
        finalScoreCard.scaleX = 0.8f
        finalScoreCard.scaleY = 0.8f

        val fadeIn = ObjectAnimator.ofFloat(finalScoreCard, View.ALPHA, 0f, 1f)
        val scaleUpX = ObjectAnimator.ofFloat(finalScoreCard, View.SCALE_X, 0.8f, 1f)
        val scaleUpY = ObjectAnimator.ofFloat(finalScoreCard, View.SCALE_Y, 0.8f, 1f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(fadeIn, scaleUpX, scaleUpY)
        animatorSet.duration = 500
        animatorSet.start()

        // Set up retry button
        retryButton.setOnClickListener {
            // Reset the quiz
            currentQuestionIndex = 0
            score = 0

            // Show quiz UI again
            questionCardView.visibility = View.VISIBLE
            nextButton.visibility = View.VISIBLE
            finalScoreCard.visibility = View.GONE

            // Display first question
            setupRemoteConfig()
        }
    }

    companion object {
        // Remote config keys
        private const val REMOTE_CONFIG_QUIZ_KEY = "purim_quiz_questions"
    }
}

// Data class for quiz questions - must match the JSON structure
data class QuizQuestion(
    val questionText: String,
    var options: MutableList<String>,
    val correctAnswer: String
)