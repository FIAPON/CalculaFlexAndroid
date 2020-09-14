package br.com.heiderlopes.calculaflex.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.heiderlopes.calculaflex.R
import br.com.heiderlopes.calculaflex.models.dashboardmenu.DashboardItem
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dash_item.view.*

class HomeListAdapter(
    private var menuItems: List<DashboardItem>,
    private var clickListener: (DashboardItem) -> Unit
) : RecyclerView.Adapter<HomeListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dash_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return menuItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(menuItems[position], clickListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: DashboardItem, clickListener: (DashboardItem) -> Unit) {
            itemView.textView8.text = item.label

            Picasso.get()
                .load(item.image)
                .into(itemView.imageView4)

            itemView.setOnClickListener { clickListener(item) }
        }
    }
}