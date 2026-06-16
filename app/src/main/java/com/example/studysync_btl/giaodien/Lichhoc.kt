package com.example.studysync_btl.giaodien

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.res.stringResource
import com.example.studysync_btl.R
import com.example.studysync_btl.viewmodel.StudySyncViewModel
import com.example.studysync_btl.data.Subject
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LichHocTab(
    viewModel: StudySyncViewModel = viewModel(),
    onNavigateToSua: (Subject, String, String) -> Unit = { _, _, _ -> }
) {
    val scope = rememberCoroutineScope()
    var showDialogXoa by remember { mutableStateOf(false) }
    var monCanXoa by remember { mutableStateOf<Subject?>(null) }

    val lichHeThong = remember { Calendar.getInstance() }
    val dinhDangNgay = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val dinhDangChiNgayThang = remember { SimpleDateFormat("dd/MM", Locale.getDefault()) }

    val calSetTuan = remember { Calendar.getInstance().apply { firstDayOfWeek = Calendar.MONDAY; set(Calendar.DAY_OF_WEEK, Calendar.MONDAY) } }
    var ngayDauTuanDangChon by remember { mutableStateOf(calSetTuan.time) }
    var chuoiNgayDangClick by remember { mutableStateOf(dinhDangNgay.format(lichHeThong.time)) }

    val monHocList by viewModel.getSubjectsByDate(chuoiNgayDangClick).collectAsState(initial = emptyList())
    val danhSachNgayCoLich by viewModel.cacngaycolich.collectAsState(initial = emptyList())

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF8FAFC)) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
            
            Text(
                text = stringResource(id = R.string.title_lich_hoc),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B),
                modifier = Modifier.padding(vertical = 16.dp)
            )

            val danhSach7NgayTrongTuan = remember(ngayDauTuanDangChon) {
                val list = mutableListOf<Date>()
                val calTemp = Calendar.getInstance().apply { time = ngayDauTuanDangChon }
                for (i in 0 until 7) { list.add(calTemp.time); calTemp.add(Calendar.DATE, 1) }
                list
            }

            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 80.dp)) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${stringResource(id = R.string.label_tuan)}: ${dinhDangChiNgayThang.format(ngayDauTuanDangChon)} - ${dinhDangNgay.format(danhSach7NgayTrongTuan.last())}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1E293B),
                                modifier = Modifier.weight(1f)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TextButton(
                                    onClick = {
                                        ngayDauTuanDangChon = Calendar.getInstance().apply {
                                            time = ngayDauTuanDangChon
                                            add(Calendar.DATE, -7)
                                        }.time
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text(stringResource(id = R.string.btn_tuan_truoc), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0072FF))
                                }
                                TextButton(
                                    onClick = {
                                        ngayDauTuanDangChon = Calendar.getInstance().apply {
                                            time = ngayDauTuanDangChon
                                            add(Calendar.DATE, 7)
                                        }.time
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text(stringResource(id = R.string.btn_tuan_sau), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0072FF))
                                }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth()) {
                            listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN").forEach { thu ->
                                Text(text = thu, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp)).padding(8.dp)) {
                            danhSach7NgayTrongTuan.forEach { ngay ->
                                val chuoiNgay = dinhDangNgay.format(ngay)
                                val laNgayClick = (chuoiNgayDangClick == chuoiNgay)
                                val cal = Calendar.getInstance().apply { time = ngay }
                                val coLichHoc = danhSachNgayCoLich.contains(chuoiNgay)

                                Box(modifier = Modifier.weight(1f).aspectRatio(1f).clip(CircleShape).background(if (laNgayClick) Color(0xFF0072FF) else Color.Transparent).clickable { chuoiNgayDangClick = chuoiNgay }, contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                        Text(text = cal.get(Calendar.DAY_OF_MONTH).toString(), color = if (laNgayClick) Color.White else Color(0xFF1E293B), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        if (coLichHoc) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(if (laNgayClick) Color(0xFFE2E8F0) else Color(0xFF0072FF)))
                                        } else {
                                            Spacer(modifier = Modifier.height(7.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item { Text(text = stringResource(id = R.string.label_lich_hoc_ngay, chuoiNgayDangClick), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B)) }

                if (monHocList.isEmpty()) {
                    item { Text(text = stringResource(id = R.string.msg_khong_co_lich), modifier = Modifier.fillMaxWidth().padding(20.dp), textAlign = TextAlign.Center, color = Color.Gray) }
                } else {
                    items(monHocList) { mon: Subject ->
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = mon.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(text = "🕒 ${mon.time} | 📍 ${mon.room}", fontSize = 12.sp, color = Color.Gray)
                                    if (mon.note.isNotEmpty()) Text(text = "📝 ${mon.note}", fontSize = 12.sp, color = Color(0xFF0072FF))
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        val tatCaMon = viewModel.getSubjectsByName(mon.name)
                                        val sortedDates = tatCaMon.map { it.date }.sortedBy {
                                            dinhDangNgay.parse(it) ?: Date(0)
                                        }
                                        val ngayBatDau = sortedDates.firstOrNull() ?: mon.date
                                        val ngayKetThuc = sortedDates.lastOrNull() ?: mon.date
                                        onNavigateToSua(mon, ngayBatDau, ngayKetThuc)
                                    }
                                }) { Icon(Icons.Default.Edit, "Sửa", tint = Color.Blue) }
                                IconButton(onClick = { monCanXoa = mon; showDialogXoa = true }) { Icon(Icons.Default.Delete, "Xóa", tint = Color.Red) }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialogXoa) {
        AlertDialog(
            onDismissRequest = { showDialogXoa = false },
            title = { Text(stringResource(id = R.string.confirm_delete_title)) },
            text = { Text(stringResource(id = R.string.confirm_delete_msg, monCanXoa?.name ?: "")) },
            confirmButton = {
                Column(horizontalAlignment = Alignment.End) {
                    TextButton(onClick = {
                        monCanXoa?.let { viewModel.deleteAllSubjectsByName(it.name) }
                        showDialogXoa = false
                    }) {
                        Text(stringResource(id = R.string.btn_xoa_tat_ca), color = Color.Red)
                    }
                    Button(onClick = {
                        monCanXoa?.let { viewModel.deleteSubject(it) }
                        showDialogXoa = false
                    }) {
                        Text(stringResource(id = R.string.btn_xoa_ngay_nay))
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialogXoa = false }) {
                    Text(stringResource(id = R.string.btn_huy_bo))
                }
            }
        )
    }
}
