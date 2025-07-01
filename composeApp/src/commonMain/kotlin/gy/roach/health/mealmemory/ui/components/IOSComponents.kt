package gy.roach.health.mealmemory.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun IOSNavigationBar(
    title: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = backgroundColor,
        shadowElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(44.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                leadingIcon?.invoke()
            }

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp
                ),
                color = contentColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(2f)
            )

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                trailingIcon?.invoke()
            }
        }
    }
}

@Composable
fun IOSButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: IOSButtonStyle = IOSButtonStyle.Primary
) {
    val backgroundColor = when (style) {
        IOSButtonStyle.Primary -> if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        IOSButtonStyle.Secondary -> Color.Transparent
        IOSButtonStyle.Destructive -> Color(0xFFFF3B30)
    }

    val contentColor = when (style) {
        IOSButtonStyle.Primary -> Color.White
        IOSButtonStyle.Secondary -> MaterialTheme.colorScheme.primary
        IOSButtonStyle.Destructive -> Color.White
    }

    val borderColor = when (style) {
        IOSButtonStyle.Primary -> Color.Transparent
        IOSButtonStyle.Secondary -> MaterialTheme.colorScheme.primary
        IOSButtonStyle.Destructive -> Color.Transparent
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(enabled = enabled) { onClick() }
            .then(
                if (style == IOSButtonStyle.Secondary) {
                    Modifier.border(1.dp, borderColor, RoundedCornerShape(10.dp))
                } else Modifier
            ),
        color = backgroundColor,
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp),
            color = contentColor,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            textAlign = TextAlign.Center
        )
    }
}

enum class IOSButtonStyle {
    Primary, Secondary, Destructive
}

@Composable
fun IOSCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else Modifier
            ),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 0.5.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun IOSTabBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = backgroundColor,
        shadowElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(49.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
fun IOSTabItem(
    text: String,
    icon: ImageVector,
    selected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 10.sp
        )
    }
}

@Composable
fun IOSFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = Color.White,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        color = backgroundColor,
        shape = CircleShape,
        shadowElevation = 6.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                content()
            }
        }
    }
}

@Composable
fun IOSActionSheet(
    title: String? = null,
    message: String? = null,
    onDismiss: () -> Unit,
    actions: List<IOSActionSheetAction>
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column {
            if (title != null || message != null) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (title != null) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                    if (message != null) {
                        if (title != null) Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                HorizontalDivider(Modifier, DividerDefaults.Thickness, color = MaterialTheme.colorScheme.outline)
            }

            actions.forEachIndexed { index, action ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            action.onClick()
                            onDismiss()
                        },
                    color = Color.Transparent
                ) {
                    Text(
                        text = action.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (action.style == IOSActionStyle.Default) FontWeight.Normal else FontWeight.SemiBold
                        ),
                        color = when (action.style) {
                            IOSActionStyle.Default -> MaterialTheme.colorScheme.primary
                            IOSActionStyle.Destructive -> Color(0xFFFF3B30)
                            IOSActionStyle.Cancel -> MaterialTheme.colorScheme.primary
                        },
                        textAlign = TextAlign.Center
                    )
                }
                if (index < actions.size - 1) {
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

data class IOSActionSheetAction(
    val title: String,
    val style: IOSActionStyle = IOSActionStyle.Default,
    val onClick: () -> Unit
)

enum class IOSActionStyle {
    Default, Destructive, Cancel
}