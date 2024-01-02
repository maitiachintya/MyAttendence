package com.example.myattendence.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myattendence.model.DashbardItemModel
import com.example.myattendence.activity.HomeActivity
import com.example.myattendence.R
import com.example.myattendence.interfaceclass.onItemClick

class DashboardActivityAdapter(val context: HomeActivity, val itemslists: ArrayList<DashbardItemModel>, val onItemClick: onItemClick)
    : RecyclerView.Adapter<DashboardActivityAdapter.DashboardViewHolder>(){

    class DashboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgpic : ImageView = itemView.findViewById(R.id.imgdashboard)
        val txtletter : TextView = itemView.findViewById(R.id.txtdashboard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.itemviews_dashboard, parent, false)
        return DashboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val dashbardItemModel : DashbardItemModel = itemslists[position]
        holder.imgpic.setImageResource(dashbardItemModel.img)
        holder.txtletter.text = dashbardItemModel.text
    }

    override fun getItemCount(): Int {
        return itemslists.size
    }
}