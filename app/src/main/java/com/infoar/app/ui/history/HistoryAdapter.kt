package com.infoar.app.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.infoar.app.data.local.entity.HistoryEntity
import com.utp.parcial2_proyecto.R

class HistoryAdapter(
    private val onDeleteClick: (HistoryEntity) -> Unit,
    private val onItemClick: (HistoryEntity) -> Unit
) : ListAdapter<HistoryEntity, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = getItem(position)
        holder.bind(history, onDeleteClick, onItemClick)
    }

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvHistoryName)
        private val tvDate: TextView = itemView.findViewById(R.id.tvHistoryDate)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvHistoryCategory)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(
            history: HistoryEntity,
            onDeleteClick: (HistoryEntity) -> Unit,
            onItemClick: (HistoryEntity) -> Unit
        ) {
            tvName.text = history.name
            tvDate.text = history.date
            tvCategory.text = history.category
            
            btnDelete.setOnClickListener { onDeleteClick(history) }
            itemView.setOnClickListener { onItemClick(history) }
        }
    }

    class HistoryDiffCallback : DiffUtil.ItemCallback<HistoryEntity>() {
        override fun areItemsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
            return oldItem == newItem
        }
    }
}
