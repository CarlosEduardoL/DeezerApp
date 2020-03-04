package zero.network.reto2.utils

import android.content.Intent
import java.io.Serializable

const val EXTRA_ERROR = "Intent don't have extra information"
const val CAST_EXCEPTION = "Object isn't type"
const val KEY_ERROR = "Extras don't have any serializable with key"

/**
 * if the object exist return it, but if some element was null execute the or function
 * the or function receives the exception that interrupt the execution en return nothing
 * @CarlosEduardoL recommend use the or function to return the method where the object is required
 * but is possible too throw an exception
 */
inline fun <reified T : Serializable> Intent.getSerializableOr(
    key: String,
    or: (Exception) -> Nothing = {
        throw it
    }
) = ((extras ?: or(NullPointerException(EXTRA_ERROR))).getSerializable(key) ?: or(
    NullPointerException("$KEY_ERROR $key")
)).let {
    if (it is T) it
    else or(ClassCastException("$CAST_EXCEPTION ${T::class.qualifiedName}"))
}
