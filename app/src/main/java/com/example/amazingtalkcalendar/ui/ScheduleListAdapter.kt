package com.example.amazingtalkcalendar.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.amazingtalkcalendar.data.model.ScheduleDividerItem
import com.example.amazingtalkcalendar.data.model.ScheduleItem
import com.example.amazingtalkcalendar.data.model.ScheduleSealed
import com.example.amazingtalkcalendar.databinding.ItemScheduleBinding
import com.example.amazingtalkcalendar.databinding.ItemScheduleDividerBinding

const val VIEW_TYPE_ITEM = 1
const val VIEW_TYPE_DIVIDER = 2

class ScheduleListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  private val data: MutableList<ScheduleSealed> = mutableListOf()

  inner class ScheduleItemViewHolder(private val binding: ItemScheduleBinding) : RecyclerView.ViewHolder(binding.root) {

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

  inner class ScheduleDividerItemViewHolder(private val binding: ItemScheduleDividerBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ScheduleDividerItem) {
      binding.displayTextView.text = item.displayText
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return when (viewType) {
      VIEW_TYPE_DIVIDER -> ScheduleDividerItemViewHolder(
        ItemScheduleDividerBinding.inflate(
          LayoutInflater.from(parent.context),
          parent,
          false
        )
      )
      else -> ScheduleItemViewHolder(
        ItemScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
      )
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (holder) {
      is ScheduleDividerItemViewHolder -> holder.bind(data[position] as ScheduleDividerItem)
      is ScheduleItemViewHolder -> holder.bind(data[position] as ScheduleItem)
    }
  }

  override fun getItemCount(): Int {
    return data.size
  }

  override fun getItemViewType(position: Int): Int {
    return when (data[position]) {
      is ScheduleDividerItem -> VIEW_TYPE_DIVIDER
      else -> VIEW_TYPE_ITEM
    }
  }

  @SuppressLint("NotifyDataSetChanged")
  fun update(list: List<ScheduleSealed>) {
    data.clear()
    data.addAll(list)
    notifyDataSetChanged()
  }
}