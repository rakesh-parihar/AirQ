package com.airq.adapter

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.airq.R
import com.airq.model.GenericDataModel

/**
 * Generic adapter for multiple recyclerViews
 *
 * @author Rakesh
 * @since 6/29/2018.
 */
class GenericAdapter(private val context: Context, private val dataList: ArrayList<GenericDataModel>, private val itemClick: View.OnClickListener) : RecyclerView.Adapter<GenericAdapter.ViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_state, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.txtName.text = dataList[i].name.replace('_', ' ')
        viewHolder.cardView.tag = i.toString()
        if (dataList[i].isSelected) {
            viewHolder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.colorLogo))
            viewHolder.txtName.setTextColor(context.resources.getColor(android.R.color.white))
        } else {
            viewHolder.cardView.setCardBackgroundColor(context.resources.getColor(android.R.color.white))
            viewHolder.txtName.setTextColor(context.resources.getColor(android.R.color.black))
        }

        viewHolder.cardView.setOnClickListener {
            itemClick.onClick(it)
            resetSelection(i)
        }
    }

    private fun resetSelection(pos: Int) {
        for (obj in dataList)
            obj.isSelected = false
        dataList[pos].isSelected = true
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtName: TextView = itemView.findViewById(R.id.txtName)
        var cardView: CardView = itemView.findViewById(R.id.cardView)

    }


}