package com.infoar.app.ui.catalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.infoar.app.data.local.entity.PlaceEntity
import com.utp.parcial2_proyecto.R

class PlaceAdapter(
    private var places: List<PlaceEntity>,
    private val onItemClick: (PlaceEntity) -> Unit,
    private val onEditClick: (PlaceEntity) -> Unit
) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    fun updateData(newPlaces: List<PlaceEntity>) {
        places = newPlaces
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        holder.bind(place, onEditClick)
        holder.itemView.setOnClickListener { onItemClick(place) }
    }

    override fun getItemCount(): Int = places.size

    class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvPlaceName)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvPlaceDescription)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)

        fun bind(place: PlaceEntity, onEditClick: (PlaceEntity) -> Unit) {
            tvName.text = place.name
            tvDescription.text = place.description
            btnEdit.setOnClickListener { onEditClick(place) }
        }
    }
}
