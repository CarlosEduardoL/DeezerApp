package zero.network.reto2

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_play_list.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONObject
import zero.network.reto2.utils.*

@ExperimentalCoroutinesApi
class PlayListActivity : AppCompatActivity() {

    private var scope = CoroutineScope(Main)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_list)

        // get the playlist from the intent
        val playlist = intent.getSerializableOr<PlayList>("playlist") {
            finish() // in case of error finish the activity
            return   // and return the onCreate method [in theory this error is impossible but just in case]
        }

        backButton.setOnClickListener { finish() } // navigation item to return to the main activity

        val songAdapter = SongAdapter()

        songsList.let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = songAdapter
        }

        playlist.apply {
            playlistTitle.text = title
            playlistDescription.text = description
            playlistFans.text = "Fans: $fansCount"
            playlistSongsCount.text = "Songs: $songCount"

            loadImage(image, playlistBanner)

            scope.launch {
                JSONObject(getHTTP("$PLAYLIST_URL${this@apply.id}"))
                    .getJSONObject("tracks")
                    .getJSONArray("data")
                    .forEach {
                        scope.launch {
                            songAdapter.addSong(fetchSong(it.getString("id")))
                        }
                    }
            }
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private suspend fun fetchSong(id: String) =
        Song.fromJson(getHTTP("$TRACK_URL$id").json)

    companion object {
        private const val PLAYLIST_URL = "https://api.deezer.com/playlist/"
        private const val TRACK_URL = "https://api.deezer.com/track/"
    }

}
