package com.maree.app

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp

@Composable
internal fun BrandIcon(size: Dp = 36.dp) {
    Image(painterResource(R.drawable.brand_mark), contentDescription = null,
        modifier = Modifier.size(size).clip(RoundedCornerShape(11.dp))
            .background(MareePalette.Mint).padding((size.value * 0.15f).dp))
}

@Composable
internal fun ScreenHeading(eyebrow: String, title: String, description: String? = null) {
    var revealed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { revealed = true }
    val entrance by animateFloatAsState(if (revealed) 1f else 0f, tween(480), label = "heading-entrance")
    Column(modifier = Modifier.graphicsLayer { alpha = entrance; translationY = (1f - entrance) * 14.dp.toPx() }, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(color = MareePalette.Mint, shape = RoundedCornerShape(6.dp)) {
            Text(eyebrow, color = MareePalette.Teal, style = MaterialTheme.typography.labelMedium,
                letterSpacing = 1.4.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
        }
        Text(title, color = MareePalette.Ink, style = MaterialTheme.typography.headlineMedium)
        if (description != null) Text(description, color = MareePalette.Muted,
            style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
internal fun AppCardSurface(modifier: Modifier = Modifier, containerColor: Color = MareePalette.Surface, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(26.dp), ambientColor = Color(0x102A5597), spotColor = Color(0x122A5597)), shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MareePalette.Outline)) {
        Column(Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

@Composable
internal fun SectionLabel(title: String, trailing: String? = null) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = MareePalette.Ink, style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f))
        if (trailing != null) Text(trailing, color = MareePalette.Muted,
            style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
internal fun StatusPill(label: String, color: Color = MareePalette.Teal,
                        background: Color = MareePalette.Subtle) {
    Surface(color = background, shape = RoundedCornerShape(50)) {
        Text(label, color = color, fontSize = 12.sp, lineHeight = 17.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
    }
}

@Composable
internal fun PrimaryAction(label: String, onClick: () -> Unit,
                           modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(onClick = onClick, enabled = enabled, shape = RoundedCornerShape(18.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
        modifier = modifier.heightIn(min = 52.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
    }
}
