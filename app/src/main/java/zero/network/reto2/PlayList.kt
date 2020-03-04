package zero.network.reto2

import org.json.JSONObject
import java.io.Serializable

/**
 * Object than represent a Deezer playlist
 */
data class PlayList(
    val id: Long,
    val title: String,
    val creator: String,
    val songCount: Int,
    val image: String,
    val description: String,
    val fansCount: Int
): Serializable {
    companion object {
        fun fromJson(listJson: JSONObject) = PlayList(
                listJson.getLong("id"),
                listJson.getString("title"),
                listJson.getJSONObject("creator").getString("name"),
                listJson.getInt("nb_tracks"),
                listJson.getString("picture"),
                listJson.getString("description"),
                listJson.getInt("fans")
            )
    }
}