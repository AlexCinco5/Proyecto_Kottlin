package com.example.proyecto_kottlin.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_kottlin.api.Report
import com.example.proyecto_kottlin.repository.ReportRepository
import kotlinx.coroutines.launch

class ReportViewModel : ViewModel() {
    private val repository = ReportRepository()

    private val _reports = mutableStateOf<List<Report>>(emptyList())
    val reports: State<List<Report>> = _reports

    fun fetchReports() {
        viewModelScope.launch {
            try {
                val response = repository.getReports()
                _reports.value = response
            } catch (e: Exception) {
                // Manejo de errores a futuro
            }
        }
    }

    fun createReport(title: String, description: String, lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val newReport = Report(title = title, description = description, latitude = lat, longitude = lon)
                repository.sendReport(newReport)
                fetchReports()
            } catch (e: Exception) {
                // Manejo de errores a futuro
            }
        }
    }
}