package zero.network.reto2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.*
import org.json.JSONObject
import zero.network.reto2.utils.get
import zero.network.reto2.utils.getIO

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {

    /**
     * Search Job
     */
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
            actualJob?.cancel() // in case job is running cancel it and restart.
            adapter.items.removeAll(adapter.items.toList())
            adapter.notifyDataSetChanged()

            actualJob = GlobalScope.launch(Dispatchers.Main) {
                fetchData().collect {
                    adapter.items += it
                    adapter.notifyItemInserted(adapter.items.lastIndex)
                }
                actualJob = null
            }
        }
    }

    /**
     * Return a flow that fetch the playlists over the IO thread
     */
    private suspend fun fetchData(): Flow<PlayList> =
        if (searchBar.text.toString().isEmpty()) flowOf()
        else flow {
                val data =
                    JSONObject(getIO("$SEARCH_URL${searchBar.text}"))
                        .getJSONArray("data")
                ( 0 until data.length() ).forEach {
                    val id = data.getJSONObject(it).getLong("id")
                    emit(PlayList.fromJson(JSONObject(
                        get("$PLAYLIST_URL$id")
                    )))
                }
            }.flowOn(IO)


    companion object {
        private const val SEARCH_URL = "https://api.deezer.com/search/playlist?q="
        private const val PLAYLIST_URL = "https://api.deezer.com/playlist/"
    }

}
