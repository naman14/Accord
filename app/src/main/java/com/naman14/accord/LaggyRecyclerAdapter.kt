package com.naman14.accord

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class LaggyRecyclerAdapter: RecyclerView.Adapter<LaggyRecyclerAdapter.ViewHolder>() {

    private val colors = arrayOf(Color.BLACK, Color.RED, Color.GRAY, Color.GREEN, Color.BLUE)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.rootView.setBackgroundColor(colors.random())
        // sleep for 100ms on every 5th item
        if (position % 3 == 0) Thread.sleep(200)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerview, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return 100
    }

    class ViewHolder(v: View): RecyclerView.ViewHolder(v)
}