package com.example.proyecto_kottlin.repository

import com.example.proyecto_kottlin.api.Report
import com.example.proyecto_kottlin.api.RetrofitClient

class ReportRepository {
    private val api = RetrofitClient.instance

    suspend fun getReports(): List<Report> {
        return api.getReports()
    }

    suspend fun sendReport(report: Report): Report {
        return api.createReport(report)
    }
}