package com.pokertrainer.ui.screens.rules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokertrainer.R
import com.pokertrainer.data.model.Card
import com.pokertrainer.data.model.Rank
import com.pokertrainer.data.model.Suit
import com.pokertrainer.ui.components.CardSize
import com.pokertrainer.ui.components.CardView
import com.pokertrainer.ui.theme.PrimaryGreenDark

data class HandRankingItem(
    val rank: Int,
    val nameRes: Int,
    val descRes: Int,
    val exampleCards: List<Card>,
    val emoji: String
)

val handRankings = listOf(
    HandRankingItem(1, R.string.hand_royal_flush, R.string.hand_royal_flush_desc,
        listOf(Card(Rank.ACE, Suit.SPADES), Card(Rank.KING, Suit.SPADES), Card(Rank.QUEEN, Suit.SPADES), Card(Rank.JACK, Suit.SPADES), Card(Rank.TEN, Suit.SPADES)), "👑"),
    HandRankingItem(2, R.string.hand_straight_flush, R.string.hand_straight_flush_desc,
        listOf(Card(Rank.NINE, Suit.HEARTS), Card(Rank.EIGHT, Suit.HEARTS), Card(Rank.SEVEN, Suit.HEARTS), Card(Rank.SIX, Suit.HEARTS), Card(Rank.FIVE, Suit.HEARTS)), "🔥"),
    HandRankingItem(3, R.string.hand_four_of_a_kind, R.string.hand_four_of_a_kind_desc,
        listOf(Card(Rank.ACE, Suit.SPADES), Card(Rank.ACE, Suit.HEARTS), Card(Rank.ACE, Suit.DIAMONDS), Card(Rank.ACE, Suit.CLUBS), Card(Rank.KING, Suit.SPADES)), "⚡"),
    HandRankingItem(4, R.string.hand_full_house, R.string.hand_full_house_desc,
        listOf(Card(Rank.KING, Suit.SPADES), Card(Rank.KING, Suit.HEARTS), Card(Rank.KING, Suit.DIAMONDS), Card(Rank.QUEEN, Suit.CLUBS), Card(Rank.QUEEN, Suit.HEARTS)), "🏠"),
    HandRankingItem(5, R.string.hand_flush, R.string.hand_flush_desc,
        listOf(Card(Rank.ACE, Suit.HEARTS), Card(Rank.JACK, Suit.HEARTS), Card(Rank.NINE, Suit.HEARTS), Card(Rank.SIX, Suit.HEARTS), Card(Rank.TWO, Suit.HEARTS)), "💧"),
    HandRankingItem(6, R.string.hand_straight, R.string.hand_straight_desc,
        listOf(Card(Rank.TEN, Suit.SPADES), Card(Rank.NINE, Suit.HEARTS), Card(Rank.EIGHT, Suit.DIAMONDS), Card(Rank.SEVEN, Suit.CLUBS), Card(Rank.SIX, Suit.SPADES)), "📈"),
    HandRankingItem(7, R.string.hand_three_of_a_kind, R.string.hand_three_of_a_kind_desc,
        listOf(Card(Rank.QUEEN, Suit.SPADES), Card(Rank.QUEEN, Suit.HEARTS), Card(Rank.QUEEN, Suit.DIAMONDS), Card(Rank.SEVEN, Suit.CLUBS), Card(Rank.TWO, Suit.SPADES)), "🎯"),
    HandRankingItem(8, R.string.hand_two_pair, R.string.hand_two_pair_desc,
        listOf(Card(Rank.ACE, Suit.SPADES), Card(Rank.ACE, Suit.HEARTS), Card(Rank.KING, Suit.DIAMONDS), Card(Rank.KING, Suit.CLUBS), Card(Rank.SEVEN, Suit.SPADES)), "✌️"),
    HandRankingItem(9, R.string.hand_one_pair, R.string.hand_one_pair_desc,
        listOf(Card(Rank.JACK, Suit.SPADES), Card(Rank.JACK, Suit.HEARTS), Card(Rank.ACE, Suit.DIAMONDS), Card(Rank.SEVEN, Suit.CLUBS), Card(Rank.TWO, Suit.SPADES)), "👆"),
    HandRankingItem(10, R.string.hand_high_card, R.string.hand_high_card_desc,
        listOf(Card(Rank.ACE, Suit.SPADES), Card(Rank.JACK, Suit.HEARTS), Card(Rank.NINE, Suit.DIAMONDS), Card(Rank.FIVE, Suit.CLUBS), Card(Rank.TWO, Suit.SPADES)), "🂡")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HandRankingsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.rules_hand_rankings)) },
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
            items(handRankings) { item ->
                HandRankingCard(item)
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun HandRankingCard(item: HandRankingItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = item.emoji, fontSize = 24.sp)
                Column {
                    Text(
                        text = "#${item.rank} ${stringResource(item.nameRes)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFFFC107),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(item.descRes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFBDBDBD)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                item.exampleCards.forEach { card ->
                    CardView(card = card, size = CardSize.SMALL)
                }
            }
        }
    }
}
