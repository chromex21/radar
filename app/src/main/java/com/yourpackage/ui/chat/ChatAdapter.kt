package com.yourpackage.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yourpackage.R
import com.yourpackage.chat.ChatMessage

private const val VIEW_TYPE_TEXT = 1
private const val VIEW_TYPE_IMAGE = 2

class ChatAdapter : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ChatMessage.TextMessage -> VIEW_TYPE_TEXT
            is ChatMessage.ImageMessage -> VIEW_TYPE_IMAGE
            else -> throw IllegalArgumentException("Invalid message type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TEXT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message, parent, false)
                TextViewHolder(view)
            }
            VIEW_TYPE_IMAGE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message, parent, false)
                ImageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TextViewHolder -> holder.bind(getItem(position) as ChatMessage.TextMessage)
            is ImageViewHolder -> holder.bind(getItem(position) as ChatMessage.ImageMessage)
        }
    }

    class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.message_text)

        fun bind(message: ChatMessage.TextMessage) {
            messageText.text = message.text
            messageText.visibility = View.VISIBLE
        }
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageImage: ImageView = itemView.findViewById(R.id.message_image)

        fun bind(message: ChatMessage.ImageMessage) {
            messageImage.visibility = View.VISIBLE
            Glide.with(itemView.context).load(message.imageUrl).into(messageImage)
        }
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }
}
