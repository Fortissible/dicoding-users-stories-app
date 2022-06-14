package com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidintermediate_sub1_wildanfajrialfarabi.R
import com.example.androidintermediate_sub1_wildanfajrialfarabi.data.remote.response.ListStoryItem
import com.example.androidintermediate_sub1_wildanfajrialfarabi.databinding.ActivityStoriesBinding
import com.example.androidintermediate_sub1_wildanfajrialfarabi.databinding.StoryItemBinding
import com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.MainViewModel
import com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.ViewModelFactory
import com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.adapter.ListStoriesAdapter
import com.example.androidintermediate_sub1_wildanfajrialfarabi.ui.adapter.LoadingStateAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class StoriesActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var storiesBinding: ActivityStoriesBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonAddStory: FloatingActionButton
    private lateinit var buttonLogout: FloatingActionButton
    private lateinit var buttonLanguage: FloatingActionButton
    private lateinit var buttonMaps: FloatingActionButton
    private lateinit var buttonExtendMenu: FloatingActionButton
    private lateinit var noStoryAvailable : TextView
    private lateinit var loadingStories : ProgressBar
    private lateinit var openMenu: Animation
    private lateinit var closeMenu: Animation
    private lateinit var adapter: ListStoriesAdapter

    private var addButtonClicked = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storiesBinding = ActivityStoriesBinding.inflate(layoutInflater)
        setContentView(storiesBinding.root)
        supportActionBar?.hide()
        viewBinding()
        val factory : ViewModelFactory = ViewModelFactory.getInstance(this)
        val mainViewModel: MainViewModel by viewModels {
            factory
        }

        openMenu = AnimationUtils.loadAnimation(this,R.anim.open_menu)
        closeMenu = AnimationUtils.loadAnimation(this,R.anim.close_menu)

        setStoriesData()

        buttonLogout.setOnClickListener {
            mainViewModel.clearLoginPreferences()
            logout()
        }
        buttonAddStory.setOnClickListener(this)
        buttonLanguage.setOnClickListener(this)
        buttonMaps.setOnClickListener(this)
        buttonExtendMenu.setOnClickListener(this)

        mainViewModel.stories.observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    override fun onClick(p0: View) {
        when(p0.id){
            buttonAddStory.id -> {
                toAddStoryActivity()
            }

            buttonLanguage.id -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

            buttonMaps.id -> {
                val intentToMaps = Intent(this,MapsActivity::class.java)
                intentToMaps.putExtra(MapsActivity.EXTRA_FROM_STORIES,true)
                startActivity(intentToMaps)
            }

            buttonExtendMenu.id -> {
                onAddButtonClicked()
            }
        }
    }

    private fun logout(){
        val intentToMainActivity = Intent(this, MainActivity::class.java)
        ActivityCompat.finishAffinity(this)
        Toast.makeText(this, "Anda telah logout", Toast.LENGTH_SHORT).show()
        startActivity(intentToMainActivity)
    }

    private fun toAddStoryActivity(){
        val intentToAddStoriesActivity = Intent(this, AddStoriesActivity::class.java)
        startActivity(intentToAddStoriesActivity)
    }

    private fun setStoriesData() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ListStoriesAdapter()
        adapter.refresh()
        recyclerView.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        lifecycleScope.launch {
            adapter.loadStateFlow.collect {
                if (adapter.itemCount < 4){
                    noStoryAvailable.visibility = View.VISIBLE
                    loadingStories.visibility = View.VISIBLE
                }
                if (adapter.itemCount >= 4) {
                    recyclerView.visibility = View.VISIBLE
                    loadingStories.visibility = View.INVISIBLE
                    noStoryAvailable.visibility = View.INVISIBLE
                }
            }
        }
        adapter.setOnItemClickCallback(object : ListStoriesAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem, view: View) {
                val binding = StoryItemBinding.bind(view)
                val cardItem = ListStoriesAdapter.ListViewHolder(binding)
                val stringName = getString(R.string.name)
                val stringImage = getString(R.string.image)
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        view.context as Activity,
                        androidx.core.util.Pair(cardItem.imageView, stringImage),
                        androidx.core.util.Pair(cardItem.name, stringName)
                        )
                val intentToDetail = Intent(view.context, StoryDetailActivity::class.java)
                intentToDetail.putExtra(StoryDetailActivity.EXTRA_DATA, data)
                view.context.startActivity(intentToDetail, optionsCompat.toBundle())
            }
        })
    }

    private fun viewBinding(){
        recyclerView = storiesBinding.storyRecview
        loadingStories = storiesBinding.loadingStories
        buttonLogout = storiesBinding.logoutButton
        buttonAddStory = storiesBinding.addStoryButton
        buttonLanguage = storiesBinding.languageButton
        buttonMaps = storiesBinding.mapsStories
        buttonExtendMenu = storiesBinding.extendMenuButton
        noStoryAvailable = storiesBinding.noStoryAvailable
    }

    private fun onAddButtonClicked() {
        setVisibility(addButtonClicked)
        setAnimation(addButtonClicked)
        buttonSetClickable()

        addButtonClicked = !addButtonClicked
    }

    private fun setVisibility(buttonClicked: Boolean) {
        if (!buttonClicked){
            buttonAddStory.visibility = View.VISIBLE
            buttonLanguage.visibility = View.VISIBLE
            buttonLogout.visibility = View.VISIBLE
            buttonMaps.visibility = View.VISIBLE
        }else{
            buttonAddStory.visibility = View.INVISIBLE
            buttonLanguage.visibility = View.INVISIBLE
            buttonLogout.visibility = View.INVISIBLE
            buttonMaps.visibility = View.INVISIBLE
        }
    }

    private fun setAnimation(buttonClicked: Boolean) {
        if (!buttonClicked){
            buttonAddStory.startAnimation(closeMenu)
            buttonLanguage.startAnimation(closeMenu)
            buttonLogout.startAnimation(closeMenu)
            buttonMaps.startAnimation(closeMenu)
        }else{
            buttonAddStory.startAnimation(openMenu)
            buttonLanguage.startAnimation(openMenu)
            buttonLogout.startAnimation(openMenu)
            buttonMaps.startAnimation(openMenu)
        }
    }

    private fun buttonSetClickable() {
        if (!addButtonClicked){
            buttonMaps.isClickable = false
            buttonAddStory.isClickable = false
            buttonLogout.isClickable = false
            buttonLanguage.isClickable = false
        }else{
            buttonMaps.isClickable = true
            buttonAddStory.isClickable = true
            buttonLogout.isClickable = true
            buttonLanguage.isClickable = true
        }
    }
}