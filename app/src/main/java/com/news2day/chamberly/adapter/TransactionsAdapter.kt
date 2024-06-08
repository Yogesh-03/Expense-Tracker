package com.news2day.chamberly.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.news2day.chamberly.R
import com.news2day.chamberly.model.dto.Transaction

class TransactionsAdapter(private val transactionList: ArrayList<Transaction?>?) : RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
       return transactionList!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(transactionList!![position]!!)
    }
}

class MyViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
    private val category:TextView = itemView.findViewById(R.id.tvCategory)
    private val desc:TextView = itemView.findViewById(R.id.tvDescription)
    private val amount:TextView = itemView.findViewById(R.id.tvAmount)
    private val date:TextView = itemView.findViewById(R.id.tvDate)
    private val time:TextView = itemView.findViewById(R.id.tvTime)

    fun bind(transaction: Transaction){
        val amt = Integer.parseInt(transaction.amount)
        if (amt>=0){
            amount.setTextColor(itemView.context.resources.getColor(R.color.green))
        }else{
            amount.setTextColor(itemView.context.resources.getColor(R.color.red))
        }
        category.text = transaction.category
        desc.text = transaction.description
        amount.text = "$ "+transaction.amount
        date.text = transaction.date
        time.text = transaction.time
    }
}