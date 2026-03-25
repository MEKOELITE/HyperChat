package com.hyperchat.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hyperchat.app.domain.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewConversationScreen(
    onConversationCreated: (Long) -> Unit,
    viewModel: NewConversationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.createdConversationId) {
        uiState.createdConversationId?.let { id ->
            onConversationCreated(id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("新建会话") },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Introduction Card
            IntroductionCard()

            // Section 1: Contact Role
            SectionHeader(
                icon = Icons.Outlined.Person,
                title = "对方身份",
                subtitle = "选择与你的关系"
            )

            RoleSelectionGrid(
                selectedRole = uiState.contactRole,
                onRoleSelected = { viewModel.setContactRole(it) }
            )

            Divider()

            // Section 2: Relationship Context
            SectionHeader(
                icon = Icons.Outlined.Favorite,
                title = "关系状态",
                subtitle = "帮助AI更好地理解你们的互动"
            )

            // Familiarity Slider
            SliderSection(
                title = "熟悉程度",
                value = uiState.familiarity,
                onValueChange = { viewModel.setFamiliarity(it) },
                description = when {
                    uiState.familiarity <= 3 -> "初次见面或不太熟悉"
                    uiState.familiarity <= 6 -> "有一定了解"
                    else -> "非常熟悉"
                }
            )

            // Relation Temperature Slider
            SliderSection(
                title = "关系温度",
                value = uiState.relationTemperature,
                onValueChange = { viewModel.setRelationTemperature(it) },
                description = when {
                    uiState.relationTemperature <= 3 -> "关系较冷淡"
                    uiState.relationTemperature <= 6 -> "关系正常"
                    else -> "关系亲密友好"
                }
            )

            // History Conflict Toggle
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.hasHistoryConflict)
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (uiState.hasHistoryConflict)
                            Icons.Outlined.Warning else Icons.Outlined.Info,
                        contentDescription = null,
                        tint = if (uiState.hasHistoryConflict)
                            MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "是否有历史矛盾",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (uiState.hasHistoryConflict)
                                "曾经有过冲突或误解"
                            else "关系正常，没有明显矛盾",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.hasHistoryConflict,
                        onCheckedChange = { viewModel.setHasHistoryConflict(it) }
                    )
                }
            }

            Divider()

            // Section 3: Chat Goal
            SectionHeader(
                icon = Icons.Outlined.Flag,
                title = "聊天目标",
                subtitle = "你希望通过这次聊天达成什么"
            )

            GoalSelectionGrid(
                selectedGoal = uiState.chatGoal,
                onGoalSelected = { viewModel.setChatGoal(it) }
            )

            Divider()

            // Section 4: Chat Style
            SectionHeader(
                icon = Icons.Outlined.Style,
                title = "聊天风格",
                subtitle = "你希望展现什么样的沟通风格"
            )

            StyleSelectionGrid(
                selectedStyle = uiState.chatStyle,
                onStyleSelected = { viewModel.setChatStyle(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Start Button
            Button(
                onClick = { viewModel.createConversation() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(16.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "开始聊天辅助",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun IntroductionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Psychology,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "AI将根据你的设置",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "生成最适合当前场景的回复建议",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RoleSelectionGrid(
    selectedRole: ContactRole,
    onRoleSelected: (ContactRole) -> Unit
) {
    val roles = ContactRole.entries.toList()
    val columns = 3

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        roles.chunked(columns).forEach { rowRoles ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowRoles.forEach { role ->
                    RoleChip(
                        role = role,
                        isSelected = selectedRole == role,
                        onClick = { onRoleSelected(role) },
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(columns - rowRoles.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoleChip(
    role: ContactRole,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val roleInfo = getRoleInfo(role)

    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = roleInfo.first,
                contentDescription = null,
                tint = if (isSelected)
                    MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = roleInfo.second,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected)
                    MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getRoleInfo(role: ContactRole): Pair<ImageVector, String> {
    return when (role) {
        ContactRole.COLLEAGUE -> Icons.Outlined.Work to "同事"
        ContactRole.SUPERIOR -> Icons.Outlined.School to "上级"
        ContactRole.SUBORDINATE -> Icons.Outlined.PersonPin to "下属"
        ContactRole.CUSTOMER -> Icons.Outlined.Handshake to "客户"
        ContactRole.FRIEND -> Icons.Outlined.Groups to "朋友"
        ContactRole.ROMANTIC -> Icons.Outlined.FavoriteBorder to "暧昧"
        ContactRole.LOVER -> Icons.Outlined.Favorite to "恋人"
        ContactRole.FAMILY -> Icons.Outlined.FamilyRestroom to "家人"
        ContactRole.STRANGER -> Icons.Outlined.PersonOutline to "陌生人"
    }
}

@Composable
private fun SliderSection(
    title: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    description: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$value/10",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 1f..10f,
            steps = 8,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalSelectionGrid(
    selectedGoal: ChatGoal,
    onGoalSelected: (ChatGoal) -> Unit
) {
    val goals = ChatGoal.entries.toList()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        goals.chunked(2).forEach { rowGoals ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowGoals.forEach { goal ->
                    GoalChip(
                        goal = goal,
                        isSelected = selectedGoal == goal,
                        onClick = { onGoalSelected(goal) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowGoals.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalChip(
    goal: ChatGoal,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val goalInfo = getGoalInfo(goal)

    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                goalInfo.second.copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected)
                goalInfo.second
            else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = if (isSelected)
            androidx.compose.foundation.BorderStroke(2.dp, goalInfo.second)
        else null,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = goalInfo.first,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = goalInfo.third,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

private fun getGoalInfo(goal: ChatGoal): Triple<ImageVector, Color, String> {
    return when (goal) {
        ChatGoal.CLOSER_RELATION -> Triple(Icons.Outlined.Favorite, Color(0xFFEC4899), "拉近关系")
        ChatGoal.INVITE_DINNER -> Triple(Icons.Outlined.Restaurant, Color(0xFFF59E0B), "邀请吃饭")
        ChatGoal.REQUEST_HELP -> Triple(Icons.Outlined.Help, Color(0xFF6366F1), "请求帮助")
        ChatGoal.PROMOTE_PRODUCT -> Triple(Icons.Outlined.Sell, Color(0xFF10B981), "推销产品")
        ChatGoal.RECONCILE -> Triple(Icons.Outlined.Replay, Color(0xFFEF4444), "挽回关系")
        ChatGoal.REFUSE -> Triple(Icons.Outlined.Block, Color(0xFF8B5CF6), "拒绝请求")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StyleSelectionGrid(
    selectedStyle: ChatStyle,
    onStyleSelected: (ChatStyle) -> Unit
) {
    val styles = ChatStyle.entries.toList()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        styles.chunked(2).forEach { rowStyles ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowStyles.forEach { style ->
                    StyleChip(
                        style = style,
                        isSelected = selectedStyle == style,
                        onClick = { onStyleSelected(style) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowStyles.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StyleChip(
    style: ChatStyle,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val styleInfo = getStyleInfo(style)

    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isSelected)
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else null,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = styleInfo.first,
                contentDescription = null,
                tint = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = styleInfo.second,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = styleInfo.third,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

private fun getStyleInfo(style: ChatStyle): Triple<ImageVector, String, String> {
    return when (style) {
        ChatStyle.SINCERE -> Triple(Icons.Outlined.SentimentSatisfied, "真诚型", "表达真实情感")
        ChatStyle.HUMOROUS -> Triple(Icons.Outlined.SentimentVerySatisfied, "幽默型", "轻松活跃氛围")
        ChatStyle.MATURE -> Triple(Icons.Outlined.Shield, "成熟稳重", "稳重可靠")
        ChatStyle.CASUAL -> Triple(Icons.Outlined.Coffee, "轻松自然", "自然随意")
        ChatStyle.PROFESSIONAL -> Triple(Icons.Outlined.Business, "商务专业", "专业正式")
    }
}
