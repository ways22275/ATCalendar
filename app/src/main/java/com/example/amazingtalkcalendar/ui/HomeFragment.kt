package com.example.amazingtalkcalendar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.amazingtalkcalendar.R
import com.example.amazingtalkcalendar.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

  private lateinit var binding: FragmentHomeBinding
  private lateinit var tabsAdapterHome: HomeTabAdapter

  private val viewModel: HomeViewModel by activityViewModels()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    binding = FragmentHomeBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setupButtonView()
    setupTabLayout()

    observeViewModelData()
  }

  private fun setupButtonView() {
    binding.previousButton.setOnClickListener {
      viewModel.previous()
    }

    binding.nextButton.setOnClickListener {
      viewModel.nextWeek()
    }
  }

  private fun setupTabLayout() {
    tabsAdapterHome = HomeTabAdapter(childFragmentManager, lifecycle)
    binding.viewPager.adapter = tabsAdapterHome

    TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
      tab.text = tabsAdapterHome.getTitle(position)
    }.attach()

  }

  private fun observeViewModelData() {
    viewModel.title.observe(viewLifecycleOwner) {
      binding.titleText.text = it.toString()
    }

    viewModel.previousBtnEnabled.observe(viewLifecycleOwner) {
      binding.previousButton.isEnabled = it
    }

    viewModel.subTitleList.observe(viewLifecycleOwner) {
      tabsAdapterHome.update(it)
      val defaultTab = binding.tabLayout.getTabAt(0)
      binding.tabLayout.selectTab(defaultTab)
      binding.viewPager.setCurrentItem(0, false)
    }
  }


}