package com.example.amazingtalkcalendar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.amazingtalkcalendar.R
import com.example.amazingtalkcalendar.databinding.FragmentScheduleListBinding
import dagger.hilt.android.AndroidEntryPoint

const val ARGUMENT_KEY_FOR_SUB_DATE = "ARGUMENT_KEY_FOR_SUB_DATE"

@AndroidEntryPoint
class ScheduleListFragment : Fragment() {

  companion object {
    @JvmStatic
    fun newInstance(subDateTitle: String) = ScheduleListFragment().apply {
      arguments = Bundle().apply {
        putString(ARGUMENT_KEY_FOR_SUB_DATE, subDateTitle)
      }
    }
  }

  private lateinit var binding: FragmentScheduleListBinding

  private val viewModel: HomeViewModel by activityViewModels()

  private val listAdapter: ScheduleListAdapter = ScheduleListAdapter()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    binding = FragmentScheduleListBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initRecycleView()
    getData()
  }

  private fun initRecycleView() {
    binding.list.apply {
      layoutManager = LinearLayoutManager(requireContext())
      ResourcesCompat.getDrawable(resources, R.drawable.line_divider, null)?.let {
        val itemDecorationVertical =
          DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply { setDrawable(it) }
        addItemDecoration(itemDecorationVertical)
      }
      adapter = listAdapter
    }
  }

  private fun getData() {
    getCurrentSubDate()?.let { it ->
      val list = viewModel.getScheduleByStartAt(it)
      listAdapter.update(list)
    }
  }

  private fun getCurrentSubDate() = arguments?.getString(ARGUMENT_KEY_FOR_SUB_DATE)
}