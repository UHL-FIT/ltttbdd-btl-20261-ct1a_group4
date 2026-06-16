package com.example.studysync_btl.giaodien

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.res.stringResource
import com.example.studysync_btl.R
import com.example.studysync_btl.viewmodel.StudySyncViewModel
import com.example.studysync_btl.data.Subject
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemmoiTab(
    viewModel: StudySyncViewModel = viewModel(),
    idBanDau: Int = 0,
    dangChinhSua: Boolean = false,
    tenBanDau: String = "",
    phongBanDau: String = "",
    tinChiBanDau: String = "",
    ghiChuBanDau: String = "",
    thoiGianBanDau: String = "",
    ngayBanDau: String = "",
    ngayKetThucBanDau: String = "",
    isLichThiBanDau: Boolean = false,
    onFinish: () -> Unit = {}
) {
    val context = LocalContext.current
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    var tenMonHoc by remember(tenBanDau) { mutableStateOf(tenBanDau) }
    var phongHoc by remember(phongBanDau) { mutableStateOf(phongBanDau) }
    var soTinChi by remember(tinChiBanDau) { mutableStateOf(tinChiBanDau) }
    var luuYRieng by remember(ghiChuBanDau) { mutableStateOf(ghiChuBanDau) }
    var laNgayThi by remember(isLichThiBanDau) { mutableStateOf(isLichThiBanDau) }

    // Xử lý Ngày khi Sửa
    val ngayInitial = try { if (ngayBanDau.isNotEmpty()) sdf.parse(ngayBanDau)?.time else null } catch(e: Exception) { null }
    val ngayKetThucInitial = try { if (ngayKetThucBanDau.isNotEmpty()) sdf.parse(ngayKetThucBanDau)?.time else null } catch(e: Exception) { null }

    var ngayBatDauMillis by remember(ngayInitial) { mutableStateOf(if (!laNgayThi) ngayInitial else null) }
    var ngayKetThucMillis by remember(ngayKetThucInitial) { mutableStateOf(ngayKetThucInitial) }
    var ngayThiDuyNhatMillis by remember(ngayInitial) { mutableStateOf(if (laNgayThi) ngayInitial else null) }

    var showDatePicker by remember { mutableStateOf(false) }
    var loaiNgayDangChon by remember { mutableStateOf("BAT_DAU") }

    val gioSplit = thoiGianBanDau.split(" - ")
    var gioBatDau by remember(thoiGianBanDau) { mutableStateOf(if (gioSplit.size == 2) gioSplit[0] else null) }
    var gioKetThuc by remember(thoiGianBanDau) { mutableStateOf(if (gioSplit.size == 2) gioSplit[1] else null) }

    var showTimePicker by remember { mutableStateOf(false) }
    var loaiGioDangChon by remember { mutableStateOf("GIO_BAT_DAU") }

    val gradientNutBam = Brush.horizontalGradient(colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)))

    val tieuDeManHinh = if (dangChinhSua) {
        if (laNgayThi) stringResource(id = R.string.title_chinh_sua_thi) else stringResource(id = R.string.title_chinh_sua_hoc)
    } else {
        stringResource(id = R.string.title_them_moi)
    }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun validateAndSave() {
        val currentGioBatDau = gioBatDau ?: ""
        val currentGioKetThuc = gioKetThuc ?: ""

        if (tenMonHoc.isBlank()) {
            Toast.makeText(context, "Tên môn học không được để trống", Toast.LENGTH_SHORT).show()
            return
        }
        // Kiểm tra tên môn học không được là số (đặc biệt là số âm)
        if (tenMonHoc.toDoubleOrNull() != null) {
            if (tenMonHoc.toDouble() < 0) {
                Toast.makeText(context, "Tên môn học không được là số âm", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Tên môn học không được chỉ bao gồm chữ số", Toast.LENGTH_SHORT).show()
            }
            return
        }
        if (phongHoc.isBlank()) {
            Toast.makeText(context, "Phòng học không được để trống", Toast.LENGTH_SHORT).show()
            return
        }
        if (currentGioBatDau.isBlank()) {
            Toast.makeText(context, "Vui lòng chọn giờ bắt đầu", Toast.LENGTH_SHORT).show()
            return
        }
        if (currentGioKetThuc.isBlank()) {
            Toast.makeText(context, "Vui lòng chọn giờ kết thúc", Toast.LENGTH_SHORT).show()
            return
        }
        val ngayCanCheck = if (laNgayThi) ngayThiDuyNhatMillis else ngayBatDauMillis
        if (ngayCanCheck == null) {
            val msg = if (laNgayThi) "Vui lòng chọn ngày thi" else "Vui lòng chọn ngày bắt đầu"
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            return
        }

        // Kiểm tra ngày thi không được trước ngày hiện tại (chỉ khi thêm mới)
        if (laNgayThi && !dangChinhSua) {
            val homNay = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            
            if (ngayCanCheck < homNay) {
                Toast.makeText(context, "Ngày thi không được trước ngày hiện tại", Toast.LENGTH_SHORT).show()
                return
            }
        }
        if (!laNgayThi && ngayKetThucMillis == null) {
            Toast.makeText(context, "Vui lòng chọn ngày kết thúc", Toast.LENGTH_SHORT).show()
            return
        }
        val dateString = sdf.format(Date(ngayCanCheck))

        val timePattern = Regex("^([01]\\d|2[0-3]):[0-5]\\d$")
        // Kiểm tra định dạng giờ bắt đầu
        if (!timePattern.matches(currentGioBatDau)) {
            Toast.makeText(context, "Giờ bắt đầu không hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }
        // Kiểm tra định dạng giờ kết thúc
        if (!timePattern.matches(currentGioKetThuc)) {
            Toast.makeText(context, "Giờ kết thúc không hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }

        // Kiểm tra giờ kết thúc phải sau giờ bắt đầu
        val startMin = currentGioBatDau.split(":").let { it[0].toInt() * 60 + it[1].toInt() }
        val endMin = currentGioKetThuc.split(":").let { it[0].toInt() * 60 + it[1].toInt() }
        if (endMin <= startMin) {
            Toast.makeText(context, "Giờ kết thúc phải sau giờ bắt đầu", Toast.LENGTH_SHORT).show()
            return
        }

        // Kiểm tra ngày kết thúc không được trước ngày bắt đầu (cho lịch học)
        if (!laNgayThi && ngayBatDauMillis != null && ngayKetThucMillis != null) {
            if (ngayKetThucMillis!! < ngayBatDauMillis!!) {
                Toast.makeText(context, "Ngày kết thúc không được trước ngày bắt đầu", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Hàm kiểm tra trùng lặp cho một ngày cụ thể
        fun isDuplicateOnDate(ngay: String, gioBatDau: String, phong: String, excludeId: Int, excludeName: String? = null): Boolean {
            val currentList = viewModel.allSubjects.value
            return currentList.any { subject ->
                // Nếu cùng ID hoặc cùng tên (khi đang sửa môn học) thì bỏ qua không check trùng
                val laMonDangSua = (excludeId != 0 && subject.id == excludeId) || 
                                   (excludeName != null && subject.name.equals(excludeName, ignoreCase = true) && !subject.isExam)
                
                if (laMonDangSua) false
                else {
                    subject.date == ngay &&
                    subject.time.startsWith(gioBatDau) &&
                    subject.room.equals(phong, ignoreCase = true)
                }
            }
        }

        if (!laNgayThi) {
            val tc = soTinChi.toIntOrNull()
            if (tc == null || tc < 1 || tc > 3) {
                Toast.makeText(context, "Số tín chỉ phải là số nguyên từ 1 đến 3", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Xử lý lưu với kiểm tra trùng lặp
        val formattedTime = "$currentGioBatDau - $currentGioKetThuc"
        val currentCredits = soTinChi.toIntOrNull() ?: 0

        if (laNgayThi) {
            // Kiểm tra trùng tên môn thi (Không được trùng tên môn thi khác)
            val exists = viewModel.allSubjects.value.any {
                it.isExam && it.name.equals(tenMonHoc, ignoreCase = true) && it.id != (if (dangChinhSua) idBanDau else 0)
            }
            if (exists) {
                Toast.makeText(context, "Môn thi này đã tồn tại trong danh sách", Toast.LENGTH_SHORT).show()
                return
            }

            // Kiểm tra trùng lịch thi theo (Ngày, Giờ, Phòng)
            if (isDuplicateOnDate(dateString, currentGioBatDau, phongHoc, if (dangChinhSua) idBanDau else 0)) {
                Toast.makeText(context, "Đã có lịch thi cùng ngày, cùng giờ và cùng phòng", Toast.LENGTH_SHORT).show()
                return
            }
            val singleSubject = Subject(
                id = idBanDau,
                name = tenMonHoc,
                room = phongHoc,
                time = formattedTime,
                date = dateString,
                note = luuYRieng,
                isExam = true,
                credits = currentCredits
            )
            viewModel.addSubject(singleSubject)
        }
        else if (ngayBatDauMillis != null && ngayKetThucMillis != null) {
            // Lặp tuần
            var currentMillis = ngayBatDauMillis!!
            val endMillis = ngayKetThucMillis!!
            val bayNgayMillis = 7 * 24 * 60 * 60 * 1000L
            val subjectsToAdd = mutableListOf<Subject>()

            while (currentMillis <= endMillis) {
                val dateStringLoop = sdf.format(Date(currentMillis))
                // Kiểm tra trùng lặp, loại trừ chính môn đang sửa (theo tên cũ)
                if (isDuplicateOnDate(dateStringLoop, currentGioBatDau, phongHoc, 0, if (dangChinhSua) tenBanDau else null)) {
                    Toast.makeText(context, "Ngày $dateStringLoop đã có môn học khác trùng giờ và phòng", Toast.LENGTH_LONG).show()
                    return 
                }
                subjectsToAdd.add(
                    Subject(
                        id = 0,
                        name = tenMonHoc,
                        room = phongHoc,
                        time = formattedTime,
                        date = dateStringLoop,
                        note = luuYRieng,
                        isExam = false,
                        credits = currentCredits
                    )
                )
                currentMillis += bayNgayMillis
            }

            if (dangChinhSua) {
                viewModel.updateWeeklySubject(tenBanDau, subjectsToAdd)
            } else {
                viewModel.addSubjects(subjectsToAdd)
            }
        }
        else {
            // Lịch học một ngày
            if (isDuplicateOnDate(dateString, currentGioBatDau, phongHoc, if (dangChinhSua) idBanDau else 0)) {
                Toast.makeText(context, "Đã có môn học cùng ngày, cùng giờ và cùng phòng", Toast.LENGTH_SHORT).show()
                return
            }
            val singleSubject = Subject(
                id = idBanDau,
                name = tenMonHoc,
                room = phongHoc,
                time = formattedTime,
                date = dateString,
                note = luuYRieng,
                isExam = false,
                credits = currentCredits
            )
            viewModel.addSubject(singleSubject)
        }

        Toast.makeText(context, if (dangChinhSua) "Cập nhật thành công" else "Thêm thành công", Toast.LENGTH_SHORT).show()
        onFinish()
    }

    SeenTemplate(title = tieuDeManHinh) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        ) {
            // KHỐI 1: CHỌN CHẾ ĐỘ (Chỉ hiển thị khi thêm mới)
            if (!dangChinhSua) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (laNgayThi) stringResource(id = R.string.mode_lich_thi) else stringResource(id = R.string.mode_lich_hoc),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (laNgayThi) Color(0xFFEF4444) else Color(0xFF0284C7)
                            )
                            Text(
                                text = stringResource(id = R.string.desc_mode),
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        Switch(checked = laNgayThi, onCheckedChange = { laNgayThi = it })
                    }
                }
            }

            // KHỐI 2: CHI TIẾT THÔNG TIN (giữ nguyên)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = tenMonHoc,
                        onValueChange = { tenMonHoc = it },
                        label = { Text(stringResource(id = R.string.label_ten_mon)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    if (!laNgayThi) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(stringResource(id = R.string.label_ngay_bat_dau), fontSize = 12.sp, color = Color(0xFF64748B))
                                Box(modifier = Modifier.fillMaxWidth().height(54.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFF1F5F9)).clickable { loaiNgayDangChon = "BAT_DAU"; showDatePicker = true }.padding(horizontal = 14.dp), contentAlignment = Alignment.CenterStart) {
                                    Text(text = ngayBatDauMillis?.let { sdf.format(Date(it)) } ?: stringResource(id = R.string.placeholder_chon_ngay), fontSize = 14.sp)
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(stringResource(id = R.string.label_ngay_ket_thuc), fontSize = 12.sp, color = Color(0xFF64748B))
                                Box(modifier = Modifier.fillMaxWidth().height(54.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFF1F5F9)).clickable { loaiNgayDangChon = "KET_THUC"; showDatePicker = true }.padding(horizontal = 14.dp), contentAlignment = Alignment.CenterStart) {
                                    Text(text = ngayKetThucMillis?.let { sdf.format(Date(it)) } ?: stringResource(id = R.string.placeholder_chon_ngay), fontSize = 14.sp)
                                }
                            }
                        }
                    } else {
                        Column {
                            Text(stringResource(id = R.string.label_ngay_thi), fontSize = 12.sp, color = Color(0xFF64748B))
                            Box(modifier = Modifier.fillMaxWidth().height(54.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFFEF2F2)).clickable { loaiNgayDangChon = "NGAY_THI"; showDatePicker = true }.padding(horizontal = 14.dp), contentAlignment = Alignment.CenterStart) {
                                Text(text = ngayThiDuyNhatMillis?.let { sdf.format(Date(it)) } ?: stringResource(id = R.string.placeholder_chon_ngay_thi), fontSize = 14.sp)
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stringResource(id = R.string.label_gio_bat_dau), fontSize = 12.sp, color = Color(0xFF64748B))
                            Box(modifier = Modifier.fillMaxWidth().height(54.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFF1F5F9)).clickable { loaiGioDangChon = "GIO_BAT_DAU"; showTimePicker = true }.padding(horizontal = 14.dp), contentAlignment = Alignment.CenterStart) {
                                Text(text = gioBatDau ?: "00:00", fontSize = 14.sp)
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stringResource(id = R.string.label_gio_ket_thuc), fontSize = 12.sp, color = Color(0xFF64748B))
                            Box(modifier = Modifier.fillMaxWidth().height(54.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFF1F5F9)).clickable { loaiGioDangChon = "GIO_KET_THUC"; showTimePicker = true }.padding(horizontal = 14.dp), contentAlignment = Alignment.CenterStart) {
                                Text(text = gioKetThuc ?: "00:00", fontSize = 14.sp)
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (!laNgayThi) {
                            OutlinedTextField(
                                value = soTinChi,
                                onValueChange = { input ->
                                    // Chỉ cho phép nhập tối đa 1 ký tự và phải là số từ 1-3
                                    if (input.isEmpty()) {
                                        soTinChi = ""
                                    } else if (input.length == 1 && input[0].isDigit()) {
                                        val num = input.toInt()
                                        if (num in 1..3) {
                                            soTinChi = input
                                        }
                                    }
                                },
                                label = { Text(stringResource(id = R.string.label_tin_chi)) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                        OutlinedTextField(value = phongHoc, onValueChange = { phongHoc = it }, label = { Text(stringResource(id = R.string.label_phong)) }, modifier = Modifier.weight(1.5f), shape = RoundedCornerShape(14.dp))
                    }
                }
            }

            // KHỐI 3: LƯU Ý
            OutlinedTextField(
                value = luuYRieng, onValueChange = { luuYRieng = it }, label = { Text(stringResource(id = R.string.label_ghi_chu)) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 90.dp), shape = RoundedCornerShape(14.dp)
            )

            // NÚT LƯU
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(27.dp))
                    .background(gradientNutBam)
                    .clickable { validateAndSave() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (dangChinhSua) stringResource(id = R.string.btn_cap_nhat).uppercase() else stringResource(id = R.string.btn_luu_lich).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }

    // DatePicker và TimePicker giữ nguyên
    if (showDatePicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
            TextButton(onClick = {
                when (loaiNgayDangChon) {
                    "BAT_DAU" -> ngayBatDauMillis = state.selectedDateMillis
                    "KET_THUC" -> ngayKetThucMillis = state.selectedDateMillis
                    "NGAY_THI" -> ngayThiDuyNhatMillis = state.selectedDateMillis
                }
                showDatePicker = false
            }) { Text(stringResource(id = R.string.btn_xac_nhan)) }
        }) { DatePicker(state = state) }
    }

    if (showTimePicker) {
        val state = rememberTimePickerState(is24Hour = true)
        AlertDialog(onDismissRequest = { showTimePicker = false }, confirmButton = {
            TextButton(onClick = {
                val time = String.format("%02d:%02d", state.hour, state.minute)
                if (loaiGioDangChon == "GIO_BAT_DAU") gioBatDau = time else gioKetThuc = time
                showTimePicker = false
            }) { Text(stringResource(id = R.string.btn_xac_nhan)) }
        }, text = { TimePicker(state = state) })
    }
}