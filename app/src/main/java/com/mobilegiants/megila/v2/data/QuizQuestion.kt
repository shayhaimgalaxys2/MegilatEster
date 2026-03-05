package com.mobilegiants.megila.v2.data

data class QuizQuestion(
    val questionText: String,
    var options: MutableList<String>,
    val correctAnswer: String
)
