package br.com.trybu.payment.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    var qrListener: QRListener? = null
    var qrCode = MutableLiveData<QRUIState>()

    var qrCodeString = ""

    fun qrCode(contents: String) {
        qrCodeString = contents
        qrListener?.onQRCode(contents)
    }

    fun openCamera() {
        qrCode.postValue(QRUIState.ReadQRCode)
    }

    interface QRListener {
        fun onQRCode(contents: String)
    }

}

sealed class QRUIState {
    data object ReadQRCode : QRUIState()
    data class ResulQRCode(val qrCode: String) : QRUIState()
}