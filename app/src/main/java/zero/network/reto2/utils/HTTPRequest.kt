package zero.network.reto2.utils

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * HTTP Get method implementation run in IO context
 */
suspend fun getHTTP(myURL: String?): String = withContext(IO) {
    val inputStream: InputStream
    val result:String

    // create URL
    val url = URL(myURL)

    // create HttpURLConnection
    val conn: HttpURLConnection = url.openConnection() as HttpURLConnection

    // make GET request to the given URL
    conn.connect()

    // receive response as inputStream
    inputStream = conn.inputStream

    // convert inputstream to string
    result = inputStream?.let{
        convertInputStreamToString(
            it
        )
    } ?: "Did not work!"

    result
}


private fun convertInputStreamToString(inputStream: InputStream): String {
    val bufferedReader = BufferedReader(InputStreamReader(inputStream))

    var line:String? = bufferedReader.readLine()
    var result = ""

    while (line != null) {
        result += line
        line = bufferedReader.readLine()
    }

    inputStream.close()
    return result
}