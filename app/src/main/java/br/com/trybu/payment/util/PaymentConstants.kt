package br.com.trybu.payment.util

object PaymentConstants {
    //App Reference Constants
    const val USER_REFERENCE = "Elosgate"

    //Payments Constants
    const val TYPE_CREDITO = 1
    const val TYPE_DEBITO = 2
    const val TYPE_VOUCHER = 3
    const val TYPE_QRCODE_DEBITO = 4
    const val TYPE_PIX = 5
    const val TYPE_QRCODE_CREDITO = 7
    const val VALUE_MINIMAL_INSTALLMENT = 1000
    const val INSTALLMENT_TYPE_A_VISTA = 1
    const val INSTALLMENT_TYPE_PARC_VENDEDOR = 2
    const val INSTALLMENT_TYPE_PARC_COMPRADOR = 3
    const val CREDIT_VALUE = "valueCredit"
    const val PAYMENT_CARD_MESSAGE = "Aproxime, insira ou passe o cartão"

    //NFC Constants
    const val NFC_OK = 1
    const val RET_WAITING_REMOVE_CARD = 2
    const val NFC_START_FAIL = "Ocorreu um erro ao iniciar o serviço nfc: "
    const val APDU_COMMAND_FAIL = "Ocorreu um erro no comando APDU: "
    const val WAITING_REMOVE_CARD = "Aguardando a remoção do cartão..."
    const val REMOVED_CARD = "Cartão removido com sucesso"
    const val CARD_NOT_REMOVED = "Cartão removido com sucesso"
    const val AUTH_CARD_SUCCESS = "Cartão autenticado com sucesso"
    const val AUTH_BLOCK_B_CARD_SUCCESS = "Autenticado com sucesso com o cartão bloco B"
    const val CARD_DETECTED_SUCCESS = "Cartão detectado com sucesso - cid: "
    const val VALUE_RESULT = "Valor do result: "
    const val NO_NEAR_FIELD_FOUND = "No Near Field Card found"
    const val CARD_NOT_FOUND = "Cartão não identificado "
    const val TEST_16_BYTES = "teste_com16bytes"

    //LED and Beep Constants
    const val LED_ON_SUCCESS = "Led ligado com sucesso"
    const val LED_OFF_SUCCESS = "Led desligado com sucesso"
    const val LED_FAIL = "Não foi possível setar o led"
    const val BEEP_SUCCESS = "Beep realizado com sucesso"
    const val BEEP_FAIL = "Não foi possível realizar o beep"

    //PlugPagStyle Constants
    const val HEAD_TEXT_COLOR = -0x1
    const val HEAD_BACKGROUND_COLOR = -0xe13c70
    const val CONTENT_TEXT_COLOR = -0xdfdfe0
    const val CONTENT_TEXT_VALUE_1_COLOR = -0xffe000
    const val CONTENT_TEXT_VALUE_2_COLOR = -0x100000
    const val POSITIVE_BUTTON_TEXT_COLOR = -0x1
    const val POSITIVE_BUTTON_BACKGROUND = -0xff358c
    const val NEGATIVE_BUTTON_TEXT_COLOR = -0x777778
    const val NEGATIVE_BUTTON_BACKGROUND = 0x00ffffff
    const val LINE_COLOR = -0x1000000
}
