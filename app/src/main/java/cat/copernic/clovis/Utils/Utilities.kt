package cat.copernic.clovis.Utils

import androidx.annotation.Keep
import java.util.regex.Pattern

@Keep
class Utilities {
    fun campEsBuit(correu: String, contrasenya: String): Boolean {
        return correu.isNotEmpty() && contrasenya.isNotEmpty()
    }

    var email_Param =
        Pattern.compile("^[_a-z0-9]+(.[_a-z0-9]+)*@[a-z0-9]+(.[a-z0-9]+)*(.[a-z]{2,4})\$")

    fun isValidEmail(email: CharSequence?): Boolean {
        return if (email == null) false else email_Param.matcher(email).matches()
    }

}
