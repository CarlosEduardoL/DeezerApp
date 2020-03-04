package zero.network.reto2

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_play_list.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.json.JSONObject
import zero.network.reto2.utils.get
import zero.network.reto2.utils.getSerializableOr
import zero.network.reto2.utils.loadImage

@ExperimentalCoroutinesApi
class PlayListActivity : AppCompatActivity() {

    private var actualJob: Job? = null

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

        songsList.apply {
            layoutManager = LinearLayoutManager(this@PlayListActivity)
            adapter = songAdapter
        }

        playlist.apply {
            playlistTitle.text = title
            playlistDescription.text = description
            playlistFans.text = "Fans: $fansCount"
            playlistSongsCount.text = "Songs: $songCount"

            loadImage(image, playlistBanner)

            actualJob = GlobalScope.launch(Main) {
                fetchData(playlist).collect {
                    songAdapter.songs.add(it)
                    songAdapter.notifyItemInserted(songAdapter.songs.lastIndex)
                }
            }
        }
    }

    override fun onDestroy() {
        actualJob?.cancel()
        super.onDestroy()
    }

    private suspend fun fetchData(list: PlayList) = flow {
        val data = JSONObject(get("$PLAYLIST_URL${list.id}"))
            .getJSONObject("tracks")
            .getJSONArray("data")
        (0 until data.length()).forEach {
            val songJson = data.getJSONObject(it)
            emit(fetchSong(songJson.getString("id")))
        }
    }.flowOn(IO)

    private fun fetchSong(id: String) =
        Song.fromJson(JSONObject(get("$TRACK_URL$id")))

    companion object {
        private const val PLAYLIST_URL = "https://api.deezer.com/playlist/"
        private const val TRACK_URL = "https://api.deezer.com/track/"
    }

}
