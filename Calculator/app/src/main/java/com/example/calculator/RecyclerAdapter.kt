package com.example.calculator

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_row.view.*

class RecyclerAdapter(val calculationHistory: ArrayList<String>) : RecyclerView.Adapter<RecyclerAdapter.calculationHistoryVH>() {
    class calculationHistoryVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): calculationHistoryVH {
        //inflater
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row, parent, false)
        return calculationHistoryVH(itemView)
    }


    override fun getItemCount(): Int {
        return calculationHistory.size
    }

    override fun onBindViewHolder(holder: calculationHistoryVH, position: Int) {
        holder.itemView.recyclerViewTextView.append(calculationHistory.get(position))
    }

}