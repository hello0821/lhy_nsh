package com.example.madcamp_week2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Text

data class QuestionItem(
    val qNumber: String,
    val question: String,
    val answers: List<String>
)

val questions = listOf(
    QuestionItem("Q1","당신의 체력은?", listOf("장거리 비행도 거뜬", "단거리 비행만 할래요", "비행도 힘들다, 국내가 좋아")),
    QuestionItem("Q2","어느 것을 선호하시나요?", listOf("뷰맛집, 청정한 자연환경", "다양한 맛집", "관광 및 유적지 탐험")),
    QuestionItem("Q3", "누구와 함께하고 싶나요?", listOf("가족", "친구", "연인")),

)

class Recommendation : Fragment() {
    private var currentQuestionIndex = 0
    private val userResponses = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.recommendation, container, false)
        val nextBtn = view.findViewById<Button>(R.id.nextBtn)
        val submitBtn = view.findViewById<Button>(R.id.submitBtn)
        val qNum = view.findViewById<TextView>(R.id.qNumber)
        val question = view?.findViewById<TextView>(R.id.question)
        val selection1 = view?.findViewById<RadioButton>(R.id.selection1)
        val selection2 = view?.findViewById<RadioButton>(R.id.selection2)
        val selection3 = view?.findViewById<RadioButton>(R.id.selection3)
        qNum?.text = questions[0].qNumber
        question?.text = questions[0].question
        selection1?.text = questions[0].answers[0]
        selection2?.text = questions[0].answers[1]
        selection3?.text = questions[0].answers[2]

        nextBtn.setOnClickListener {
            val selectedOption = view?.findViewById<RadioGroup>(R.id.answersRadioGroup)?.checkedRadioButtonId
            selectedOption?.let {
                val radioButton = view?.findViewById<RadioButton>(it)
                val answerIndex = when (radioButton) {
                    selection1 -> 1
                    selection2 -> 2
                    selection3 -> 3
                    else -> 0
                }
                userResponses.add(answerIndex)

                if (currentQuestionIndex < questions.size - 1) {
                    currentQuestionIndex++
                    updateQuestion()
                }
            }
        }

        submitBtn.setOnClickListener {
            val selectedOption = view?.findViewById<RadioGroup>(R.id.answersRadioGroup)?.checkedRadioButtonId
            selectedOption?.let {
                val radioButton = view?.findViewById<RadioButton>(it)
                val answerIndex = when (radioButton) {
                    selection1 -> 1
                    selection2 -> 2
                    selection3 -> 3
                    else -> 0
                }
                userResponses.add(answerIndex)
            }

            val recommendedCountry = recommendCountry()
            val resultFragment = ResultRecommendation()
            val args = Bundle()
            args.putString("recommendedCountry", recommendedCountry)
            resultFragment.arguments = args

            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.totalView, resultFragment)
                .addToBackStack(null)
                .commit()

        }

        updateQuestion()


        return view
    }

    override fun onResume() {
        super.onResume()
        resetQuiz()
    }

    private fun resetQuiz() {
        currentQuestionIndex = 0
        userResponses.clear()
        updateQuestion()
    }

    private fun updateQuestion() {
        val radioGroup = view?.findViewById<RadioGroup>(R.id.answersRadioGroup)
        val nextBtn = view?.findViewById<Button>(R.id.nextBtn)
        val submitBtn = view?.findViewById<Button>(R.id.submitBtn)
        val qNum = view?.findViewById<TextView>(R.id.qNumber)
        val question = view?.findViewById<TextView>(R.id.question)
        val selection1 = view?.findViewById<RadioButton>(R.id.selection1)
        val selection2 = view?.findViewById<RadioButton>(R.id.selection2)
        val selection3 = view?.findViewById<RadioButton>(R.id.selection3)

        if (currentQuestionIndex < questions.size) {
            val currentQuestion = questions[currentQuestionIndex]
            qNum?.text = currentQuestion.qNumber
            question?.text = currentQuestion.question
            selection1?.text = currentQuestion.answers[0]
            selection2?.text = currentQuestion.answers[1]
            selection3?.text = currentQuestion.answers[2]

            radioGroup?.clearCheck()

            // 마지막 질문일 경우 '제출하기' 버튼 표시
            if (currentQuestionIndex == questions.size - 1) {
                nextBtn?.visibility = View.GONE
                submitBtn?.visibility = View.VISIBLE
            }
        }
    }

    private fun recommendCountry(): String {
        return when (userResponses.joinToString("-")) {
            "1-1-1" -> "호주"
            "1-1-2" -> "스위스"
            "1-1-3" -> "아이슬란드"
            "1-2-1" -> "이탈리아"
            "1-2-2" -> "미국"
            "1-2-3" -> "스페인"
            "1-3-1" -> "프랑스"
            "1-3-2" -> "체코"
            "1-3-3" -> "뉴질랜드"
            "2-1-1" -> "몽골"
            "2-1-2" -> "인도"
            "2-1-3" -> "홍콩"
            "2-2-1" -> "베트남"
            "2-2-2" -> "중국"
            "2-2-3" -> "대만"
            "2-3-1" -> "태국"
            "2-3-2" -> "러시아"
            "2-3-3" -> "일본"
            "3-1-1" -> "제주도"
            "3-1-2" -> "부산"
            "3-1-3" -> "제주도"
            "3-2-1" -> "순천"
            "3-2-2" -> "제주도"
            "3-2-3" -> "부산"
            "3-3-1" -> "여수"
            "3-3-2" -> "강릉"
            "3-3-3" -> "제주도"
            else -> "미국"
        }
    }

}