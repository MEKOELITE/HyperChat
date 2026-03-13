package com.hyperchat.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                    IconButton(onClick = { /* handled by nav */ }) {
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 对方身份
            Text("对方身份", style = MaterialTheme.typography.titleMedium)
            var expandedRole by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedRole,
                onExpandedChange = { expandedRole = it }
            ) {
                OutlinedTextField(
                    value = getContactRoleName(uiState.contactRole),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRole) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedRole,
                    onDismissRequest = { expandedRole = false }
                ) {
                    ContactRole.entries.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(getContactRoleName(role)) },
                            onClick = {
                                viewModel.setContactRole(role)
                                expandedRole = false
                            }
                        )
                    }
                }
            }

            // 熟悉程度
            Text("熟悉程度: ${uiState.familiarity}/10", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = uiState.familiarity.toFloat(),
                onValueChange = { viewModel.setFamiliarity(it.toInt()) },
                valueRange = 1f..10f,
                steps = 8
            )

            // 关系温度
            Text("关系温度: ${uiState.relationTemperature}/10", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = uiState.relationTemperature.toFloat(),
                onValueChange = { viewModel.setRelationTemperature(it.toInt()) },
                valueRange = 1f..10f,
                steps = 8
            )

            // 是否有历史矛盾
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("是否有历史矛盾", style = MaterialTheme.typography.titleMedium)
                Switch(
                    checked = uiState.hasHistoryConflict,
                    onCheckedChange = { viewModel.setHasHistoryConflict(it) }
                )
            }

            // 聊天目标
            Text("聊天目标", style = MaterialTheme.typography.titleMedium)
            var expandedGoal by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedGoal,
                onExpandedChange = { expandedGoal = it }
            ) {
                OutlinedTextField(
                    value = getChatGoalName(uiState.chatGoal),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGoal) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedGoal,
                    onDismissRequest = { expandedGoal = false }
                ) {
                    ChatGoal.entries.forEach { goal ->
                        DropdownMenuItem(
                            text = { Text(getChatGoalName(goal)) },
                            onClick = {
                                viewModel.setChatGoal(goal)
                                expandedGoal = false
                            }
                        )
                    }
                }
            }

            // 聊天风格
            Text("聊天风格", style = MaterialTheme.typography.titleMedium)
            var expandedStyle by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedStyle,
                onExpandedChange = { expandedStyle = it }
            ) {
                OutlinedTextField(
                    value = getChatStyleName(uiState.chatStyle),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStyle) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedStyle,
                    onDismissRequest = { expandedStyle = false }
                ) {
                    ChatStyle.entries.forEach { style ->
                        DropdownMenuItem(
                            text = { Text(getChatStyleName(style)) },
                            onClick = {
                                viewModel.setChatStyle(style)
                                expandedStyle = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.createConversation() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("开始聊天")
                }
            }
        }
    }
}

fun getContactRoleName(role: ContactRole): String {
    return when (role) {
        ContactRole.COLLEAGUE -> "同事"
        ContactRole.SUPERIOR -> "上级"
        ContactRole.SUBORDINATE -> "下属"
        ContactRole.CUSTOMER -> "客户"
        ContactRole.FRIEND -> "朋友"
        ContactRole.ROMANTIC -> "暧昧对象"
        ContactRole.LOVER -> "恋人"
        ContactRole.FAMILY -> "家人"
        ContactRole.STRANGER -> "陌生人"
    }
}

fun getChatGoalName(goal: ChatGoal): String {
    return when (goal) {
        ChatGoal.CLOSER_RELATION -> "拉近关系"
        ChatGoal.INVITE_DINNER -> "邀请吃饭"
        ChatGoal.REQUEST_HELP -> "请求帮助"
        ChatGoal.PROMOTE_PRODUCT -> "推销产品"
        ChatGoal.RECONCILE -> "挽回关系"
        ChatGoal.REFUSE -> "拒绝请求"
    }
}

fun getChatStyleName(style: ChatStyle): String {
    return when (style) {
        ChatStyle.SINCERE -> "真诚型"
        ChatStyle.HUMOROUS -> "幽默型"
        ChatStyle.MATURE -> "成熟稳重"
        ChatStyle.CASUAL -> "轻松自然"
        ChatStyle.PROFESSIONAL -> "商务专业"
    }
}
