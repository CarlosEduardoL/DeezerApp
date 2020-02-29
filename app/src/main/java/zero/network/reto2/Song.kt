package zero.network.reto2

import org.json.JSONObject

data class Song (
    val name: String,
    val artist: String,
    val releaseDate: String,
    val album: String,
    val image: String,
    val duration: Int
){
    companion object {
        fun fromJson(songJson: JSONObject) = Song(
            songJson.getString("title"),
            songJson.getJSONObject("artist").getString("name"),
            songJson.getString("release_date"),
            songJson.getJSONObject("album").getString("title"),
            songJson.getJSONObject("album").getString("cover"),
            songJson.getInt("duration")
        )
    }
}