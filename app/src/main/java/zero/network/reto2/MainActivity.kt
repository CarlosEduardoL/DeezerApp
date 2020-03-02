package zero.network.reto2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.*
import org.json.JSONObject

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {

    private var actualJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = PlayListAdapter()
        playlistList.apply{
            this.adapter =  adapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        searchButton.setOnClickListener {
            actualJob?.cancel()
            adapter.items.removeAll(adapter.items.toList())
            adapter.notifyDataSetChanged()

            actualJob = GlobalScope.launch(Dispatchers.Main) {
                fetchData().collect {
                    adapter.items += it
                    adapter.notifyItemInserted(adapter.items.size - 1)
                }
                actualJob = null
            }
        }
    }

    private suspend fun fetchData(): Flow<PlayList> =
        if (searchBar.text.toString().isEmpty()) flowOf()
        else {
            val data =
                JSONObject(httpGet("$SEARCH_URL${searchBar.text}"))
                    .getJSONArray("data")
            flow {
                for (i in 0 until data.length()) {
                    val id = data.getJSONObject(i).getLong("id")
                    emit(PlayList.fromJson(JSONObject(httpGet("$PLAYLIST_URL$id"))))
                }
            }.flowOn(IO)
        }

    companion object {
        private const val SEARCH_URL = "https://api.deezer.com/search/playlist?q="
        private const val PLAYLIST_URL = "https://api.deezer.com/playlist/"
    }

}
