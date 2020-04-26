package com.miwas.domakruche.ui.leaderboard

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miwas.domakruche.R
import com.miwas.domakruche.network.model.Leader
import com.miwas.domakruche.utils.Constants

class LeaderBoardFragment : Fragment(), LeaderBoardView {

	private lateinit var presenter: LeaderBoardPresenter
	private val leaderBoardAdapter = LeaderBoardAdapter()
	private lateinit var leaderboardRecycler: RecyclerView
	private lateinit var placeTextView: TextView
	private lateinit var pointsCounterTextView: TextView
	private lateinit var usernameTextView: TextView
	private var sharedPreferences: SharedPreferences? = null

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val root = inflater.inflate(R.layout.fragment_leaderboard, container, false)
		placeTextView = root.findViewById(R.id.place)
		pointsCounterTextView = root.findViewById(R.id.current_user_points_counter)
		usernameTextView = root.findViewById(R.id.username)
		leaderboardRecycler = root.findViewById(R.id.leaderboard_recycler)
		leaderboardRecycler.apply {
			setHasFixedSize(true)
			layoutManager = LinearLayoutManager(context)
			adapter = leaderBoardAdapter
		}
		sharedPreferences = context?.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)
		(activity as AppCompatActivity).supportActionBar?.hide()
		presenter = LeaderBoardPresenter(LeaderBoardModel(sharedPreferences))
		presenter.attachView(this)
		presenter.viewIsReady()
		return root
	}

	override fun initLeaders(leaders: Array<Leader>) {
		leaderBoardAdapter.setLeaders(leaders)
	}

	override fun initCurrentUser(leader: Leader) {
		usernameTextView.text = leader.username
		pointsCounterTextView.text = leader.balance.toString()
		placeTextView.text = leader.rating.toString()
	}

	override fun onResume() {
		super.onResume()
		(activity as AppCompatActivity).supportActionBar?.hide()
	}

	override fun onStop() {
		super.onStop()
		(activity as AppCompatActivity).supportActionBar?.show()
	}
}
