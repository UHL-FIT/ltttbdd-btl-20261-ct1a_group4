package com.example.studysync_btl.giaodien

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studysync_btl.R
import com.example.studysync_btl.viewmodel.StudySyncViewModel
import java.util.Calendar

@Composable
fun TrangChuTab(
    viewModel: StudySyncViewModel = viewModel(),
    onNavigateToLichHoc: () -> Unit,
    onNavigateToLichThi: () -> Unit,
    onNavigateToThem: () -> Unit,
    onNavigateToThongKe: () -> Unit
) {
    val allSubjects by viewModel.allSubjects.collectAsState(initial = emptyList())
    val examSubjects by viewModel.examSubjects.collectAsState(initial = emptyList())

    val soNgayHoc = allSubjects
        .filter { !it.isExam }
        .map { it.date }
        .distinct()
        .size

    val soMonThi = examSubjects.size

    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val loiChuc = when (currentHour) {
        in 0..11 -> stringResource(id = R.string.text_chuc_ngay_moi)
        in 12..17 -> "Giữ vững phong độ học\ntập thật tốt nhé!"
        else -> "Nghỉ ngơi và rà soát lại\nkiến thức nhé!"
    }

    SeenTemplate(title = stringResource(id = R.string.title_trang_chu)) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.horizontalGradient(colors = listOf(Color(0xFFEFB8C8), Color(0xFFEFB8C8))))
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.text_xin_chao),
                        fontSize = 15.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = loiChuc,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 26.sp
                    )
                }
            }

            Column {
                Text(
                    text = stringResource(id = R.string.label_tinh_hinh),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(bottom = 10.dp, start = 4.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToLichHoc() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(id = R.string.stat_so_ngay_hoc),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$soNgayHoc ngày",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToLichThi() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(id = R.string.stat_sap_thi),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$soMonThi môn",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                        }
                    }
                }
            }

            Column {
                Text(
                    text = stringResource(id = R.string.label_thao_tac),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(bottom = 10.dp, start = 4.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFEFF6FF))
                            .clickable { onNavigateToLichHoc() }
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "📆", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.action_xem_lich),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF0FDF4))
                            .clickable { onNavigateToThem() }
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "➕", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.action_them_mon),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534)
                        )
                    }
                }
            }
        }
    }
}
