package com.example.amazingtalkcalendar.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.amazingtalkcalendar.data.model.ScheduleItem
import com.example.amazingtalkcalendar.data.model.ScheduleSealed
import com.example.amazingtalkcalendar.databinding.ItemScheduleBinding

class ScheduleListAdapter: RecyclerView.Adapter<ScheduleListAdapter.ScheduleItemViewHolder>() {

  private val data: MutableList<ScheduleSealed> = mutableListOf()

  inner class ScheduleItemViewHolder(private val binding: ItemScheduleBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ScheduleItem) {
      binding.displayText.apply {
        isEnabled = item.available
        text = item.startAt.substring(11, 16)
        val color = if (item.available) {
          ContextCompat.getColor(context, android.R.color.white)
        } else {
          ContextCompat.getColor(context, android.R.color.black)
        }
        setTextColor(color)
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleItemViewHolder {
    return ScheduleItemViewHolder(
      ItemScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
  }

  override fun onBindViewHolder(holder: ScheduleItemViewHolder, position: Int) {
    holder.bind(data[position] as ScheduleItem)
  }

  override fun getItemCount(): Int {
    return data.size
  }

  @SuppressLint("NotifyDataSetChanged")
  fun update(list: List<ScheduleSealed>) {
    data.clear()
    data.addAll(list)
    notifyDataSetChanged()
  }
}