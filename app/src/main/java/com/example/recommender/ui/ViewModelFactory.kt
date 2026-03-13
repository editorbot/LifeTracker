package com.example.recommender.ui


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recommender.data.local.GenreScoreManager
import com.example.recommender.data.local.MediaDatabase
import com.example.recommender.data.local.UserPreferencesDataStore
import com.example.recommender.data.remote.aws.AwsRetrofitClient
import com.example.recommender.data.remote.books.BooksRetrofitClient
import com.example.recommender.data.remote.tmdb.TmdbRetrofitClient
import com.example.recommender.data.repository.MediaRepository
import com.example.recommender.ui.add.AddTitleViewModel
import com.example.recommender.ui.detail.DetailViewModel
import com.example.recommender.ui.foryou.ForYouViewModel
import com.example.recommender.ui.home.HomeViewModel


class ViewModelFactory(context: Context) : ViewModelProvider.Factory {

    // All dependencies built once here
    private val dao = MediaDatabase.getInstance(context).mediaDao()
    private val genreScoreManager = GenreScoreManager(context)   // ← add this
    private val repository = MediaRepository(
        dao,
        TmdbRetrofitClient.api,
        BooksRetrofitClient.api,
        genreScoreManager ,                                        // ← add this
        AwsRetrofitClient.api
    )
    private val dataStore = UserPreferencesDataStore(context)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(repository) as T
            modelClass.isAssignableFrom(AddTitleViewModel::class.java) ->
                AddTitleViewModel(repository, dataStore) as T
            modelClass.isAssignableFrom(DetailViewModel::class.java) ->
                DetailViewModel(repository) as T
            modelClass.isAssignableFrom(ForYouViewModel::class.java) ->
                ForYouViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }

}