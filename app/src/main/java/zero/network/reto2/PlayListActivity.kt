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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.json.JSONObject

@ExperimentalCoroutinesApi
class PlayListActivity : AppCompatActivity() {

    private var actualJob: Job? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_list)
        val playlist = intent.extras!!.getSerializable("playlist")

        backButton.setOnClickListener { finish() }

        val adapter = SongAdapter()

        songsList.apply {
            layoutManager = LinearLayoutManager(this@PlayListActivity)
            this.adapter = adapter
        }

        if (playlist is PlayList) playlist.apply{
            playlistTitle.text = title
            playlistDescription.text = description
            playlistFans.text = "Fans: $fansCount"
            playlistSongsCount.text = "Songs: $songCount"

            loadImage(image, playlistBanner)

            actualJob = GlobalScope.launch(Main) {
                fetchData(this@apply).collect {
                    adapter.songs.add(it)
                    adapter.notifyItemInserted(adapter.songs.size-1)
                }
            }
        }
    }

    override fun onDestroy() {
        actualJob?.cancel()
        super.onDestroy()
    }

    private suspend fun fetchData(list: PlayList): Flow<Song> {
        val data = JSONObject(httpGet("$PLAYLIST_URL${list.id}"))
            .getJSONObject("tracks")
            .getJSONArray("data")
        return flow {
            for (i in 0 until data.length()) {
                val songJson = data.getJSONObject(i)
                emit(fetchSong(songJson.getString("id")))
            }
        }.flowOn(IO)
    }

    private suspend fun fetchSong(id: String) =
        Song.fromJson(JSONObject(httpGet("https://api.deezer.com/track/$id")))

    companion object {
        private const val PLAYLIST_URL = "https://api.deezer.com/playlist/"
    }

}
