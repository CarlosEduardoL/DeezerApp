package zero.network.reto2

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_play_list.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject

@ExperimentalCoroutinesApi
class PlayListActivity : AppCompatActivity() {

    private var actualJob: Job? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_list)
        val playlist = intent.extras!!.getSerializable("playlist")

        backButton.setOnClickListener {
            actualJob?.cancel()
            finish()
        }

        val adapter = SongAdapter()

        songsList.apply {
            layoutManager = LinearLayoutManager(this@PlayListActivity)
            this.adapter = adapter
        }

        if (playlist is PlayList){
            playlistTitle.text = playlist.title
            playlistDescription.text = playlist.description
            playlistFans.text = "Fans: ${playlist.fansCount}"
            playlistSongsCount.text = "Songs: ${playlist.songCount}"
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(this)
                .applyDefaultRequestOptions(requestOptions)
                .load(playlist.image)
                .into(playlistBanner)


            actualJob = GlobalScope.launch {
                fetchData(playlist).collect {
                    withContext(Main){
                        adapter.songs.add(it)
                        adapter.notifyItemInserted(adapter.songs.size-1)
                    }
                }
            }
        }


    }

    private suspend fun fetchData(list: PlayList): Flow<Song> {
        println(JSONObject(httpGet("https://api.deezer.com/playlist/${list.id}")))
        val data = JSONObject(httpGet("https://api.deezer.com/playlist/${list.id}"))
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
}
