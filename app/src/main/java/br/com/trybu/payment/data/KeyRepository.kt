package br.com.trybu.payment.data

import android.content.SharedPreferences
import javax.inject.Inject

class KeyRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    fun persisKey(key: String) {
        sharedPreferences.edit().putString("key", key).commit()
    }

    fun retrieveKey() = sharedPreferences.getString("key", "")


}