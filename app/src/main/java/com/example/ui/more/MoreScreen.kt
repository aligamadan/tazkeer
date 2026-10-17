package com.example.ui.more

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppPreferences
import com.example.ui.components.BotanicalDivider
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.CairoFontFamily
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    appPreferences: AppPreferences,
    onOpenSettings: () -> Unit,
    onBackToHome: () -> Unit
) {
    var activeSubTool by remember { mutableStateOf<String?>(null) } // "QIBLA", "NAMES", "CALENDAR"

    when (activeSubTool) {
        "QIBLA" -> QiblaCompassView(
            userLat = appPreferences.customLatitude,
            userLng = appPreferences.customLongitude,
            onBack = { activeSubTool = null }
        )
        "NAMES" -> NamesOfAllahView(onBack = { activeSubTool = null })
        "CALENDAR" -> IslamicCalendarView(onBack = { activeSubTool = null })
        else -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "الخدمات والمزايا الإسلامية",
                                fontFamily = CairoFontFamily,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onBackToHome) {
                                Icon(Icons.Default.ArrowForward, contentDescription = "الرئيسية")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    )
                },
                containerColor = MaterialTheme.colorScheme.background
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        ToolItemCard(
                            title = "بوصلة اتجاه القبلة",
                            subtitle = "تحديد اتجاه الكعبة المشرفة بمستشعر البوصلة والزاوية الفلكية",
                            icon = Icons.Default.Explore,
                            onClick = { activeSubTool = "QIBLA" }
                        )
                    }

                    item {
                        ToolItemCard(
                            title = "أسماء الله الحسنى",
                            subtitle = "٩٩ اسماً مع المعاني الجليلة والدعاء بها",
                            icon = Icons.Default.AutoAwesome,
                            onClick = { activeSubTool = "NAMES" }
                        )
                    }



                    item {
                        ToolItemCard(
                            title = "المناسبات والتقويم الهجري",
                            subtitle = "موعد رمضان والأعياد وعاشوراء والأيام البيض المباركة",
                            icon = Icons.Default.Event,
                            onClick = { activeSubTool = "CALENDAR" }
                        )
                    }

                    item {
                        ToolItemCard(
                            title = "الإعدادات وضبط المواقيت",
                            subtitle = "تعديل المدينة، صوت المؤذن، طريقة الحساب، وتأخير المتابعة",
                            icon = Icons.Default.Settings,
                            onClick = onOpenSettings
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        BotanicalDivider()
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolItemCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaCompassView(
    userLat: Double,
    userLng: Double,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var azimuth by remember { mutableStateOf(0f) }

    // Calculate Kaaba bearing
    val kaabaLat = 21.4225
    val kaabaLng = 39.8262
    val qiblaBearing = remember(userLat, userLng) {
        val lat1 = Math.toRadians(userLat)
        val lat2 = Math.toRadians(kaabaLat)
        val dLng = Math.toRadians(kaabaLng - userLng)
        val y = sin(dLng)
        val x = cos(lat1) * tan(lat2) - sin(lat1) * cos(dLng)
        var b = Math.toDegrees(atan2(y, x))
        if (b < 0) b += 360.0
        b.toFloat()
    }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null && event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    var deg = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    if (deg < 0) deg += 360f
                    azimuth = deg
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        rotationSensor?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
        onDispose { sensorManager?.unregisterListener(listener) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("بوصلة القبلة", fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "زاوية اتجاه الكعبة: ${qiblaBearing.toInt()}°",
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "ضع الهاتف بشكل أفقي لتوجيه السهم نحو الكعبة المشرفة",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Compass Dial
            val needleRotation = qiblaBearing - azimuth
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(3.dp, MaterialTheme.colorScheme.outline, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Compass markings
                Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    drawCircle(color = Color(0xFF627357).copy(alpha = 0.2f), radius = size.minDimension / 2f)
                }

                // Rotating Needle
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(needleRotation),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxHeight().padding(vertical = 20.dp)
                    ) {
                        Text(text = "🕋", fontSize = 32.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height(60.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
            BotanicalDivider()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamesOfAllahView(onBack: () -> Unit) {
    val names = listOf(
        Pair("الله", "الاسم الأعظم الجامع لصفات الألوهية"),
        Pair("الرَّحْمَنُ", "ذو الرحمة الشاملة لجميع الخلائق"),
        Pair("الرَّحِيمُ", "ذو الرحمة الخاصة بالمؤمنين"),
        Pair("الْمَلِكُ", "المتصرف في ملكه كيف يشاء"),
        Pair("الْقُدُّوسُ", "المنزه عن كل نقص وعيب"),
        Pair("السَّلَامُ", "السالم من النقائص والعيوب وناشر الأمان"),
        Pair("الْمُؤْمِنُ", "المصدق لرسله المانح للأمن والأمان"),
        Pair("الْمُهَيْمِنُ", "الرقيب الشاهد الحافظ لكل شيء"),
        Pair("الْعَزِيزُ", "القوي الغالب الذي لا يُغلب"),
        Pair("الْجَبَّارُ", "الذي يجبر كسر المنكسرين ويصلح أحوالهم"),
        Pair("الْمُتَكَبِّرُ", "المتعالي عن صفات الخلق والظلم"),
        Pair("الْخَالِقُ", "الموجد للأشياء من العدم"),
        Pair("الْبَارِئُ", "المبرئ للمخلوقات على أكمل صورة"),
        Pair("الْمُصَوِّرُ", "المعطي لكل خلق شكله وصورته"),
        Pair("الْغَفَّارُ", "الكثير المغفرة والستر على العباد"),
        Pair("الْقَهَّارُ", "الذي خضعت له الرقاب والخلائق"),
        Pair("الْوَهَّابُ", "الكثير العطاء بلا عوض ولا غرض"),
        Pair("الرَّزَّاقُ", "المتكفل بأرزاق العباد كلهم"),
        Pair("الْفَتَّاحُ", "الحاكم بين عباده بالحق والفاتح لأبواب رحمته"),
        Pair("الْعَلِيمُ", "المحيط بكل شيء ظاهراً وباطناً")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("أسماء الله الحسنى", fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(names) { (name, meaning) ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = name,
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = meaning,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IslamicCalendarView(onBack: () -> Unit) {
    val events = listOf(
        Triple("شهر رمضان المبارك", "١ رمضان ١٤٤٨ هـ", "شهر الصيام والقرآن والبركات 🌙"),
        Triple("عيد الفطر المبارك", "١ شوال ١٤٤٨ هـ", "يوم الجائزة والفرح وإتمام الصيام ✨"),
        Triple("يوم عرفة المبارك", "٩ ذو الحجة ١٤٤٨ هـ", "خير يوم طلعت عليه الشمس، صيامه يكفر سنتين 🕋"),
        Triple("عيد الأضحى المبارك", "١٠ ذو الحجة ١٤٤٨ هـ", "يوم النحر والتقرب بالأضاحي 🐑"),
        Triple("يوم عاشوراء", "١٠ محرم ١٤٤٩ هـ", "نجى الله فيه موسى، وصيامه يكفر سنة ماضية 🌊")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("المناسبات الإسلامية", fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(events) { (name, date, desc) ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = date,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
