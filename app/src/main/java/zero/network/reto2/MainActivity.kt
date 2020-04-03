package zero.network.reto2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.json.JSONObject
import zero.network.reto2.utils.forEach
import zero.network.reto2.utils.getHTTP
import zero.network.reto2.utils.json

class MainActivity : AppCompatActivity() {

    private var scope = CoroutineScope(Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = PlayListAdapter()
        playlistList.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        searchButton.setOnClickListener {
            scope.cancel()
            scope = CoroutineScope(Main)
            adapter.clear()
            scope.launch {
                getHTTP("$SEARCH_URL${searchBar.text}").json.getJSONArray("data")
                    .forEach {
                        val id = it.getLong("id")
                        scope.launch {
                            adapter.addPlayList(PlayList.fromJson(getHTTP("$PLAYLIST_URL$id").json))
                        }
                    }
            }
        }
    }

    companion object {
        private const val SEARCH_URL = "https://api.deezer.com/search/playlist?q="
        private const val PLAYLIST_URL = "https://api.deezer.com/playlist/"
    }

}
