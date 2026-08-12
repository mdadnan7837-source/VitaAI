package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Egg
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.navigation.Screen
import com.example.ui.components.AppBottomBar
import com.example.ui.components.GoalSelectionBottomSheet
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Shared Color Palette for Unified Design System
private val PrimaryPurple = Color(0xFF6D28D9)
private val LightPurpleBg = Color(0xFFF5F3FF)
private val PurpleBorder = Color(0xFFDDD6FE)
private val GreenAccent = Color(0xFF16A34A)
private val AmberAccent = Color(0xFFEA580C)
private val RedAccent = Color(0xFFDC2626)
private val WaterBlue = Color(0xFF2563EB)
private val AppBackground = Color(0xFFF8F9FE)
private val PrimaryTextColor = Color(0xFF111827)
private val SecondaryTextColor = Color(0xFF6B7280)
private val CardBorderColor = Color(0xFFE5E7EB)

// Cancer-Aware-only nutrient colors (per official Cancer-Aware design spec).
// Applied only when activeGoal == CANCER_AWARE; every other goal keeps the
// shared GreenAccent/AmberAccent/RedAccent palette above untouched.
private val CancerFiberGreen = Color(0xFF16A34A)
private val CancerFruitsVegBlue = Color(0xFF0284C7)
private val CancerWholeGrainAmber = Color(0xFFD97706)
private val CancerProcessedMeatRed = Color(0xFFDC2626)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigate: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showGoalSheet by remember { mutableStateOf(false) }
    var showAddWaterSheet by remember { mutableStateOf(false) }
    var showManualWaterInput by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var syncSuccessMsg by remember { mutableStateOf<String?>(null) }

    if (showGoalSheet) {
        GoalSelectionBottomSheet(
            selectedGoal = uiState.activeGoal,
            onGoalSelected = { goal ->
                viewModel.selectGoal(goal)
            },
            onDismissRequest = { showGoalSheet = false }
        )
    }

    if (showAddWaterSheet) {
        AddWaterBottomSheet(
            onPresetSelected = { amountMl ->
                viewModel.addWaterIntake(amountMl / 1000.0)
                showAddWaterSheet = false
            },
            onManualSelected = {
                showAddWaterSheet = false
                showManualWaterInput = true
            },
            onDismissRequest = { showAddWaterSheet = false }
        )
    }

    if (showManualWaterInput) {
        ManualWaterInputDialog(
            onConfirm = { amountMl ->
                viewModel.addWaterIntake(amountMl / 1000.0)
                showManualWaterInput = false
            },
            onDismissRequest = { showManualWaterInput = false }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                modifier = Modifier.width(310.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // User Profile Header in Drawer
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape),
                                color = PrimaryPurple.copy(alpha = 0.12f),
                                border = androidx.compose.foundation.BorderStroke(2.dp, PrimaryPurple)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Profile",
                                        tint = PrimaryPurple,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = if (uiState.activeGoal == com.example.model.NutritionGoal.WEIGHT_LOSS) "Gogo Ji" else "Alex Morgan",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = PrimaryTextColor
                                    )
                                )
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFEFF6FF)
                                ) {
                                    Text(
                                        text = "Goal: Weight Loss (181.4 lb)",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = WaterBlue,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        androidx.compose.material3.HorizontalDivider(
                            color = CardBorderColor,
                            thickness = 1.dp
                        )

                        Text(
                            text = "NAVIGATION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SecondaryTextColor,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            )
                        )

                        // 1. Food Analysis History
                        NavigationDrawerItem(
                            label = {
                                Text(
                                    text = "Food Analysis History",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = PrimaryPurple
                                )
                            },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onNavigate(Screen.FoodAnalysisHistory.route)
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedContainerColor = LightPurpleBg
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // 2. Permanent History
                        NavigationDrawerItem(
                            label = {
                                Text(
                                    text = "Permanent History Reports",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = SecondaryTextColor
                                )
                            },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onNavigate(Screen.History.route)
                            },
                            shape = RoundedCornerShape(12.dp)
                        )

                        // 3. Progress & Analytics
                        NavigationDrawerItem(
                            label = {
                                Text(
                                    text = "Progress & Analytics",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = SecondaryTextColor
                                )
                            },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onNavigate(Screen.Progress.route)
                            },
                            shape = RoundedCornerShape(12.dp)
                        )

                        // 4. AI Nutrition Coach
                        NavigationDrawerItem(
                            label = {
                                Text(
                                    text = "AI Nutrition Coach",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFFA855F7)
                                )
                            },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onNavigate(Screen.AICoach.route)
                            },
                            shape = RoundedCornerShape(12.dp)
                        )

                        // 5. Profile & Settings
                        NavigationDrawerItem(
                            label = {
                                Text(
                                    text = "Profile & Settings",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = SecondaryTextColor
                                )
                            },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onNavigate(Screen.Profile.route)
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // End-of-Day AI Sync Box
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = LightPurpleBg),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PurpleBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.AutoAwesome,
                                    contentDescription = null,
                                    tint = PrimaryPurple,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "End-of-Day AI Sync",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = PrimaryPurple
                                    )
                                )
                            }
                            Text(
                                text = "Processes today's food analysis history into permanent record.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = SecondaryTextColor,
                                    lineHeight = 15.sp
                                )
                            )
                            Button(
                                onClick = {
                                    syncSuccessMsg = "Daily report saved to Permanent History!"
                                    scope.launch { drawerState.close() }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                            ) {
                                Text(
                                    text = "Run End-of-Day Sync",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                AppBottomBar(
                    currentRoute = Screen.Home.route,
                    onNavigate = onNavigate
                )
            },
            containerColor = AppBackground,
            modifier = Modifier.testTag("home_screen")
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header: Menu, Greeting, Notification, Avatar
                HomeHeaderSection(
                    greeting = uiState.greetingMessage,
                    subtitle = uiState.subtitleMessage,
                    notificationCount = uiState.notificationCount,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onNotificationClick = { },
                    onProfileClick = { onNavigate(Screen.Profile.route) }
                )

                if (syncSuccessMsg != null) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = syncSuccessMsg!!,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = GreenAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = GreenAccent,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { syncSuccessMsg = null }
                            )
                        }
                    }
                }

            // Goal Banner Card ("Cancer-Aware Nutrition")
            GoalBannerSection(
                title = uiState.currentGoalTitle,
                description = uiState.currentGoalDescription,
                isCancerAware = uiState.activeGoal == com.example.model.NutritionGoal.CANCER_AWARE,
                onSwitchGoal = { showGoalSheet = true },
                onLearnMore = { }
            )

            // Today's Progress Section
            TodayProgressSection(
                progressPercentage = uiState.progressPercentage,
                fiberProgress = uiState.fiberProgress,
                fruitsVeggiesProgress = uiState.fruitsVeggiesProgress,
                wholeGrainsProgress = uiState.wholeGrainsProgress,
                processedMeatProgress = uiState.processedMeatProgress,
                activeGoal = uiState.activeGoal
            )

            // Middle Grid Row: Recent Analysis, Nutrition Score, Healthy Streak
            MiddleGridSection(
                recentAnalysis = uiState.recentAnalysis,
                nutritionScore = uiState.nutritionScore,
                scoreStatus = uiState.nutritionScoreStatus,
                streakDays = uiState.healthyStreakDays,
                isCancerAware = uiState.activeGoal == com.example.model.NutritionGoal.CANCER_AWARE,
                onViewFullAnalysis = { onNavigate(Screen.FoodAnalysisResult.route) }
            )

            // Water Intake Card
            WaterIntakeSection(
                currentLiters = uiState.waterIntakeLiters,
                targetLiters = uiState.waterTargetLiters,
                isCancerAware = uiState.activeGoal == com.example.model.NutritionGoal.CANCER_AWARE,
                onAddWater = { viewModel.addWaterIntake() },
                onOpenAddWaterSheet = { showAddWaterSheet = true }
            )

            // Quick Actions Section
            QuickActionsSection(
                onScanFood = { onNavigate(Screen.FoodAnalysis.route) },
                onSearchFood = { onNavigate(Screen.AiProcessing.route) },
                onVoiceInput = { onNavigate(Screen.AiProcessing.route) },
                onAddMeal = { onNavigate(Screen.AiProcessing.route) },
                isCancerAware = uiState.activeGoal == com.example.model.NutritionGoal.CANCER_AWARE
            )

            // Today's Meals Section
            TodayMealsSection(
                meals = uiState.meals,
                onViewAll = { onNavigate(Screen.History.route) },
                isCancerAware = uiState.activeGoal == com.example.model.NutritionGoal.CANCER_AWARE
            )

            // AI Insight Card
            AiInsightSection(
                insightText = uiState.aiInsightText,
                isCancerAware = uiState.activeGoal == com.example.model.NutritionGoal.CANCER_AWARE
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
}

@Composable
private fun HomeHeaderSection(
    greeting: String,
    subtitle: String,
    notificationCount: Int,
    onMenuClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("home_header"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("btn_menu")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = PrimaryTextColor,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = PrimaryTextColor
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        color = SecondaryTextColor
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Notification Bell with Badge
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("btn_notifications")
            ) {
                BadgedBox(
                    badge = {
                        if (notificationCount > 0) {
                            Badge(
                                containerColor = RedAccent,
                                contentColor = Color.White
                            ) {
                                Text(text = notificationCount.toString(), fontSize = 11.sp)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = PrimaryTextColor,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            // User Profile Avatar
            Surface(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onProfileClick)
                    .testTag("btn_profile_avatar"),
                color = PrimaryPurple.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, PrimaryPurple)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = PrimaryPurple,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GoalBannerSection(
    title: String,
    description: String,
    isCancerAware: Boolean = false,
    onSwitchGoal: () -> Unit,
    onLearnMore: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_goal_banner"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFF3F0FF),
                            Color(0xFFEBE5FF)
                        )
                    )
                )
                .border(1.dp, PurpleBorder, RoundedCornerShape(22.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top Tag + Switch Goal Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = PrimaryPurple,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text(
                            text = "YOUR GOAL",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryPurple,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }

                    // Switch Goal Pill Button
                    Surface(
                        onClick = onSwitchGoal,
                        shape = RoundedCornerShape(50),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier.testTag("btn_switch_goal")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = PrimaryPurple,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "Switch Goal",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryPurple,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }

                // Main Goal Title & Description
                Column(modifier = Modifier.fillMaxWidth(0.65f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryTextColor,
                            fontSize = 22.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = SecondaryTextColor,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Learn More Button
                Button(
                    onClick = onLearnMore,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryPurple,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("btn_learn_more")
                ) {
                    Text(
                        text = "Learn More",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Purple Ribbon Vector Illustration Canvas Overlay
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(125.dp)
                    .padding(end = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    if (isCancerAware) {
                        // Soft background glow
                        drawCircle(
                            color = Color(0xFFDDD6FE).copy(alpha = 0.5f),
                            radius = w * 0.45f,
                            center = Offset(w * 0.5f, h * 0.5f)
                        )

                        // Cancer-awareness ribbon: two crossed loops meeting at a point,
                        // drawn as filled paths so it reads as a real ribbon, not a badge.
                        val ribbonColor = Color(0xFF7C3AED)
                        val cx = w * 0.5f
                        val cy = h * 0.42f
                        val loopW = w * 0.30f
                        val loopH = h * 0.34f
                        val tailLen = h * 0.30f
                        val tailW = w * 0.11f

                        // Left loop (leans right, crossing down to the point)
                        val leftLoop = androidx.compose.ui.graphics.Path().apply {
                            moveTo(cx, cy)
                            cubicTo(
                                cx - loopW, cy - loopH * 0.9f,
                                cx - loopW * 1.15f, cy + loopH * 0.55f,
                                cx, cy
                            )
                            close()
                        }
                        // Right loop (leans left, crossing down to the point)
                        val rightLoop = androidx.compose.ui.graphics.Path().apply {
                            moveTo(cx, cy)
                            cubicTo(
                                cx + loopW, cy - loopH * 0.9f,
                                cx + loopW * 1.15f, cy + loopH * 0.55f,
                                cx, cy
                            )
                            close()
                        }
                        // Tail hanging below the crossing point
                        val tail = androidx.compose.ui.graphics.Path().apply {
                            moveTo(cx - tailW * 0.5f, cy)
                            lineTo(cx + tailW * 0.5f, cy)
                            lineTo(cx + tailW * 0.35f, cy + tailLen)
                            lineTo(cx, cy + tailLen - tailW * 0.4f)
                            lineTo(cx - tailW * 0.35f, cy + tailLen)
                            close()
                        }

                        drawPath(tail, color = ribbonColor.copy(alpha = 0.85f))
                        drawPath(leftLoop, color = ribbonColor)
                        drawPath(rightLoop, color = ribbonColor.copy(alpha = 0.92f))

                        // Small highlight at the crossing point for depth
                        drawCircle(
                            color = Color(0xFFF3F0FF).copy(alpha = 0.6f),
                            radius = w * 0.035f,
                            center = Offset(cx, cy)
                        )
                    } else {
                        // Soft background glow
                        drawCircle(
                            color = Color(0xFFDDD6FE).copy(alpha = 0.5f),
                            radius = w * 0.45f,
                            center = Offset(w * 0.5f, h * 0.5f)
                        )

                        // Purple Ribbon representation
                        drawCircle(
                            color = Color(0xFF8B5CF6).copy(alpha = 0.2f),
                            radius = w * 0.35f,
                            center = Offset(w * 0.5f, h * 0.4f)
                        )

                        // Ribbon Loop
                        drawRoundRect(
                            color = Color(0xFF7C3AED),
                            topLeft = Offset(w * 0.35f, h * 0.2f),
                            size = Size(w * 0.3f, h * 0.45f),
                            cornerRadius = CornerRadius(20f, 20f)
                        )
                        drawRoundRect(
                            color = Color(0xFFF3F0FF),
                            topLeft = Offset(w * 0.42f, h * 0.28f),
                            size = Size(w * 0.16f, h * 0.25f),
                            cornerRadius = CornerRadius(12f, 12f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TodayProgressSection(
    progressPercentage: Int,
    fiberProgress: NutrientProgress,
    fruitsVeggiesProgress: NutrientProgress,
    wholeGrainsProgress: NutrientProgress,
    processedMeatProgress: NutrientProgress,
    activeGoal: com.example.model.NutritionGoal = com.example.model.NutritionGoal.WEIGHT_LOSS
) {
    val useSpacedUnits = activeGoal == com.example.model.NutritionGoal.CANCER_AWARE
    val isCancerAwareGoal = activeGoal == com.example.model.NutritionGoal.CANCER_AWARE
    val fiberColor = if (isCancerAwareGoal) CancerFiberGreen else GreenAccent
    val fruitsVeggiesColor = if (isCancerAwareGoal) CancerFruitsVegBlue else GreenAccent
    val wholeGrainsColor = if (isCancerAwareGoal) CancerWholeGrainAmber else AmberAccent
    val processedMeatColor = if (isCancerAwareGoal) CancerProcessedMeatRed else RedAccent
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("section_todays_progress"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Progress",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTextColor,
                    fontSize = 18.sp
                )
            )
            Text(
                text = "$progressPercentage% Complete",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryPurple,
                    fontSize = 14.sp
                )
            )
        }

        // 4 Circular Nutrient Progress Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NutrientRingCard(
                progress = fiberProgress,
                color = fiberColor,
                icon = Icons.Outlined.LocalFlorist,
                useSpacedUnits = useSpacedUnits,
                modifier = Modifier.weight(1f)
            )
            NutrientRingCard(
                progress = fruitsVeggiesProgress,
                color = fruitsVeggiesColor,
                icon = Icons.Outlined.Egg,
                useSpacedUnits = useSpacedUnits,
                modifier = Modifier.weight(1f)
            )
            NutrientRingCard(
                progress = wholeGrainsProgress,
                color = wholeGrainsColor,
                icon = Icons.Outlined.Grain,
                useSpacedUnits = useSpacedUnits,
                modifier = Modifier.weight(1f)
            )
            NutrientRingCard(
                progress = processedMeatProgress,
                color = processedMeatColor,
                icon = Icons.Outlined.Restaurant,
                useSpacedUnits = useSpacedUnits,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun NutrientRingCard(
    progress: NutrientProgress,
    color: Color,
    icon: ImageVector,
    useSpacedUnits: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.testTag("card_nutrient_${progress.name.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Circular Ring with Center Icon & Badge
            Box(
                modifier = Modifier.size(54.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { (progress.current / progress.target).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxSize(),
                    color = color,
                    trackColor = color.copy(alpha = 0.15f),
                    strokeWidth = 5.dp,
                    strokeCap = StrokeCap.Round
                )
                Icon(
                    imageVector = icon,
                    contentDescription = progress.name,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )

                if (progress.isAchieved) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(18.dp)
                            .background(color, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Achieved",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                } else if (progress.isWarning) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(18.dp)
                            .background(RedAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "!",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Text(
                text = progress.name,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = PrimaryTextColor
                ),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            val currentText = if (progress.current % 1.0 == 0.0) progress.current.toInt().toString() else progress.current.toString()
            val targetText = progress.target.toInt().toString()
            val valueText = if (useSpacedUnits) {
                "$currentText ${progress.unit} / $targetText ${progress.unit}"
            } else {
                "$currentText${progress.unit} / $targetText${progress.unit}"
            }
            Text(
                text = valueText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = if (progress.isWarning) RedAccent else SecondaryTextColor,
                    fontWeight = if (progress.isWarning) FontWeight.Bold else FontWeight.Normal
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MiddleGridSection(
    recentAnalysis: RecentFoodAnalysisData,
    nutritionScore: Int,
    scoreStatus: String,
    streakDays: Int,
    isCancerAware: Boolean = false,
    onViewFullAnalysis: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isCancerAware) Modifier.height(androidx.compose.foundation.layout.IntrinsicSize.Min) else Modifier)
            .testTag("section_middle_grid"),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Card 1: Recent Food Analysis
        Card(
            onClick = onViewFullAnalysis,
            modifier = Modifier
                .weight(if (isCancerAware) 1.5f else 1.35f)
                .then(if (isCancerAware) Modifier.fillMaxHeight() else Modifier)
                .testTag("card_recent_analysis"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Food Analysis",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryTextColor,
                            fontSize = 13.sp
                        )
                    )
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = PrimaryPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.clickable { onViewFullAnalysis() }
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(if (isCancerAware) 8.dp else 10.dp)
                ) {
                    // Bacon image canvas illustration
                    Surface(
                        modifier = Modifier
                            .size(if (isCancerAware) 42.dp else 54.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        color = Color(0xFF78350F)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRoundRect(
                                color = Color(0xFFB91C1C),
                                topLeft = Offset(4f, 12f),
                                size = Size(size.width - 8f, size.height * 0.32f),
                                cornerRadius = CornerRadius(6f, 6f)
                            )
                            drawRoundRect(
                                color = Color(0xFF991B1B),
                                topLeft = Offset(4f, 32f),
                                size = Size(size.width - 8f, size.height * 0.32f),
                                cornerRadius = CornerRadius(6f, 6f)
                            )
                        }
                    }

                    if (isCancerAware) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = recentAnalysis.title,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = PrimaryTextColor
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFFF7ED)
                            ) {
                                Text(
                                    text = "${recentAnalysis.score}/100",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = AmberAccent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Clip,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    } else {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = recentAnalysis.title,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = PrimaryTextColor
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFFF7ED)
                            ) {
                                Text(
                                    text = "${recentAnalysis.score}/100",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = AmberAccent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFFBEB), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = AmberAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = recentAnalysis.alertMessage,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Text(
                    text = recentAnalysis.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = SecondaryTextColor,
                        lineHeight = 15.sp
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .testTag("btn_view_full_analysis")
                        .clickable { onViewFullAnalysis() }
                ) {
                    Text(
                        text = "View Full Analysis",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = PrimaryPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = PrimaryPurple,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // Card 2: Nutrition Score (~25% Width)
        Card(
            modifier = Modifier
                .weight(1f)
                .then(if (isCancerAware) Modifier.fillMaxHeight() else Modifier)
                .testTag("card_nutrition_score"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Nutrition Score",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryTextColor,
                            fontSize = 11.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Info",
                        tint = SecondaryTextColor,
                        modifier = Modifier.size(13.dp)
                    )
                }

                Box(
                    modifier = Modifier.size(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { (nutritionScore / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxSize(),
                        color = PrimaryPurple,
                        trackColor = PrimaryPurple.copy(alpha = 0.15f),
                        strokeWidth = 6.dp,
                        strokeCap = StrokeCap.Round
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = nutritionScore.toString(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryTextColor,
                                fontSize = 20.sp
                            )
                        )
                        Text(
                            text = "/100",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                color = SecondaryTextColor
                            )
                        )
                    }
                }

                Text(
                    text = scoreStatus,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryPurple,
                        fontSize = 13.sp
                    )
                )
            }
        }

        // Card 3: Healthy Streak (~25% Width)
        Card(
            modifier = Modifier
                .weight(1f)
                .then(if (isCancerAware) Modifier.fillMaxHeight() else Modifier)
                .testTag("card_healthy_streak"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Healthy Streak",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTextColor,
                        fontSize = 11.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFFF7ED),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = Color(0xFFEA580C),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Text(
                    text = "$streakDays",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTextColor,
                        fontSize = 22.sp
                    )
                )

                Text(
                    text = "Days",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SecondaryTextColor,
                        fontSize = 11.sp
                    )
                )

                Text(
                    text = "Keep it up!",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SecondaryTextColor,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun WaterIntakeSection(
    currentLiters: Double,
    targetLiters: Double,
    isCancerAware: Boolean = false,
    onAddWater: () -> Unit,
    onOpenAddWaterSheet: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_water_intake"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = WaterBlue.copy(alpha = 0.12f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "Water",
                            tint = WaterBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    if (isCancerAware) {
                        Text(
                            text = "Water Intake",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryTextColor,
                                fontSize = 15.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$currentLiters L / $targetLiters L",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = WaterBlue,
                                fontSize = 14.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Water Intake",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryTextColor,
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = "${currentLiters}L / ${targetLiters}L",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = WaterBlue,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { (currentLiters / targetLiters).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = WaterBlue,
                        trackColor = WaterBlue.copy(alpha = 0.15f),
                        strokeCap = StrokeCap.Round
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Add Water Button
            Surface(
                onClick = if (isCancerAware) {
                    { onOpenAddWaterSheet?.invoke() }
                } else {
                    onAddWater
                },
                shape = RoundedCornerShape(50),
                color = WaterBlue.copy(alpha = 0.12f),
                modifier = Modifier.testTag("btn_add_water")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "🥤", fontSize = 16.sp)
                    Text(
                        text = "+ Add",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = WaterBlue,
                            fontSize = 13.sp
                        )
                    )
                }
            }
        }
    }
}

// Cancer-Aware-only Water Intake feature: preset amount picker.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddWaterBottomSheet(
    onPresetSelected: (Double) -> Unit,
    onManualSelected: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val presets = listOf(100.0, 200.0, 250.0, 300.0)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = Modifier.testTag("add_water_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Add Water",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTextColor,
                    fontSize = 18.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            presets.forEach { amountMl ->
                Surface(
                    onClick = { onPresetSelected(amountMl) },
                    shape = RoundedCornerShape(14.dp),
                    color = LightPurpleBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, PurpleBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .testTag("water_preset_${amountMl.toInt()}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = WaterBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "${amountMl.toInt()} ml",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryTextColor,
                                fontSize = 15.sp
                            )
                        )
                    }
                }
            }

            Surface(
                onClick = onManualSelected,
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, PurpleBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .testTag("water_preset_manual")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = PrimaryPurple,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Manual",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryPurple,
                            fontSize = 15.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// Cancer-Aware-only Water Intake feature: manual ml entry.
@Composable
private fun ManualWaterInputDialog(
    onConfirm: (Double) -> Unit,
    onDismissRequest: () -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    val amountMl = amountText.toDoubleOrNull()
    val isValid = amountMl != null && amountMl > 0.0

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.testTag("manual_water_dialog"),
        title = {
            Text(
                text = "Enter water amount",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTextColor
                )
            )
        },
        text = {
            OutlinedTextField(
                value = amountText,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) amountText = input
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                suffix = { Text("ml") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("manual_water_input")
            )
        },
        confirmButton = {
            Button(
                onClick = { amountMl?.let(onConfirm) },
                enabled = isValid,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                modifier = Modifier.testTag("btn_manual_water_add")
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = SecondaryTextColor
                ),
                elevation = null
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun QuickActionsSection(
    onScanFood: () -> Unit,
    onSearchFood: () -> Unit,
    onVoiceInput: () -> Unit,
    onAddMeal: () -> Unit,
    isCancerAware: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("section_quick_actions"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = PrimaryTextColor,
                fontSize = 18.sp
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(if (isCancerAware) 10.dp else 8.dp)
        ) {
            QuickActionTile(
                title = "Scan Food",
                subtitle = "Analyze instantly",
                icon = Icons.Default.QrCodeScanner,
                onClick = onScanFood,
                modifier = Modifier.weight(1f),
                isCancerAware = isCancerAware
            )
            QuickActionTile(
                title = "Search Food",
                subtitle = "Find healthy foods",
                icon = Icons.Default.Search,
                onClick = onSearchFood,
                modifier = Modifier.weight(1f),
                isCancerAware = isCancerAware
            )
            QuickActionTile(
                title = "Voice Input",
                subtitle = "Speak to add",
                icon = Icons.Default.Mic,
                onClick = onVoiceInput,
                modifier = Modifier.weight(1f),
                isCancerAware = isCancerAware
            )
            QuickActionTile(
                title = "Add Meal",
                subtitle = "Log your meal",
                icon = Icons.Default.Add,
                onClick = onAddMeal,
                modifier = Modifier.weight(1f),
                isCancerAware = isCancerAware
            )
        }
    }
}

@Composable
private fun QuickActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCancerAware: Boolean = false
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .testTag("action_${title.lowercase().replace(" ", "_")}")
            .then(if (isCancerAware) Modifier.heightIn(min = 128.dp) else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LightPurpleBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, PurpleBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isCancerAware) Modifier.fillMaxHeight() else Modifier
                )
                .padding(
                    vertical = if (isCancerAware) 12.dp else 14.dp,
                    horizontal = if (isCancerAware) 8.dp else 4.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (isCancerAware)
                Arrangement.spacedBy(6.dp, Alignment.CenterVertically)
            else
                Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = PrimaryPurple,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = PrimaryTextColor
                ),
                textAlign = TextAlign.Center,
                maxLines = if (isCancerAware) 2 else 1
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.sp,
                    color = SecondaryTextColor,
                    lineHeight = if (isCancerAware) 12.sp else MaterialTheme.typography.bodySmall.lineHeight
                ),
                textAlign = TextAlign.Center,
                maxLines = if (isCancerAware) 2 else 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TodayMealsSection(
    meals: List<HomeMealItem>,
    onViewAll: () -> Unit,
    isCancerAware: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("section_todays_meals"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Meals",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTextColor,
                    fontSize = 18.sp
                )
            )
            if (!isCancerAware) {
                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryPurple,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.clickable { onViewAll() }
                )
            }
        }

        if (isCancerAware) {
            // Cancer-Aware: fixed-size Breakfast / Lunch / Dinner cards in a swipeable pager,
            // so exactly one card is emphasized at a time with a clear "more available" affordance.
            val cancerAwareMeals = meals.filter {
                it.category.equals("Breakfast", ignoreCase = true) ||
                    it.category.equals("Lunch", ignoreCase = true) ||
                    it.category.equals("Dinner", ignoreCase = true)
            }
            val pagerState = rememberPagerState(pageCount = { cancerAwareMeals.size })
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 32.dp),
                    pageSpacing = 14.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pager_todays_meals")
                ) { page ->
                    MealCardItem(meal = cancerAwareMeals[page], isCancerAware = true)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(cancerAwareMeals.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(if (isSelected) 8.dp else 6.dp)
                                .background(
                                    color = if (isSelected) PrimaryPurple else PurpleBorder,
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(meals) { meal ->
                    MealCardItem(meal = meal)
                }
            }
        }
    }
}

@Composable
private fun MealCardItem(meal: HomeMealItem, isCancerAware: Boolean = false) {
    Card(
        modifier = Modifier
            .width(if (isCancerAware) 210.dp else 155.dp)
            .then(if (isCancerAware) Modifier.height(240.dp) else Modifier)
            .testTag("card_meal_${meal.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Food Image Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isCancerAware) 110.dp else 90.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFEF3C7)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color(0xFFF59E0B).copy(alpha = 0.3f),
                        radius = size.width * 0.35f
                    )
                }
                Text(
                    text = when (meal.category.lowercase()) {
                        "breakfast" -> "🥣"
                        "lunch" -> "🥗"
                        "dinner" -> "🍣"
                        else -> "🥜"
                    },
                    fontSize = 42.sp
                )
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = SecondaryTextColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = meal.category,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = SecondaryTextColor
                )
            )

            Text(
                text = meal.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = PrimaryTextColor
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(34.dp)
            )

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFDCFCE7)
            ) {
                Text(
                    text = "${meal.calories} kcal",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF15803D),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun AiInsightSection(
    insightText: String,
    isCancerAware: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_ai_insight"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            LightPurpleBg,
                            Color(0xFFEDE9FE)
                        )
                    )
                )
                .border(1.dp, PurpleBorder, RoundedCornerShape(20.dp))
                .padding(if (isCancerAware) 20.dp else 18.dp)
        ) {
            if (isCancerAware) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = PrimaryPurple,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.AutoAwesome,
                                    contentDescription = "AI Insight",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Text(
                            text = "AI Insight",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryPurple,
                                fontSize = 17.sp
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = insightText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = PrimaryTextColor,
                                fontSize = 13.5.sp,
                                lineHeight = 19.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "🥗", fontSize = 30.sp)
                            }
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = PrimaryPurple,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.AutoAwesome,
                                contentDescription = "AI Insight",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI Insight",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryPurple,
                                fontSize = 16.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = insightText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = PrimaryTextColor,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        )
                    }

                    Text(text = "🥗", fontSize = 40.sp)
                }
            }
        }
    }
}
