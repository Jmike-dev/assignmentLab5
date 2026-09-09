package com.example.assignmentlab5.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.assignmentlab5.data.Bounty
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun BountyCard(
    bounty: Bounty,
    onAccept: (Bounty) -> Unit,
    onAbandon: (Bounty) -> Unit,
    onAdvanceProgress: (Bounty) -> Unit,
    modifier: Modifier = Modifier
) {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val dragThreshold = 300f

    val cardColor = if (bounty.isAccepted) Color(0xFFE8F5E9) else Color.White

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = when {
                    offsetX.value > 100f -> Color(0xFFC8E6C9)
                    offsetX.value < -100f -> Color(0xFFFFCDD2)
                    else -> Color.Transparent
                },
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(bounty.id) {
                    detectDragGestures(
                        onDragEnd = {
                            scope.launch {
                                if (offsetX.value > dragThreshold) {
                                    onAccept(bounty)
                                    offsetX.animateTo(0f)
                                } else if (offsetX.value < -dragThreshold) {
                                    onAbandon(bounty)
                                    offsetX.animateTo(0f)
                                } else {
                                    offsetX.animateTo(0f)
                                }
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                offsetX.snapTo(offsetX.value + dragAmount.x)
                            }
                        }
                    )
                }
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = bounty.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Reward: ${bounty.rewardGold} Gold",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFD4AF37)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    FlowLayout(horizontalSpacing = 6.dp, verticalSpacing = 6.dp) {
                        bounty.tags.forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = tag,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                ObjectiveRing(
                    progress = bounty.progress,
                    onAdvance = { onAdvanceProgress(bounty) }
                )
            }
        }
    }
}