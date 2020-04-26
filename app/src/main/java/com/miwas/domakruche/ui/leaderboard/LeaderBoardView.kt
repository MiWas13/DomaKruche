package com.miwas.domakruche.ui.leaderboard

import com.miwas.domakruche.base.MvpView
import com.miwas.domakruche.network.model.Leader

interface LeaderBoardView : MvpView {
	fun initLeaders(leaders: Array<Leader>)
	fun initCurrentUser(leader: Leader)
}