package com.example.studysync_btl.giaodien

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studysync_btl.R
import com.example.studysync_btl.viewmodel.StudySyncViewModel
import com.example.studysync_btl.data.Subject

@Composable
fun LichThiTab(
    viewModel: StudySyncViewModel = viewModel(),
    onNavigateToSua: (Subject, String, String) -> Unit = { _, _, _ -> }
) {
    val monThiList by viewModel.examSubjects.collectAsState(initial = emptyList())
    var showDialogXoa by remember { mutableStateOf(false) }
    var monCanXoa by remember { mutableStateOf<Subject?>(null) }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF8FAFC)) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                text = stringResource(id = R.string.title_danh_sach_thi),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                color = Color(0xFF1E293B),
                modifier = Modifier.padding(vertical = 16.dp)
            )

            if (monThiList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = stringResource(id = R.string.msg_empty_lich_thi), color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 80.dp)) {
                    items(monThiList) { thi: Subject ->
                        val soNgay = viewModel.tinhNgayConLai(thi.date)
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFFF4B2B)), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("$soNgay", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                        Text(stringResource(id = R.string.label_ngay), color = Color.White, fontSize = 10.sp)
                                     }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = thi.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(text = "📆 Ngày thi: ${thi.date}", fontSize = 12.sp, color = Color.Gray)
                                    LinearProgressIndicator(
                                        progress = { (soNgay.toFloat() / 30f).coerceIn(0f, 1f) },
                                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(4.dp).clip(CircleShape),
                                        color = Color(0xFFFF4B2B)
                                    )
                                }
                                IconButton(onClick = { onNavigateToSua(thi, thi.date, thi.date) }) {
                                    Icon(Icons.Default.Edit, "Sửa", tint = Color.Blue)
                                }
                                IconButton(onClick = { monCanXoa = thi; showDialogXoa = true }) { Icon(Icons.Default.Delete, "Xóa", tint = Color.Red) }
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
                Button(onClick = {
                    monCanXoa?.let { viewModel.deleteSubject(it) }
                    showDialogXoa = false
                }) {
                    Text(stringResource(id = R.string.btn_xoa))
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
