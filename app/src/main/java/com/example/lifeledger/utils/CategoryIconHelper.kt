package com.example.lifeledger.utils

import com.example.lifeledger.R

object CategoryIconHelper {

    fun getCategoryIcon(category: String): Int {

        return when(category.lowercase()) {

            "food" ->
                R.drawable.ic_food

            "transport" ->
                R.drawable.ic_transportation

            "shopping" ->
                R.drawable.ic_shopping

            "entertainment" ->
                R.drawable.ic_entertainment

            "healthcare" ->
                R.drawable.ic_healthcare

            "education" ->
                R.drawable.ic_education

            "housing" ->
                R.drawable.ic_housing

//            "salary" ->
//                R.drawable.ic_salary
//
//            "investment" ->
//                R.drawable.ic_investment

            "others" ->
                R.drawable.ic_others

            else ->
                R.drawable.ic_others
        }
    }
}