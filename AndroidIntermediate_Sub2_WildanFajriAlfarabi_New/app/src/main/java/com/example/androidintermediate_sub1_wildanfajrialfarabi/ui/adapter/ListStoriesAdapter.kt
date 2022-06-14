package com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidintermediate_sub1_wildanfajrialfarabi.R
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.ListStoryItem
import com.example.androidintermediate_sub1_wildanfajrialfarabi.databinding.StoryItemBinding

class ListStoriesAdapter :
    PagingDataAdapter<ListStoryItem, ListStoriesAdapter.ListViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem, view: View)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder(binding: StoryItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        var imageView = binding.storyImage
        var name = binding.uploadByText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        val image = data?.photoUrl
        val name = data?.name
        val uploader = buildString {
            append("Uploaded by ")
            append(name)
        }
        Glide.with(holder.itemView)
            .load(image)
            .override(800, 800)
            .fitCenter()
            .placeholder(R.drawable.noimage)
            .error(R.drawable.ic_baseline_error_outline_24_red)
            .into(holder.imageView)
        holder.name.text =  uploader
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(data!!,it)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}