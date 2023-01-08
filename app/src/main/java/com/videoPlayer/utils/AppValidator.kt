package com.videoPlayer.utils

class AppValidator {

    companion object {
        private var nameRegex: String = "[a-zA-Z ]{2,}"

        fun isValidName(name: String): Boolean {
            return name.matches(nameRegex.toRegex())
        }
    }
}