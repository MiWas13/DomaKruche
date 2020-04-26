package com.miwas.domakruche.ui.leaderboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.miwas.domakruche.R
import com.miwas.domakruche.network.model.Leader

class LeaderBoardAdapter : RecyclerView.Adapter<LeaderBoardAdapter.LeaderBoardRecyclerViewHolder>() {

	private var leaders: Array<Leader> = arrayOf()

	class LeaderBoardRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderBoardRecyclerViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.leaderboard_item, parent, false)
		return LeaderBoardRecyclerViewHolder(view)
	}

	override fun getItemCount(): Int {
		return leaders.size
	}

	override fun onBindViewHolder(holder: LeaderBoardRecyclerViewHolder, position: Int) {
		val leaderName = holder.itemView.findViewById<TextView>(R.id.leader_name_text_view)
		val leaderPointsCount = holder.itemView.findViewById<TextView>(R.id.leader_points_counter)
		val leaderNumber = holder.itemView.findViewById<TextView>(R.id.leader_number_text_view)
		leaderName.text = leaders[position].username
		leaderPointsCount.text = leaders[position].balance.toString()
		leaderNumber.text = leaders[position].rating.toString()
	}

	fun setLeaders(leaders: Array<Leader>) {
		this.leaders = leaders
		notifyDataSetChanged()
	}

}