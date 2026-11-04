package com.dailykharcha.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailykharcha.app.data.model.CategorySpending
import com.dailykharcha.app.data.model.ExpenseCategory
import com.dailykharcha.app.ui.theme.TextPrimary
import com.dailykharcha.app.ui.theme.TextSecondary
import com.dailykharcha.app.ui.theme.TextTertiary
import com.dailykharcha.app.util.CurrencyFormatter
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Interactive category-wise Donut / Pie Chart for Daily Kharcha.
 *
 * Displays:
 * - Animated category slices with distinct colors
 * - Center interactive display with total or selected category amount
 * - Touch detection to select category
 * - Legend chips displaying Category name, ₹ amount, and %
 * - Graceful empty state when no expenses exist
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryPieChart(
    categorySpendingList: List<CategorySpending>,
    totalPaise: Long,
    modifier: Modifier = Modifier,
    onCategoryClick: ((ExpenseCategory?) -> Unit)? = null
) {
    var selectedCategory by remember { mutableStateOf<ExpenseCategory?>(null) }
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(categorySpendingList, totalPaise) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Chart Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PieChart,
                            contentDescription = "Pie Chart",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Category Hisaab",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Kharcha Batwara (%)",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                if (selectedCategory != null) {
                    Text(
                        text = "Reset",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                selectedCategory = null
                                onCategoryClick?.invoke(null)
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // If empty, show clean empty state
            if (categorySpendingList.isEmpty() || totalPaise <= 0L) {
                EmptyChartState()
            } else {
                // Interactive Donut Chart Canvas with Center Information
                Box(
                    modifier = Modifier.size(230.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val strokeWidth = 34.dp
                    Canvas(
                        modifier = Modifier
                            .size(210.dp)
                            .pointerInput(categorySpendingList) {
                                detectTapGestures { offset ->
                                    val center = Offset(size.width / 2f, size.height / 2f)
                                    val dx = offset.x - center.x
                                    val dy = offset.y - center.y
                                    val distance = sqrt(dx * dx + dy * dy)
                                    val outerRadius = size.width / 2f
                                    val innerRadius = outerRadius - 40.dp.toPx()

                                    // Check if tapped inside the donut ring
                                    if (distance in innerRadius..outerRadius) {
                                        var touchAngle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                        if (touchAngle < 0f) touchAngle += 360f
                                        // Standardize to start at 270 (-90)
                                        val angleFromTop = (touchAngle + 90f) % 360f

                                        var currentAngle = 0f
                                        var tappedCat: ExpenseCategory? = null
                                        for (item in categorySpendingList) {
                                            val sweep = (item.percentage / 100f) * 360f
                                            if (angleFromTop in currentAngle..(currentAngle + sweep)) {
                                                tappedCat = item.category
                                                break
                                            }
                                            currentAngle += sweep
                                        }

                                        selectedCategory = if (selectedCategory == tappedCat) null else tappedCat
                                        onCategoryClick?.invoke(selectedCategory)
                                    } else {
                                        // Tapped center
                                        selectedCategory = null
                                        onCategoryClick?.invoke(null)
                                    }
                                }
                            }
                    ) {
                        val canvasSize = size.minDimension
                        val radius = (canvasSize - strokeWidth.toPx()) / 2f
                        val topLeft = Offset(
                            x = (size.width - 2 * radius) / 2f,
                            y = (size.height - 2 * radius) / 2f
                        )
                        val arcSize = Size(radius * 2f, radius * 2f)

                        var startAngle = -90f // Start from 12 o'clock

                        categorySpendingList.forEach { item ->
                            val sweepAngle = (item.percentage / 100f) * 360f * animationProgress.value
                            val isSelected = selectedCategory == item.category
                            val effectiveStroke = if (isSelected) strokeWidth.toPx() * 1.22f else strokeWidth.toPx()

                            drawArc(
                                color = item.category.color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(
                                    width = effectiveStroke,
                                    cap = StrokeCap.Butt
                                )
                            )
                            startAngle += sweepAngle
                        }
                    }

                    // Center Content in Donut
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        val activeItem = categorySpendingList.firstOrNull { it.category == selectedCategory }
                        if (activeItem != null) {
                            Text(
                                text = activeItem.category.hindiTitle,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = activeItem.category.color
                            )
                            Text(
                                text = CurrencyFormatter.formatPaiseToRupees(activeItem.totalPaise),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = String.format("%.1f%%", activeItem.percentage),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        } else {
                            Text(
                                text = "KUL KHARCHA",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = CurrencyFormatter.formatPaiseToRupees(totalPaise),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Total Spending",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Category Legends & Breakdown Chips
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categorySpendingList.forEach { item ->
                        val isSelected = selectedCategory == item.category
                        LegendChip(
                            spending = item,
                            isSelected = isSelected,
                            onClick = {
                                selectedCategory = if (isSelected) null else item.category
                                onCategoryClick?.invoke(selectedCategory)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendChip(
    spending: CategorySpending,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) spending.category.color else MaterialTheme.colorScheme.outline
    val bgColor = if (isSelected) spending.category.color.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(spending.category.color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = spending.category.title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = CurrencyFormatter.formatPaiseToRupees(spending.totalPaise),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = spending.category.color
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "(${String.format("%.0f%%", spending.percentage)})",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun EmptyChartState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(modifier = Modifier.size(140.dp)) {
            val strokeWidth = 14.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2f
            drawCircle(
                color = Color(0xFFE2E8F0),
                radius = radius,
                style = Stroke(
                    width = strokeWidth,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Is Mahine Koi Kharcha Nahi",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Apna kharcha jodne ke liye neeche '+' dabayein",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}
