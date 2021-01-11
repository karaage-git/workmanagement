package com.karaageumai.workmanagement.presenter.analyze.annual.util

data class AnnualDataRow(
        // データ名のリソースID
        val dataNameResId: Int,
        // データ表示用の値
        val dataValue: String,
        // 単位の文字列リソース
        val unitResId: Int,
        // 内訳用の項目か否かを示す値（true:内訳）
        val isSubItem: Boolean
)