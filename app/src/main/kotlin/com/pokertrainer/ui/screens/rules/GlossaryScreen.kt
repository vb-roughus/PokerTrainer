package com.pokertrainer.ui.screens.rules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pokertrainer.R
import com.pokertrainer.ui.theme.PrimaryGreenDark

private val glossaryTerms = listOf(
    "Blinds" to "Forced bets posted before cards are dealt. Small Blind = half, Big Blind = full blind amount.",
    "Hole Cards" to "Your 2 private cards, only visible to you.",
    "Community Cards" to "The 5 cards dealt face-up in the center, shared by all players.",
    "The Flop" to "The first 3 community cards dealt at once.",
    "The Turn" to "The 4th community card.",
    "The River" to "The 5th and final community card.",
    "Pot" to "The total chips bet in the current hand.",
    "Pot Odds" to "The ratio of chips in the pot to the cost of calling. Helps decide if calling is profitable.",
    "Position" to "Your seat relative to the dealer. Later position = more information = advantage.",
    "Button / Dealer" to "The player who acts last in most betting rounds. Rotates clockwise each hand.",
    "Showdown" to "When remaining players reveal their cards to determine the winner.",
    "Bluff" to "Betting or raising with a weak hand to make opponents fold.",
    "Equity" to "Your percentage chance of winning the hand at any given moment.",
    "Outs" to "Cards remaining in the deck that would improve your hand to likely win.",
    "Kicker" to "A side card used to break ties when two players have the same hand rank.",
    "Muck" to "To discard your hand without showing it (when you fold or lose at showdown).",
    "Donk Bet" to "A bet made by an out-of-position player into the pre-flop aggressor.",
    "Value Bet" to "Betting with a strong hand hoping your opponent calls with a worse hand.",
    "Check-Raise" to "Checking first, then raising when your opponent bets — a deceptive move."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlossaryScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.glossary_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.nav_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreenDark,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            items(glossaryTerms.size) { idx ->
                val (term, definition) = glossaryTerms[idx]
                GlossaryCard(term = term, definition = definition)
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun GlossaryCard(term: String, definition: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = term,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFFFC107),
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = definition,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFBDBDBD)
            )
        }
    }
}
