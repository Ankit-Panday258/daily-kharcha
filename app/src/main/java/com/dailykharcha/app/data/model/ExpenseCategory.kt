package com.dailykharcha.app.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Standard expense categories for Daily Kharcha.
 * Includes Hindi + English bilingual names, icon representations, and distinct theme colors.
 */
enum class ExpenseCategory(
    val title: String,
    val hindiTitle: String,
    val icon: ImageVector,
    val color: Color
) {
    FOOD(
        title = "Khana (Food)",
        hindiTitle = "खाना",
        icon = Icons.Default.Fastfood,
        color = Color(0xFFEF4444) // Coral Red
    ),
    PETROL(
        title = "Petrol",
        hindiTitle = "पेट्रोल",
        icon = Icons.Default.LocalGasStation,
        color = Color(0xFFF59E0B) // Amber Orange
    ),
    RECHARGE(
        title = "Recharge",
        hindiTitle = "रिचार्ज",
        icon = Icons.Default.PhoneAndroid,
        color = Color(0xFF2563EB) // Royal Accent Blue
    ),
    RENT(
        title = "Kiraya (Rent)",
        hindiTitle = "किराया",
        icon = Icons.Default.Home,
        color = Color(0xFF8B5CF6) // Purple / Violet
    ),
    OTHER(
        title = "Other (Anya)",
        hindiTitle = "अन्य",
        icon = Icons.Default.MoreHoriz,
        color = Color(0xFF10B981) // Emerald Teal
    );

    companion object {
        fun fromString(value: String): ExpenseCategory {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: OTHER
        }
    }
}
