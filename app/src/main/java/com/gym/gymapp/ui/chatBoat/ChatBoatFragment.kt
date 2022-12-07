package com.gym.gymapp.ui.chatBoat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gym.gymapp.R
import com.gym.gymapp.databinding.FragmentChatBoatBinding
import com.gym.gymapp.ui.chatBoat.adapter.ChatAdapter
import com.gym.gymapp.ui.chatBoat.model.ChatData
import com.gym.gymapp.utils.AgileLoader
import com.gym.gymapp.utils.Resource
import com.gym.gymapp.utils.SessionManager
import com.gym.gymapp.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@AndroidEntryPoint
class ChatBoatFragment : Fragment(R.layout.fragment_chat_boat), View.OnClickListener {
    lateinit var binding: FragmentChatBoatBinding

    private var currentView: View? = null

    @Inject
    lateinit var session: SessionManager

    lateinit var appLoader: AgileLoader

    @Inject
    lateinit var chatAdapter: ChatAdapter

    private val dateFormat: DateFormat = SimpleDateFormat("dd MMM yyyy hh:mma", Locale.getDefault())

    private var chatList: MutableList<ChatData> = ArrayList()

    private val chatBoatViewModel: ChatBoatViewModel by viewModels()

    private var questionCompleted: Boolean = false

    var questionsIds:String = ""
    var answers:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_chat_boat, container, false)
            binding = FragmentChatBoatBinding.bind(currentView!!)
            init()
            setObserver()
            setRecyclerView()
            setOnClickListener()
        }
        return currentView!!
    }


    private fun init() {
        appLoader = AgileLoader(requireContext())
        val currentDate = dateFormat.format(Date())
        binding.date.text = currentDate
        chatBoatViewModel.getFaqQuestion()
    }

    private fun setObserver() {
        chatBoatViewModel.getFaqQuestion.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    appLoader.dismiss()
                    if (response.data!!.success) {
                        setChatData(response.data.data[0])
                    } else {
                        requireContext().showToast(response.message.toString())
                    }
                }
                is Resource.Loading -> {
                    appLoader.show()
                }
                is Resource.Error -> {
                    appLoader.dismiss()
                }
            }
        }

        chatBoatViewModel.getNextFaqQuestion.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    appLoader.dismiss()
                    if (response.data!!.success) {
                        setRecyclerView()

                        setChatData(response.data.data[0])
                    } else {
                        binding.allQuestionsSubmitted.isVisible = true
                    }
                }
                is Resource.Loading -> {
                    appLoader.show()
                }
                is Resource.Error -> {
                    if (response.message?.contains("Conversion Error") == true) {
//                        binding.completedView.isVisible = true
//                        binding.allQuestionsSubmitted.isVisible = true
                        binding.descriptionEdt.isVisible = true
                        binding.messageSubmit.isVisible = true
                        questionCompleted = true


                        chatAdapter.showOption(false)
                        val params:MutableMap<String,String> = HashMap()
                        params["question"] = questionsIds
                        params["answer"] = answers
                        params["message"] = ""
                        println("=== $params")
                        chatBoatViewModel.submitAllQuestions(params)
                    }
                    appLoader.dismiss()
                }
            }
        }
        chatBoatViewModel.submitAllQuestions.observe(viewLifecycleOwner) { }
    }

    private fun setChatData(data: ChatData) {
        val chat = ChatData(
            data.id,
            data.options,
            data.question,
            right = false,
            answer = ""
        )
        chatList.add(chat)
        chatAdapter.differ.submitList(chatList)
        chatAdapter.notifyItemInserted(chatList.size - 1)
        binding.chatRv.scrollToPosition(chatList.size - 1)

    }


    private fun setRecyclerView() {
        binding.chatRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }
    }


    private fun setOnClickListener() {
        binding.backArrow.setOnClickListener(this@ChatBoatFragment)
        chatAdapter.setOnOptionClickListener { option, questionId ->
            if (!questionCompleted) {
                val chat = ChatData(
                    0,
                    listOf(""),
                    question = "",
                    right = true,
                    answer = option
                )
                chatList.add(chat)
                chatAdapter.differ.submitList(chatList)
                chatAdapter.notifyItemInserted(chatList.size - 1)
                binding.chatRv.scrollToPosition(chatList.size - 1)

                val params: MutableMap<String, String> = HashMap()
                params["question_id"] = chatAdapter.differ.currentList[chatAdapter.differ.currentList.size-2].id.toString()
                chatBoatViewModel.getNextFaqQuestion(params = params)

                questionsIds = if(questionsIds.isEmpty()){chatAdapter.differ.currentList[chatAdapter.differ.currentList.size-2].id.toString()}else{
                    "$questionsIds,${chatAdapter.differ.currentList[chatAdapter.differ.currentList.size-2].id.toString()}"
                }

                answers = if(answers.isEmpty()){option}else{
                    "$answers,$option"
                }

            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backArrow -> {
                findNavController().popBackStack()
            }
        }
    }

}