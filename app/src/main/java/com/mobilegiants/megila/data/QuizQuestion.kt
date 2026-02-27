package com.mobilegiants.megila.data

data class QuizQuestion(
    val questionText: String,
    var options: MutableList<String>,
    val correctAnswer: String
)
