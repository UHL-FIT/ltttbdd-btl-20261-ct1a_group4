package com.example.studysync_btl.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.studysync_btl.giaodien.LichHocTab
import com.example.studysync_btl.giaodien.LichThiTab
import com.example.studysync_btl.giaodien.ThemmoiTab
import com.example.studysync_btl.giaodien.ThongKeTab
import com.example.studysync_btl.giaodien.TrangChuTab
import com.example.studysync_btl.data.Subject
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studysync_btl.viewmodel.StudySyncViewModel

// 1. Định nghĩa các tuyến đường hằng số
object StudySyncRoutes {
    const val TRANG_CHU = "trangchu"
    const val LICH_HOC = "lichhoc"
    const val LICH_THI = "lichthi"
    const val THEM_MOI = "themmoi"
    const val THONG_KE = "thongke"
}

// 2. Tạo một kiểu dữ liệu chuẩn thay thế cho Triple cũ
data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)


object NavigationItems {
    val bottomNavItems = listOf(
        BottomNavItem(StudySyncRoutes.TRANG_CHU, Icons.Filled.Home, "Trang chủ"),
        BottomNavItem(StudySyncRoutes.LICH_HOC, Icons.Filled.DateRange, "Lịch học"),
        BottomNavItem(StudySyncRoutes.LICH_THI, Icons.Filled.Assignment, "Lịch thi"),
        BottomNavItem(StudySyncRoutes.THEM_MOI, Icons.Filled.AddBox, "Thêm mới"),
        BottomNavItem(StudySyncRoutes.THONG_KE, Icons.Filled.BarChart, "Thống kê")
    )
}

@Composable
fun StudySyncNavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    var idSua by remember { mutableIntStateOf(0) }
    var dangSua by remember { mutableStateOf(false) }
    var tenSua by remember { mutableStateOf("") }
    var phongSua by remember { mutableStateOf("") }
    var tinChiSua by remember { mutableStateOf("") }
    var noteSua by remember { mutableStateOf("") }
    var thoiGianSua by remember { mutableStateOf("") }
    var ngaySua by remember { mutableStateOf("") }
    var ngayKetThucSua by remember { mutableStateOf("") }
    var laLichThiSua by remember { mutableStateOf(false) }

    val viewModel: StudySyncViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = StudySyncRoutes.TRANG_CHU,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(StudySyncRoutes.TRANG_CHU) {
            TrangChuTab(
                viewModel = viewModel,
                onNavigateToLichHoc = {
                    navController.navigate(StudySyncRoutes.LICH_HOC) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToLichThi = {
                    navController.navigate(StudySyncRoutes.LICH_THI) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToThem = {
                    idSua = 0; dangSua = false
                    tenSua = ""; phongSua = ""; tinChiSua = ""; noteSua = ""; thoiGianSua = ""; ngaySua = ""
                    ngayKetThucSua = ""; laLichThiSua = false
                    navController.navigate(StudySyncRoutes.THEM_MOI) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToThongKe = {
                    navController.navigate(StudySyncRoutes.THONG_KE) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(StudySyncRoutes.LICH_HOC) {
            LichHocTab(
                viewModel = viewModel,
                onNavigateToSua = { subject: Subject, ngayBatDau: String, ngayKetThuc: String ->
                    idSua = subject.id; dangSua = true; tenSua = subject.name; phongSua = subject.room
                    tinChiSua = subject.credits.toString(); noteSua = subject.note; thoiGianSua = subject.time
                    ngaySua = ngayBatDau; ngayKetThucSua = ngayKetThuc; laLichThiSua = subject.isExam
                    navController.navigate(StudySyncRoutes.THEM_MOI)
                }
            )
        }

        composable(StudySyncRoutes.LICH_THI) {
            LichThiTab(
                viewModel = viewModel,
                onNavigateToSua = { subject: Subject, ngayBatDau: String, ngayKetThuc: String ->
                    idSua = subject.id; dangSua = true; tenSua = subject.name; phongSua = subject.room
                    tinChiSua = subject.credits.toString(); noteSua = subject.note; thoiGianSua = subject.time
                    ngaySua = ngayBatDau; ngayKetThucSua = ngayKetThuc; laLichThiSua = subject.isExam
                    navController.navigate(StudySyncRoutes.THEM_MOI)
                }
            )
        }

        composable(StudySyncRoutes.THEM_MOI) {
            ThemmoiTab(
                viewModel = viewModel,
                idBanDau = idSua,
                dangChinhSua = dangSua,
                tenBanDau = tenSua,
                phongBanDau = phongSua,
                tinChiBanDau = tinChiSua,
                ghiChuBanDau = noteSua,
                thoiGianBanDau = thoiGianSua,
                ngayBanDau = ngaySua,
                ngayKetThucBanDau = ngayKetThucSua,
                isLichThiBanDau = laLichThiSua,
                onFinish = {
                    val wasEditing = dangSua
                    idSua = 0; dangSua = false; tenSua = ""; phongSua = ""; tinChiSua = ""; noteSua = ""
                    thoiGianSua = ""; ngaySua = ""; ngayKetThucSua = ""; laLichThiSua = false

                    if (wasEditing) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(StudySyncRoutes.TRANG_CHU) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = false
                                saveState = false
                            }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }
                }
            )
        }

        composable(StudySyncRoutes.THONG_KE) {
            ThongKeTab(viewModel = viewModel)
        }
    }
}