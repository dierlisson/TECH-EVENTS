package com.dierlisson.techevents.core.util

import com.dierlisson.techevents.R

object CategoryUtils {

    fun getCategoryBackgroundRes(category: String, title: String? = null, date: String? = null): Int {
        val isEnded = (title?.contains("[ENCERRADO]", ignoreCase = true) == true) || (date != null && date < "2026-07-30")
        if (category.equals("Encerrados", ignoreCase = true)) {
            return R.drawable.bg_category_encerrados
        }

        return when (category.lowercase()) {
            "android" -> R.drawable.bg_category_android
            "kotlin" -> R.drawable.bg_category_kotlin
            "backend" -> R.drawable.bg_category_backend
            "web" -> R.drawable.bg_category_web
            "ia" -> R.drawable.bg_category_ia
            "cloud" -> R.drawable.bg_category_cloud
            "devops" -> R.drawable.bg_category_devops
            "encerrados" -> R.drawable.bg_category_encerrados
            else -> if (isEnded) R.drawable.bg_category_encerrados else R.drawable.bg_category_kotlin
        }
    }
}
