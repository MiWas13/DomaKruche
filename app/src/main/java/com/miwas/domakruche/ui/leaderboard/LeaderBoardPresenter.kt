package com.miwas.domakruche.ui.leaderboard

import com.miwas.domakruche.base.BasePresenter
import com.miwas.domakruche.network.model.Leader

class LeaderBoardPresenter(private val leaderBoardModel: LeaderBoardModel) : BasePresenter<LeaderBoardView>() {
	override fun viewIsReady() {
		leaderBoardModel.getLeaders(leaderBoardModel.getToken()) {
			it?.let {
				val currentUser = findCurrentUser(it)
				baseView?.initLeaders(removeCurrentUserIfNeeded(it))
				baseView?.initCurrentUser(currentUser)
			}
		}
	}

	private fun findCurrentUser(leaders: Array<Leader>): Leader {
		return leaders.find { it.id == leaderBoardModel.getUserId() } ?: leaders.first()
	}

	private fun removeCurrentUserIfNeeded(leaders: Array<Leader>): Array<Leader> {
		return leaders.filterIndexed { index, leader ->
			index == 0 || leader.rating - 1 == leaders[index - 1].rating
		}.toTypedArray()
	}
}