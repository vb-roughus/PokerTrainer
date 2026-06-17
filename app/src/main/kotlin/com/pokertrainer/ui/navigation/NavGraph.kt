package com.pokertrainer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pokertrainer.ui.screens.game.GameScreen
import com.pokertrainer.ui.screens.home.HomeScreen
import com.pokertrainer.ui.screens.rules.GameFlowScreen
import com.pokertrainer.ui.screens.rules.GlossaryScreen
import com.pokertrainer.ui.screens.rules.HandRankingsScreen
import com.pokertrainer.ui.screens.rules.RulesScreen
import com.pokertrainer.ui.screens.strategy.StrategyScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onLearnRules = { navController.navigate(Screen.Rules.route) },
                onPlayGame = { navController.navigate(Screen.Game.route) },
                onStrategy = { navController.navigate(Screen.Strategy.route) }
            )
        }
        composable(Screen.Rules.route) {
            RulesScreen(
                onHandRankings = { navController.navigate(Screen.HandRankings.route) },
                onGameFlow = { navController.navigate(Screen.GameFlow.route) },
                onGlossary = { navController.navigate(Screen.Glossary.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.HandRankings.route) {
            HandRankingsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.GameFlow.route) {
            GameFlowScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Glossary.route) {
            GlossaryScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Game.route) {
            GameScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Strategy.route) {
            StrategyScreen(onBack = { navController.popBackStack() })
        }
    }
}
