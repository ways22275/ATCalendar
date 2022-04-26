package com.example.amazingtalkcalendar.ui

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.amazingtalkcalendar.data.model.DayOfCurrentWeek

class HomeTabAdapter constructor(
  fragmentManager: FragmentManager, lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

  private val titleList = mutableListOf<DayOfCurrentWeek>()

  private val fragments = mutableListOf<ScheduleListFragment>()

  @SuppressLint("NotifyDataSetChanged")
  fun update(tabTitleList: List<DayOfCurrentWeek>) {
    titleList.clear()
    titleList.addAll(tabTitleList)
    fragments.clear()
    titleList.forEach {
      fragments.add(
        ScheduleListFragment.newInstance(it.originDate)
      )
    }
    notifyDataSetChanged()
  }

  fun getTitle(position: Int) = titleList[position].displayName

  override fun getItemCount() = titleList.size

  override fun getItemId(position: Int): Long {
    return fragments[position].hashCode().toLong()
  }

  override fun createFragment(position: Int): Fragment {
    return fragments[position]
  }
}