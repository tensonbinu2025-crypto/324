package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FootballEliteMainApp(viewModel: AppViewModel) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val dailyRecord by viewModel.dailyRecord.collectAsStateWithLifecycle()
    val allDailyRecords by viewModel.allDailyRecords.collectAsStateWithLifecycle()
    val allGoals by viewModel.allGoals.collectAsStateWithLifecycle()
    val routineItems by viewModel.allRoutineItems.collectAsStateWithLifecycle()
    val todayWorkoutLogs by viewModel.todayWorkoutLogs.collectAsStateWithLifecycle()
    val allWorkoutLogs by viewModel.allWorkoutLogs.collectAsStateWithLifecycle()
    val currentQuote by viewModel.currentQuote.collectAsStateWithLifecycle()
    val selectedDate by viewModel.currentDate.collectAsStateWithLifecycle()

    // Retrieve system colors based on customized theme setting
    val themeColor = when (userProfile.selectedTheme) {
        "Athletic Neon" -> Color(0xFF39FF14)
        "Midnight Blue" -> Color(0xFF3B82F6)
        "Sunset Gold" -> Color(0xFFFBBF24)
        else -> Color(0xFF00FF66) // "Grass Green"
    }

    var selectedTab by remember { mutableStateOf("home") }

    Scaffold(
        bottomBar = {
            ImmersiveBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                themeColor = themeColor
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF000000))
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "TabTransition"
            ) { targetTab ->
                when (targetTab) {
                    "home" -> HomeScreen(
                        viewModel = viewModel,
                        userProfile = userProfile,
                        dailyRecord = dailyRecord,
                        allGoals = allGoals,
                        routineItems = routineItems,
                        currentQuote = currentQuote,
                        themeColor = themeColor,
                        onNavigateToTab = { selectedTab = it }
                    )
                    "training" -> TrainingScreen(
                        viewModel = viewModel,
                        todayWorkoutLogs = todayWorkoutLogs,
                        themeColor = themeColor
                    )
                    "routine" -> RoutineScreen(
                        viewModel = viewModel,
                        routineItems = routineItems,
                        themeColor = themeColor
                    )
                    "trackers" -> TrackersScreen(
                        viewModel = viewModel,
                        dailyRecord = dailyRecord,
                        themeColor = themeColor
                    )
                    "progress" -> StatsAndSettingsScreen(
                        viewModel = viewModel,
                        dailyRecord = dailyRecord,
                        allDailyRecords = allDailyRecords,
                        userProfile = userProfile,
                        themeColor = themeColor
                    )
                }
            }
        }
    }
}

@Composable
fun ImmersiveBottomBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    themeColor: Color
) {
    Surface(
        color = Color(0xFF09090B),
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = Color.White.copy(alpha = 0.05f),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFF18181B).copy(alpha = 0.85f))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val tabs = listOf(
                    "home" to "🏠",
                    "training" to "⚽",
                    "routine" to "💪",
                    "trackers" to "🥛",
                    "progress" to "📈"
                )
                tabs.forEach { (tabId, iconSymbol) ->
                    val isSelected = selectedTab == tabId
                    IconButton(
                        onClick = { onTabSelected(tabId) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = iconSymbol,
                                fontSize = 20.sp,
                                modifier = Modifier.alpha(if (isSelected) 1f else 0.5f)
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(themeColor)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 🏠 HOME SCREEN
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    userProfile: UserProfile,
    dailyRecord: DailyRecord,
    allGoals: List<Goal>,
    routineItems: List<RoutineItem>,
    currentQuote: Quote,
    themeColor: Color,
    onNavigateToTab: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    var newGoalText by remember { mutableStateOf("") }
    var notesText by remember { mutableStateOf(dailyRecord.notes) }

    // Sync note text when dailyRecord notes loads or changes
    LaunchedEffect(dailyRecord.notes) {
        if (dailyRecord.notes != notesText) {
            notesText = dailyRecord.notes
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header directly from HTML Design
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Football Elite",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF4ADE80),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    )
                    Text(
                        text = "Welcome back, Alex",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color(0xFFF1F5F9),
                            fontWeight = FontWeight.Medium,
                            fontSize = 22.sp
                        )
                    )
                }
                
                // ⚡ Avatar Badge
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF27272A))
                        .border(1.dp, Color(0xFF22C55E).copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚡", fontSize = 18.sp)
                }
            }
        }

        // Today's Focus Card with Gradient Glow Ring
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Glow Background Blur Layer
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF16A34A), Color(0xFF34D399))
                            ),
                            alpha = 0.25f
                        )
                )

                // Actual focus card content
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF22C55E).copy(alpha = 0.1f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Today's Training",
                                    color = Color(0xFF4ADE80),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = "45 mins left",
                                color = Color(0xFF71717A),
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Ball Mastery II",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                letterSpacing = (-0.5).sp,
                                fontSize = 24.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Master the low-drill and agility turns.",
                            color = Color(0xFFA1A1AA),
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { onNavigateToTab("training") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                            ) {
                                Text(
                                    text = "Start Training",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF27272A))
                                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                                    .clickable { onNavigateToTab("training") },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("⏱️", fontSize = 20.sp)
                            }
                        }
                    }
                }
            }
        }

        // Vitals Split Grid
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Water Tracker Card
                val waterProgress = (dailyRecord.waterMl / 4000f).coerceIn(0f, 1f)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Water Intake",
                                color = Color(0xFFA1A1AA),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${(waterProgress * 100).toInt()}%",
                                color = Color(0xFF60A5FA),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Custom visual glass water column inside the card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF27272A))
                                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                        ) {
                            // Blue fill
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(waterProgress)
                                    .align(Alignment.BottomCenter)
                                    .background(Color(0xFF3B82F6).copy(alpha = 0.4f))
                            ) {
                                // Top highlighted border line of the water column
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .background(Color(0xFF60A5FA))
                                        .align(Alignment.TopCenter)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Button(
                                onClick = { viewModel.addWater(250) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27272A)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(28.dp)
                            ) {
                                Text("+250ml", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { viewModel.addWater(500) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27272A)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(28.dp)
                            ) {
                                Text("+500ml", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // 2. Rest & Recovery Sleep Card
                val sleepProgress = (dailyRecord.sleepScore / 100f).coerceIn(0f, 1f)
                val sleepHrs = dailyRecord.sleepMinutes / 60f
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Rest & Recovery",
                            color = Color(0xFFA1A1AA),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize().padding(6.dp)) {
                                drawCircle(
                                    color = Color(0xFF27272A),
                                    radius = size.minDimension / 2,
                                    style = Stroke(width = 4.dp.toPx())
                                )
                                drawArc(
                                    color = Color(0xFF22C55E),
                                    startAngle = -90f,
                                    sweepAngle = 360f * sleepProgress,
                                    useCenter = false,
                                    style = Stroke(width = 4.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = String.format("%.1fh", sleepHrs),
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Optimum",
                                    color = Color(0xFF71717A),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.2).sp
                                )
                            }
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Score: ${dailyRecord.sleepScore}/100",
                                color = Color.Gray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Next Scheduled Task Row
        item {
            val nextRoutine = routineItems.firstOrNull { !it.isCompleted }
            val nextTime = nextRoutine?.time ?: "16:30"
            val nextTask = nextRoutine?.task ?: "Afternoon Snack & Routine"

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B).copy(alpha = 0.4f)),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
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
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(44.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFF22C55E))
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Up Next • $nextTime",
                                color = Color(0xFF71717A),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = nextTask,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF27272A))
                            .clickable { onNavigateToTab("routine") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔔", fontSize = 14.sp)
                    }
                }
            }
        }

        // Soccer Pitch Banner + Streak Overlay (Original components preserved cleanly!)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, Color(0xFF222222), RoundedCornerShape(24.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.soccer_pitch_banner),
                    contentDescription = "Soccer Pitch Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.95f))
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "FOOTBALL STREAK",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = themeColor,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp
                            )
                        )
                        Text(
                            text = "Elite Performance Log",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Row(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .border(1.dp, themeColor, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalFireDepartment,
                            contentDescription = "Streak Flame",
                            tint = Color(0xFFFF5722),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${userProfile.streak} Day Streak",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        // Goals Feature
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "🏆 Football Goals & Milestones",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newGoalText,
                            onValueChange = { newGoalText = it },
                            placeholder = { Text("E.g., Master 100 juggles", color = Color.Gray, fontSize = 13.sp) },
                            textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 13.sp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeColor,
                                unfocusedBorderColor = Color(0xFF333333),
                                focusedContainerColor = Color(0xFF09090B),
                                unfocusedContainerColor = Color(0xFF09090B)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.addGoal(newGoalText)
                                newGoalText = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(50.dp)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Add Goal", tint = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (allGoals.isEmpty()) {
                        Text(
                            text = "No active goals. Set your target milestone!",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        allGoals.forEach { goal ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Checkbox(
                                        checked = goal.isCompleted,
                                        onCheckedChange = { viewModel.toggleGoal(goal) },
                                        colors = CheckboxDefaults.colors(checkedColor = themeColor)
                                    )
                                    Text(
                                        text = goal.title,
                                        color = if (goal.isCompleted) Color.Gray else Color.White,
                                        fontSize = 13.sp,
                                        style = if (goal.isCompleted) LocalTextStyle.current.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough) else LocalTextStyle.current
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.deleteGoal(goal) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Goal",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Daily Journal Notes
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "📝 Training Notes & Reflections",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Reflect on today's performance, touch, or weak foot progress.",
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = notesText,
                        onValueChange = {
                            notesText = it
                            viewModel.saveNotes(it)
                        },
                        placeholder = { Text("How did your training feel today? Any wins or tactical insights?", color = Color.Gray, fontSize = 12.sp) },
                        textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 13.sp),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = themeColor,
                            unfocusedBorderColor = Color(0xFF333333),
                            focusedContainerColor = Color(0xFF09090B),
                            unfocusedContainerColor = Color(0xFF09090B)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Auto-saves to local database.",
                        color = Color.DarkGray,
                        fontSize = 10.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Centered Daily Quote Section directly from HTML
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "\"${currentQuote.text}\"",
                    color = Color(0xFF71717A),
                    fontSize = 12.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(112.dp))
        }
    }
}

// -------------------------------------------------------------
// ⚽ FOOTBALL & HOME WORKOUT TRAINING SCREEN
// -------------------------------------------------------------
data class FootballDrill(
    val name: String,
    val instructions: String,
    val defaultDurationSeconds: Int
)

data class HomeWorkoutItem(
    val name: String,
    val sets: Int,
    val reps: String,
    val defaultDurationSeconds: Int
)

@Composable
fun TrainingScreen(
    viewModel: AppViewModel,
    todayWorkoutLogs: List<WorkoutLog>,
    themeColor: Color
) {
    var activeSubTab by remember { mutableStateOf("football") } // "football", "homeworkout", "stopwatch", "timer"

    val footballDrills = listOf(
        FootballDrill("Ball Mastery", "Juggle, roll overs, toe taps, and inside-outside cuts using both feet. Focus on rapid touch counts.", 300),
        FootballDrill("Passing", "Wall passes (15m, 10m, 5m), dynamic one-touch rebounds, weak-foot specific passing against wall.", 240),
        FootballDrill("Dribbling", "Cone slalom zig-zag drills. Do inside foot only, outside foot only, and sole rolls.", 300),
        FootballDrill("Shooting", "Approach, plant foot alignment, and striking with laces. Focus on precision and body lean angles.", 180),
        FootballDrill("Speed", "30-meter maximal sprints, deceleration hops, and sudden accelerations with recovery walks.", 180),
        FootballDrill("Agility", "L-drills, shuttle runs, high-knees, and fast ladder footwork in tight spaces.", 240),
        FootballDrill("Recovery", "Light dynamic jogging, foam rolling major muscle groups, and active static deep stretches.", 300)
    )

    val homeWorkouts = listOf(
        HomeWorkoutItem("Push-ups", 4, "15-20 reps", 60),
        HomeWorkoutItem("Squats", 4, "20 reps", 60),
        HomeWorkoutItem("Lunges", 3, "12 reps per leg", 90),
        HomeWorkoutItem("Plank", 3, "60 seconds hold", 60),
        HomeWorkoutItem("Jump Squats", 3, "15 reps", 60),
        HomeWorkoutItem("Calf Raises", 4, "25 reps", 60),
        HomeWorkoutItem("Stretching", 1, "Deep full-body stretches", 300)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Screen title
        Text(
            text = "ATHLETIC TRAINING CENTER",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Custom horizontal sub-tab selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF18181B), RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val tabs = listOf(
                "football" to "Football",
                "homeworkout" to "Workouts",
                "stopwatch" to "Stopwatch",
                "timer" to "Custom Timer"
            )
            tabs.forEach { (key, label) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (activeSubTab == key) themeColor else Color.Transparent)
                        .clickable { activeSubTab = key }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (activeSubTab == key) Color.Black else Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sub-Tab content
        Box(modifier = Modifier.weight(1f)) {
            when (activeSubTab) {
                "football" -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(footballDrills) { drill ->
                            val isLoggedToday = todayWorkoutLogs.any { it.workoutName == drill.name }
                            FootballDrillCard(
                                drill = drill,
                                isLoggedToday = isLoggedToday,
                                onComplete = { duration ->
                                    viewModel.logWorkout(drill.name, true, duration)
                                },
                                themeColor = themeColor
                            )
                        }
                    }
                }
                "homeworkout" -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(homeWorkouts) { workout ->
                            val isLoggedToday = todayWorkoutLogs.any { it.workoutName == workout.name }
                            HomeWorkoutCard(
                                workout = workout,
                                isLoggedToday = isLoggedToday,
                                onComplete = { duration ->
                                    viewModel.logWorkout(workout.name, false, duration)
                                },
                                themeColor = themeColor
                            )
                        }
                    }
                }
                "stopwatch" -> {
                    StopwatchPanel(themeColor = themeColor)
                }
                "timer" -> {
                    WorkoutTimerPanel(themeColor = themeColor)
                }
            }
        }
    }
}

@Composable
fun FootballDrillCard(
    drill: FootballDrill,
    isLoggedToday: Boolean,
    onComplete: (Int) -> Unit,
    themeColor: Color
) {
    var timerRunning by remember { mutableStateOf(false) }
    var timeRemaining by remember { mutableStateOf(drill.defaultDurationSeconds) }

    LaunchedEffect(timerRunning, timeRemaining) {
        if (timerRunning && timeRemaining > 0) {
            delay(1000)
            timeRemaining -= 1
        } else if (timeRemaining == 0) {
            timerRunning = false
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isLoggedToday) themeColor else Color.White.copy(alpha = 0.05f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = drill.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                if (isLoggedToday) {
                    Badge(
                        containerColor = themeColor.copy(alpha = 0.2f),
                        contentColor = themeColor,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text("COMPLETED TODAY", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = drill.instructions,
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Timer display
            val minutes = timeRemaining / 60
            val seconds = timeRemaining % 60
            val timeString = String.format("%02d:%02d", minutes, seconds)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0A0A0A), RoundedCornerShape(12.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = "Timer Icon",
                        tint = themeColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = timeString,
                        color = if (timeRemaining == 0) Color.Red else Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Start/Pause button
                    Button(
                        onClick = { timerRunning = !timerRunning },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (timerRunning) Color(0xFFCC3333) else themeColor
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = if (timerRunning) "Pause" else "Start",
                            color = if (timerRunning) Color.White else Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Reset button
                    OutlinedButton(
                        onClick = {
                            timerRunning = false
                            timeRemaining = drill.defaultDurationSeconds
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF333333)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Reset", fontSize = 11.sp)
                    }

                    // Complete Button
                    Button(
                        onClick = {
                            onComplete(drill.defaultDurationSeconds - timeRemaining)
                            timerRunning = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Complete", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun HomeWorkoutCard(
    workout: HomeWorkoutItem,
    isLoggedToday: Boolean,
    onComplete: (Int) -> Unit,
    themeColor: Color
) {
    var timerRunning by remember { mutableStateOf(false) }
    var timeRemaining by remember { mutableStateOf(workout.defaultDurationSeconds) }

    LaunchedEffect(timerRunning, timeRemaining) {
        if (timerRunning && timeRemaining > 0) {
            delay(1000)
            timeRemaining -= 1
        } else if (timeRemaining == 0) {
            timerRunning = false
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isLoggedToday) themeColor else Color.White.copy(alpha = 0.05f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = workout.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                if (isLoggedToday) {
                    Badge(
                        containerColor = themeColor.copy(alpha = 0.2f),
                        contentColor = themeColor
                    ) {
                        Text("COMPLETED TODAY", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.FitnessCenter,
                        contentDescription = "Sets Icon",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Sets: ${workout.sets}", color = Color.LightGray, fontSize = 13.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Autorenew,
                        contentDescription = "Reps Icon",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Reps: ${workout.reps}", color = Color.LightGray, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Timer display
            val minutes = timeRemaining / 60
            val seconds = timeRemaining % 60
            val timeString = String.format("%02d:%02d", minutes, seconds)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0A0A0A), RoundedCornerShape(12.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = "Timer Icon",
                        tint = themeColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = timeString,
                        color = if (timeRemaining == 0) Color.Red else Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { timerRunning = !timerRunning },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (timerRunning) Color(0xFFCC3333) else themeColor
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = if (timerRunning) "Pause" else "Start",
                            color = if (timerRunning) Color.Black else Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            timerRunning = false
                            timeRemaining = workout.defaultDurationSeconds
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF333333)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Reset", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            onComplete(workout.defaultDurationSeconds - timeRemaining)
                            timerRunning = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Complete", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StopwatchPanel(themeColor: Color) {
    var stopwatchTime by remember { mutableStateOf(0L) }
    var running by remember { mutableStateOf(false) }
    val lapList = remember { mutableStateListOf<String>() }

    LaunchedEffect(running) {
        if (running) {
            val startSystemTime = System.currentTimeMillis() - stopwatchTime
            while (running) {
                stopwatchTime = System.currentTimeMillis() - startSystemTime
                delay(10)
            }
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("FOOTBALL STOPWATCH", color = themeColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(16.dp))

            // Time text formatting
            val totalMsec = stopwatchTime
            val msec = (totalMsec % 1000) / 10
            val sec = (totalMsec / 1000) % 60
            val min = (totalMsec / 60000) % 60
            val timeText = String.format("%02d:%02d.%02d", min, sec, msec)

            Text(
                text = timeText,
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { running = !running },
                    colors = ButtonDefaults.buttonColors(containerColor = if (running) Color(0xFFEF4444) else themeColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (running) "Stop" else "Start", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                if (running) {
                    Button(
                        onClick = { lapList.add(0, timeText) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Lap", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            stopwatchTime = 0L
                            lapList.clear()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFF444444)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reset")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("LAPS", color = Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF0F0F0F), RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                if (lapList.isEmpty()) {
                    item {
                        Text(
                            "Record lap timings to analyze sprints or interval speed.",
                            color = Color.DarkGray,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                } else {
                    items(lapList) { lap ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Lap #${lapList.size - lapList.indexOf(lap)}", color = Color.Gray, fontSize = 13.sp)
                            Text(lap, color = themeColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutTimerPanel(themeColor: Color) {
    var timerSeconds by remember { mutableStateOf(60) }
    var currentTimerRemaining by remember { mutableStateOf(60) }
    var timerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(timerRunning, currentTimerRemaining) {
        if (timerRunning && currentTimerRemaining > 0) {
            delay(1000)
            currentTimerRemaining -= 1
        } else if (currentTimerRemaining == 0) {
            timerRunning = false
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("INTERVAL WORKOUT TIMER", color = themeColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(24.dp))

            // Animated Timer Circle
            val progress = if (timerSeconds > 0) (currentTimerRemaining.toFloat() / timerSeconds) else 0f

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(180.dp)
                    .drawBehind {
                        drawCircle(
                            color = Color(0xFF222222),
                            radius = size.minDimension / 2,
                            style = Stroke(width = 12.dp.toPx())
                        )
                        drawArc(
                            color = themeColor,
                            startAngle = -90f,
                            sweepAngle = 360f * progress,
                            useCenter = false,
                            style = Stroke(width = 12.dp.toPx())
                        )
                    }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val min = currentTimerRemaining / 60
                    val sec = currentTimerRemaining % 60
                    Text(
                        text = String.format("%02d:%02d", min, sec),
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text("remaining", color = Color.Gray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Pre-defined interval speeds
            Text("Presets", color = Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "30s" to 30,
                    "1 Min" to 60,
                    "2 Min" to 120,
                    "5 Min" to 300
                ).forEach { (label, duration) ->
                    OutlinedButton(
                        onClick = {
                            timerRunning = false
                            timerSeconds = duration
                            currentTimerRemaining = duration
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, if (timerSeconds == duration) themeColor else Color(0xFF333333)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(label, fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { timerRunning = !timerRunning },
                    colors = ButtonDefaults.buttonColors(containerColor = if (timerRunning) Color(0xFFEF4444) else themeColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (timerRunning) "Pause" else "Start", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        timerRunning = false
                        currentTimerRemaining = timerSeconds
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 📅 DAILY ROUTINE SCREEN
// -------------------------------------------------------------
@Composable
fun RoutineScreen(
    viewModel: AppViewModel,
    routineItems: List<RoutineItem>,
    themeColor: Color
) {
    val totalCount = routineItems.size
    val doneCount = routineItems.count { it.isCompleted }
    val progress = if (totalCount > 0) (doneCount.toFloat() / totalCount) else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Screen title
        Text(
            text = "DAILY ROUTINE & DISCIPLINE",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Consistency builds champions. Keep your routine checked.",
            color = Color.Gray,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Combined Progress Info Panel
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circle Progress Indicator
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(72.dp)
                        .drawBehind {
                            drawCircle(
                                color = Color(0xFF222222),
                                radius = size.minDimension / 2,
                                style = Stroke(width = 6.dp.toPx())
                            )
                            drawArc(
                                color = themeColor,
                                startAngle = -90f,
                                sweepAngle = 360f * progress,
                                useCenter = false,
                                style = Stroke(width = 6.dp.toPx())
                            )
                        }
                ) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text("Today's Progression", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("$doneCount of $totalCount habits completed", color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (progress >= 1f) "Outstanding! 100% Elite Day!" else "Keep checking off habits!",
                        color = themeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Routine Habit List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(routineItems) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF18181B), RoundedCornerShape(12.dp))
                        .border(
                            1.dp,
                            if (item.isCompleted) themeColor.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Checkbox(
                            checked = item.isCompleted,
                            onCheckedChange = { viewModel.toggleRoutineItem(item) },
                            colors = CheckboxDefaults.colors(checkedColor = themeColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = item.time,
                                color = themeColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = item.task,
                                color = if (item.isCompleted) Color.Gray else Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                style = if (item.isCompleted) LocalTextStyle.current.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough) else LocalTextStyle.current
                            )
                        }
                    }

                    // Reminder toggle button
                    IconButton(
                        onClick = { viewModel.toggleRoutineItemReminder(item) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (item.reminderEnabled) Icons.Filled.NotificationsActive else Icons.Outlined.Notifications,
                            contentDescription = "Reminder Alert",
                            tint = if (item.reminderEnabled) themeColor else Color.DarkGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// -------------------------------------------------------------
// 🍳💧😴 TRACKERS SCREEN (WATER, SLEEP, DIET)
// -------------------------------------------------------------
@Composable
fun TrackersScreen(
    viewModel: AppViewModel,
    dailyRecord: DailyRecord,
    themeColor: Color
) {
    var trackerTab by remember { mutableStateOf("water") } // "water", "sleep", "diet"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Screen title
        Text(
            text = "NUTRITION & BIOMETRICS",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Tab Selector Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF18181B), RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf(
                "water" to "Water",
                "sleep" to "Sleep",
                "diet" to "Diet Log"
            ).forEach { (key, label) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (trackerTab == key) themeColor else Color.Transparent)
                        .clickable { trackerTab = key }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (trackerTab == key) Color.Black else Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.weight(1f)) {
            when (trackerTab) {
                "water" -> WaterTrackerPanel(viewModel, dailyRecord, themeColor)
                "sleep" -> SleepTrackerPanel(viewModel, dailyRecord, themeColor)
                "diet" -> DietTrackerPanel(viewModel, themeColor)
            }
        }
    }
}

@Composable
fun WaterTrackerPanel(
    viewModel: AppViewModel,
    dailyRecord: DailyRecord,
    themeColor: Color
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "DAILY HYDRATION TARGET",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Circular visual indicator
                val waterGoalMl = 4000f
                val waterProgress = (dailyRecord.waterMl / waterGoalMl).coerceAtMost(1f)

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(200.dp)
                        .drawBehind {
                            drawCircle(
                                color = Color(0xFF1F1F1F),
                                radius = size.minDimension / 2,
                                style = Stroke(width = 16.dp.toPx())
                            )
                            drawArc(
                                color = themeColor,
                                startAngle = -90f,
                                sweepAngle = 360f * waterProgress,
                                useCenter = false,
                                style = Stroke(width = 16.dp.toPx())
                            )
                        }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.LocalDrink,
                            contentDescription = "Water Cup",
                            tint = themeColor,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${dailyRecord.waterMl} ml",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "of 4.0 Litres",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                if (waterProgress >= 1f) {
                    Text(
                        "Goal Met! Ultimate cellular hydration achieved!",
                        color = themeColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Intake Quick Action Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Add Water Intake", color = Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "+250ml" to 250,
                    "+500ml" to 500,
                    "+1L" to 1000
                ).forEach { (label, amt) ->
                    Button(
                        onClick = { viewModel.addWater(amt) },
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text(label, color = Color.Black, fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                }
            }

            OutlinedButton(
                onClick = { viewModel.resetWater() },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Reset Water Intake", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SleepTrackerPanel(
    viewModel: AppViewModel,
    dailyRecord: DailyRecord,
    themeColor: Color
) {
    var bedTimeInput by remember { mutableStateOf(dailyRecord.bedTime) }
    var wakeTimeInput by remember { mutableStateOf(dailyRecord.wakeTime) }
    var scoreInput by remember { mutableFloatStateOf(dailyRecord.sleepScore.toFloat()) }

    LaunchedEffect(dailyRecord) {
        bedTimeInput = dailyRecord.bedTime
        wakeTimeInput = dailyRecord.wakeTime
        scoreInput = dailyRecord.sleepScore.toFloat()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("LAST SLEEP REPORT", color = themeColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    val hours = dailyRecord.sleepMinutes / 60
                    val mins = dailyRecord.sleepMinutes % 60

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${hours}h ${mins}m",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text("Total sleep duration", color = Color.Gray, fontSize = 12.sp)
                        }

                        // Score Ring
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(72.dp)
                                .drawBehind {
                                    drawCircle(
                                        color = Color(0xFF222222),
                                        radius = size.minDimension / 2,
                                        style = Stroke(width = 6.dp.toPx())
                                    )
                                    drawArc(
                                        color = themeColor,
                                        startAngle = -90f,
                                        sweepAngle = 360f * (dailyRecord.sleepScore / 100f),
                                        useCenter = false,
                                        style = Stroke(width = 6.dp.toPx())
                                    )
                                }
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${dailyRecord.sleepScore}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
                                Text("Score", color = Color.Gray, fontSize = 9.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Bedtime, contentDescription = "Bedtime", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Bed: ${dailyRecord.bedTime}", color = Color.LightGray, fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.WbSunny, contentDescription = "Wake", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Wake: ${dailyRecord.wakeTime}", color = Color.LightGray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("UPDATE SLEEP TIMES", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = bedTimeInput,
                            onValueChange = { bedTimeInput = it },
                            label = { Text("Bed Time (HH:MM)", fontSize = 11.sp, color = Color.Gray) },
                            textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 13.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeColor,
                                unfocusedBorderColor = Color(0xFF333333)
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = wakeTimeInput,
                            onValueChange = { wakeTimeInput = it },
                            label = { Text("Wake Time (HH:MM)", fontSize = 11.sp, color = Color.Gray) },
                            textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 13.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeColor,
                                unfocusedBorderColor = Color(0xFF333333)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Sleep Score: ${scoreInput.toInt()}/100", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Slider(
                        value = scoreInput,
                        onValueChange = { scoreInput = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = themeColor,
                            activeTrackColor = themeColor,
                            inactiveTrackColor = Color(0xFF333333)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.saveSleepDetails(bedTimeInput, wakeTimeInput, scoreInput.toInt())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Save Sleep Logs", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

data class MealLog(
    val name: String,
    val icon: String,
    var food: String,
    var calories: Int,
    var protein: Int,
    var carbs: Int,
    var fat: Int
)

@Composable
fun DietTrackerPanel(
    viewModel: AppViewModel,
    themeColor: Color
) {
    // Standard default meals
    val defaultMeals = remember {
        mutableStateListOf(
            MealLog("Breakfast", "🍳", "Oatmeal with honey, bananas, and 3 boiled eggs", 550, 30, 70, 15),
            MealLog("Lunch", "🍗", "Grilled chicken breast, brown rice, and steamed broccoli", 680, 52, 85, 12),
            MealLog("Dinner", "🐟", "Baked salmon fillet, sweet potato mash, and spinach salad", 610, 45, 60, 18),
            MealLog("Snacks", "🍌", "Whey protein shake, a banana, and a handful of almonds", 380, 28, 40, 10)
        )
    }

    var selectedMealToEdit by remember { mutableStateOf<MealLog?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("TOTAL DAILY INTAKE", color = themeColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    val totalKcal = defaultMeals.sumOf { it.calories }
                    val totalPro = defaultMeals.sumOf { it.protein }
                    val totalCarb = defaultMeals.sumOf { it.carbs }
                    val totalFat = defaultMeals.sumOf { it.fat }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("$totalKcal kcal", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                            Text("Estimated Energy", color = Color.Gray, fontSize = 12.sp)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("P: ${totalPro}g", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("C: ${totalCarb}g", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("F: ${totalFat}g", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        items(defaultMeals) { meal ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(meal.icon, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(meal.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(
                            onClick = { selectedMealToEdit = meal },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit Meal", tint = themeColor, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = meal.food, color = Color.LightGray, fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "${meal.calories} kcal" to Color.White,
                            "${meal.protein}g Pro" to themeColor,
                            "${meal.carbs}g Carb" to Color.White,
                            "${meal.fat}g Fat" to Color.Gray
                        ).forEach { (label, col) ->
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF0A0A0A), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(label, color = col, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Meal Dialog
    if (selectedMealToEdit != null) {
        val editingMeal = selectedMealToEdit!!
        var foodInput by remember { mutableStateOf(editingMeal.food) }
        var kcalInput by remember { mutableStateOf(editingMeal.calories.toString()) }
        var proInput by remember { mutableStateOf(editingMeal.protein.toString()) }
        var carbInput by remember { mutableStateOf(editingMeal.carbs.toString()) }
        var fatInput by remember { mutableStateOf(editingMeal.fat.toString()) }

        AlertDialog(
            onDismissRequest = { selectedMealToEdit = null },
            title = { Text("Log ${editingMeal.name}", color = Color.White) },
            containerColor = Color(0xFF18181B),
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = foodInput,
                        onValueChange = { foodInput = it },
                        label = { Text("Food details") },
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = themeColor,
                            unfocusedBorderColor = Color(0xFF333333)
                        )
                    )

                    OutlinedTextField(
                        value = kcalInput,
                        onValueChange = { kcalInput = it },
                        label = { Text("Calories (kcal)") },
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = themeColor,
                            unfocusedBorderColor = Color(0xFF333333)
                        )
                    )

                    OutlinedTextField(
                        value = proInput,
                        onValueChange = { proInput = it },
                        label = { Text("Protein (g)") },
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = themeColor,
                            unfocusedBorderColor = Color(0xFF333333)
                        )
                    )

                    OutlinedTextField(
                        value = carbInput,
                        onValueChange = { carbInput = it },
                        label = { Text("Carbohydrates (g)") },
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = themeColor,
                            unfocusedBorderColor = Color(0xFF333333)
                        )
                    )

                    OutlinedTextField(
                        value = fatInput,
                        onValueChange = { fatInput = it },
                        label = { Text("Fats (g)") },
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = themeColor,
                            unfocusedBorderColor = Color(0xFF333333)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updatedIdx = defaultMeals.indexOfFirst { it.name == editingMeal.name }
                        if (updatedIdx != -1) {
                            defaultMeals[updatedIdx] = editingMeal.copy(
                                food = foodInput,
                                calories = kcalInput.toIntOrNull() ?: editingMeal.calories,
                                protein = proInput.toIntOrNull() ?: editingMeal.protein,
                                carbs = carbInput.toIntOrNull() ?: editingMeal.carbs,
                                fat = fatInput.toIntOrNull() ?: editingMeal.fat
                            )
                        }
                        selectedMealToEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = themeColor)
                ) {
                    Text("Save Logs", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { selectedMealToEdit = null },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFF333333))
                ) {
                    Text("Dismiss")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// 📈 STATS & ⚙️ SETTINGS SCREEN
// -------------------------------------------------------------
@Composable
fun StatsAndSettingsScreen(
    viewModel: AppViewModel,
    dailyRecord: DailyRecord,
    allDailyRecords: List<DailyRecord>,
    userProfile: UserProfile,
    themeColor: Color
) {
    var statSettingsTab by remember { mutableStateOf("stats") } // "stats", "settings"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Screen title
        Text(
            text = "ANALYTICS & SYSTEMS",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Tab Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF18181B), RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf(
                "stats" to "Performance Stats",
                "settings" to "Settings"
            ).forEach { (key, label) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (statSettingsTab == key) themeColor else Color.Transparent)
                        .clickable { statSettingsTab = key }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (statSettingsTab == key) Color.Black else Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.weight(1f)) {
            when (statSettingsTab) {
                "stats" -> StatsPanel(viewModel, dailyRecord, allDailyRecords, themeColor)
                "settings" -> SettingsPanel(viewModel, userProfile, themeColor)
            }
        }
    }
}

@Composable
fun StatsPanel(
    viewModel: AppViewModel,
    dailyRecord: DailyRecord,
    allDailyRecords: List<DailyRecord>,
    themeColor: Color
) {
    // Inputs for stats
    var weightInput by remember { mutableStateOf(dailyRecord.weightKg.toString()) }
    var speedInput by remember { mutableStateOf(dailyRecord.sprintSpeedKmh.toString()) }
    var pushupsInput by remember { mutableStateOf(dailyRecord.pushupsCount.toString()) }
    var plankInput by remember { mutableStateOf(dailyRecord.plankSeconds.toString()) }
    var ratingInput by remember { mutableStateOf(dailyRecord.footballRating.toString()) }

    LaunchedEffect(dailyRecord) {
        weightInput = dailyRecord.weightKg.toString()
        speedInput = dailyRecord.sprintSpeedKmh.toString()
        pushupsInput = dailyRecord.pushupsCount.toString()
        plankInput = dailyRecord.plankSeconds.toString()
        ratingInput = dailyRecord.footballRating.toString()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Simple Vector Custom Line Graph of Football Rating Progress
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("FOOTBALL ELITE RATING INDEX", color = themeColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Calculated from touch mechanics, passing volume, and tactical discipline.", color = Color.Gray, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Line Chart
                    val points = allDailyRecords.takeLast(7).map { it.footballRating.toFloat() }
                    val defaultPoints = listOf(70f, 72f, 71f, 75f, 74f, 76f, dailyRecord.footballRating.toFloat())
                    val graphPoints = if (points.size >= 2) points else defaultPoints

                    CustomMiniGraph(
                        points = graphPoints,
                        strokeColor = themeColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Day -7", color = Color.DarkGray, fontSize = 11.sp)
                        Text("Today's Rating: ${dailyRecord.footballRating}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Stats inputs
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("RECORD METRICS", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = weightInput,
                            onValueChange = { weightInput = it },
                            label = { Text("Weight (kg)") },
                            textStyle = LocalTextStyle.current.copy(color = Color.White),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeColor,
                                unfocusedBorderColor = Color(0xFF333333)
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = speedInput,
                            onValueChange = { speedInput = it },
                            label = { Text("Sprint (km/h)") },
                            textStyle = LocalTextStyle.current.copy(color = Color.White),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeColor,
                                unfocusedBorderColor = Color(0xFF333333)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = pushupsInput,
                            onValueChange = { pushupsInput = it },
                            label = { Text("Push-ups") },
                            textStyle = LocalTextStyle.current.copy(color = Color.White),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeColor,
                                unfocusedBorderColor = Color(0xFF333333)
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = plankInput,
                            onValueChange = { plankInput = it },
                            label = { Text("Plank (s)") },
                            textStyle = LocalTextStyle.current.copy(color = Color.White),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeColor,
                                unfocusedBorderColor = Color(0xFF333333)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = ratingInput,
                        onValueChange = { ratingInput = it },
                        label = { Text("Football Rating (1-99)") },
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = themeColor,
                            unfocusedBorderColor = Color(0xFF333333)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.saveProgressMetrics(
                                weight = weightInput.toFloatOrNull() ?: dailyRecord.weightKg,
                                sprintSpeed = speedInput.toFloatOrNull() ?: dailyRecord.sprintSpeedKmh,
                                pushups = pushupsInput.toIntOrNull() ?: dailyRecord.pushupsCount,
                                plank = plankInput.toIntOrNull() ?: dailyRecord.plankSeconds,
                                rating = ratingInput.toIntOrNull() ?: dailyRecord.footballRating
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Update Stats", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomMiniGraph(
    points: List<Float>,
    strokeColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (points.isEmpty()) return@Canvas

        val maxVal = points.maxOrNull() ?: 100f
        val minVal = points.minOrNull() ?: 0f
        val diff = (maxVal - minVal).coerceAtLeast(1f)

        val stepX = size.width / (points.size - 1)
        val path = Path()

        points.forEachIndexed { idx, point ->
            val ratio = (point - minVal) / diff
            val y = size.height - (ratio * size.height * 0.8f) - (size.height * 0.1f)
            val x = idx * stepX

            if (idx == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        // Draw line path
        drawPath(
            path = path,
            color = strokeColor,
            style = Stroke(width = 3.dp.toPx())
        )

        // Draw dots
        points.forEachIndexed { idx, point ->
            val ratio = (point - minVal) / diff
            val y = size.height - (ratio * size.height * 0.8f) - (size.height * 0.1f)
            val x = idx * stepX

            drawCircle(
                color = Color.Black,
                radius = 6.dp.toPx(),
                center = Offset(x, y)
            )
            drawCircle(
                color = strokeColor,
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun SettingsPanel(
    viewModel: AppViewModel,
    userProfile: UserProfile,
    themeColor: Color
) {
    var notifEnabled by remember { mutableStateOf(userProfile.notificationsEnabled) }

    LaunchedEffect(userProfile) {
        notifEnabled = userProfile.notificationsEnabled
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SYSTEM CONFIGURATIONS", color = themeColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Dark Mode Info
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Immersive Dark Theme", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Fully black backdrop for ultimate focus", color = Color.Gray, fontSize = 12.sp)
                        }
                        Switch(
                            checked = true, // Force Dark Theme as requested
                            onCheckedChange = { },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = themeColor,
                                checkedTrackColor = themeColor.copy(alpha = 0.5f)
                            ),
                            enabled = false // Locked to true for the immersive soccer feel!
                        )
                    }

                    HorizontalDivider(color = Color(0xFF222222), modifier = Modifier.padding(vertical = 12.dp))

                    // Notifications Settings
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Training Alerts & Reminders", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Receive schedule alerts for sessions", color = Color.Gray, fontSize = 12.sp)
                        }
                        Switch(
                            checked = notifEnabled,
                            onCheckedChange = {
                                notifEnabled = it
                                viewModel.toggleNotifications(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = themeColor,
                                checkedTrackColor = themeColor.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("ATHLETE ACCENT COLOR", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Swap the glowing athletic accent throughout", color = Color.Gray, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    val options = listOf("Grass Green", "Athletic Neon", "Midnight Blue", "Sunset Gold")
                    val colors = listOf(Color(0xFF00FF66), Color(0xFF39FF14), Color(0xFF3B82F6), Color(0xFFFBBF24))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        options.forEachIndexed { idx, opt ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { viewModel.updateTheme(opt) }
                                    .padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(colors[idx])
                                        .border(
                                            width = if (userProfile.selectedTheme == opt) 3.dp else 0.dp,
                                            color = Color.White,
                                            shape = CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = opt.split(" ").last(),
                                    color = if (userProfile.selectedTheme == opt) themeColor else Color.Gray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
