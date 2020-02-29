package zero.network.reto2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = PlayListAdapter()
        playlistList.apply{
            this.adapter =  adapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        searchButton.setOnClickListener {
            GlobalScope.launch {
                fetchData().collect {
                    withContext(Dispatchers.Main){
                        adapter.items += it
                        adapter.notifyItemInserted(adapter.items.size - 1)
                    }
                }
            }
        }
    }

    private suspend fun fetchData(): Flow<PlayList> {
        val data =
            JSONObject(httpGet("https://api.deezer.com/search/playlist?q=${searchBar.text}"))
                .getJSONArray("data")
        return flow {
            for (i in 0 until data.length()) {
                val id = data.getJSONObject(i).getLong("id")
                emit(PlayList.fromJson(JSONObject(httpGet("https://api.deezer.com/playlist/$id"))))
            }
        }.flowOn(IO)
    }
}
