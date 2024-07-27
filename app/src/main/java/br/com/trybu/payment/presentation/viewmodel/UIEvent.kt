package br.com.trybu.payment.presentation.viewmodel

sealed class UIEvent {
    data object GoToInformation : UIEvent()
    data object GoToPending : UIEvent()
    data object GoToOperations : UIEvent()
    data object GoToPayment : UIEvent()
    data object GoToDetails : UIEvent()
    data object GoToQRCode : UIEvent()
    data object None : UIEvent()
}

//
//fun UIEvent.string(): String {
//    return when (this) {
//        is UIEvent.GoToInformation -> "GoToInformation"
//        is UIEvent.GoToPending -> "GoToPending"
//        is UIEvent.GoToOperations -> "GoToOperations"
//        is UIEvent.GoToPayment -> "GoToPayment"
//        is UIEvent.GoToDetails -> "GoToDetails"
//        is UIEvent.GoToQRCode -> "GoToQRCode"
//        is UIEvent.None -> "None"
//    }
//}
//
//fun String.toEvent(): UIEvent {
//    return when (this) {
//        "GoToInformation" -> UIEvent.GoToInformation(this)
//        "GoToPending" -> UIEvent.GoToPending
//        "GoToOperations" -> UIEvent.GoToOperations
//        "GoToPayment" -> UIEvent.GoToPayment
//        "GoToDetails" -> UIEvent.GoToDetails
//        "GoToQRCode" -> UIEvent.GoToQRCode
//        else -> UIEvent.None
//    }
//}