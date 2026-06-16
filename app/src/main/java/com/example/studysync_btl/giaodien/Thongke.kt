package com.example.studysync_btl.giaodien

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.res.stringResource
import com.example.studysync_btl.R
import com.example.studysync_btl.viewmodel.StudySyncViewModel

@Composable
fun ThongKeTab(
    viewModel: StudySyncViewModel = viewModel()
) {

    val allSubjects by viewModel.allSubjects.collectAsState(initial = emptyList())


    val danhSachMonHocDK = allSubjects.filter { !it.isExam }


    val danhSachThongKeDuyNhat = remember(danhSachMonHocDK) {
        danhSachMonHocDK.distinctBy { it.name.trim().lowercase() }
    }


    val tongSoTinChi = danhSachThongKeDuyNhat.sumOf { it.credits }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Text(
                text = stringResource(id = R.string.title_thong_ke),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B)
            )


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF8A2387), Color(0xFFE94057))
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Text(text = stringResource(id = R.string.text_xin_chao), color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(id = R.string.text_chuc_ngay_moi),
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp
                    )
                }
            }


            Text(
                text = stringResource(id = R.string.label_thong_ke_tin_chi),
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(top = 8.dp),
                letterSpacing = 0.5.sp
            )

            Card(
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxSize()
                ) {
                    // Danh sách môn học (Dùng LazyColumn để cuộn nếu nhiều môn)
                    Box(modifier = Modifier.weight(1f)) {

                        if (danhSachThongKeDuyNhat.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.msg_empty_thong_ke),
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.Gray
                            )
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                                items(danhSachThongKeDuyNhat) { mon ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = mon.name,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF334155)
                                        )
                                        Text(
                                            text = stringResource(id = R.string.label_tin_chi_format, mon.credits),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(
                        color = Color(0xFFF1F5F9),
                        thickness = 1.5.dp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    // Tổng cộng số tín chỉ
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.label_tong_so_dang_ky),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFFEFEF))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.label_tin_chi_format, tongSoTinChi),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFF4B2B)
                            )
                        }
                    }
                }
            }
        }
    }
}