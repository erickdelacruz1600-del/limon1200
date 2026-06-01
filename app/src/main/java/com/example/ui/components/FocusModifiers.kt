package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.tvFocusable(
    shape: Shape = RoundedCornerShape(12.dp),
    focusedBorderColor: Color = Color(0xFFD0BCFF), // TCL High Density Lavender accent for modern TV look
    unfocusedBorderColor: Color = Color.Transparent,
    borderWidth: Dp = 3.dp,
    scaleOnFocus: Float = 1.06f,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
): Modifier {
    val isFocused by interactionSource.collectIsFocusedAsState()
    val animatedScale by animateFloatAsState(
        targetValue = if (isFocused) scaleOnFocus else 1f,
        label = "scale"
    )

    return this
        .scale(animatedScale)
        .shadow(
            elevation = if (isFocused) 16.dp else 0.dp,
            shape = shape,
            clip = false,
            ambientColor = focusedBorderColor,
            spotColor = focusedBorderColor
        )
        .border(
            width = borderWidth,
            color = if (isFocused) focusedBorderColor else unfocusedBorderColor,
            shape = shape
        )
        .focusable(interactionSource = interactionSource)
}
