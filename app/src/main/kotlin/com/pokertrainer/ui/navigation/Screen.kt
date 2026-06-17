package com.pokertrainer.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Rules : Screen("rules")
    data object HandRankings : Screen("hand_rankings")
    data object GameFlow : Screen("game_flow")
    data object Glossary : Screen("glossary")
    data object Game : Screen("game")
    data object Strategy : Screen("strategy")
}
