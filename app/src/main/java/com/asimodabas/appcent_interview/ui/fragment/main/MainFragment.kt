package com.asimodabas.appcent_interview.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.asimodabas.appcent_interview.Constants.API_KEY
import com.asimodabas.appcent_interview.adapter.MainRecyclerAdapter
import com.asimodabas.appcent_interview.adapter.MainViewPagerAdapter
import com.asimodabas.appcent_interview.databinding.FragmentMainBinding
import com.asimodabas.appcent_interview.listener.GameClickListener
import com.asimodabas.appcent_interview.listener.GameViewPagerListener
import com.asimodabas.appcent_interview.model.Result
import com.asimodabas.appcent_interview.toastMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    private lateinit var recyclerAdapter: MainRecyclerAdapter
    private lateinit var viewPagerAdapter: MainViewPagerAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        onCreateFinished()
        observeEvents()
    }

    fun onCreateFinished() {
        viewModel.getData(API_KEY)
    }

    fun observeEvents() {
        with(viewModel) {

            gameResponse.observe(viewLifecycleOwner, Observer {

                it?.let {
                    it.results?.let { it1 -> setRecycler(it1) }
                    it.results?.let { it2 -> setSearchView(it2) }
                    it.results?.let { it3 -> setViewPager(it3) }
                }
            })

            onError.observe(viewLifecycleOwner, Observer {
                requireContext().toastMessage(it.toString())
            })
        }
    }

    private fun setRecycler(data: List<Result>) {
        recyclerAdapter = MainRecyclerAdapter(object : GameClickListener {
            override fun GameClick(vie: Result) {
                if (vie.id != null) {
                    val navigation =
                        MainFragmentDirections.actionMainFragmentToDetailFragment(vie.id)
                    Navigation.findNavController(requireView()).navigate(navigation)
                }
            }

            override fun GameFilter(nameLength: Int) {
                if (false) {
                    binding.infoLayout.visibility = View.VISIBLE
                } else {
                    binding.infoLayout.visibility = View.GONE
                }
            }
        })

        viewPagerAdapter = MainViewPagerAdapter(object : GameViewPagerListener {
            override fun ViewPagerClick(game: Result) {
                if (game.id != null) {
                    val navigation =
                        MainFragmentDirections.actionMainFragmentToDetailFragment(game.id)
                    Navigation.findNavController(requireView()).navigate(navigation)
                }
            }

        })
        binding.mainRecyclerView.adapter = recyclerAdapter
        recyclerAdapter.setList(data)
    }

    private fun setSearchView(games: List<Result>) {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText?.length!! >= 3) {
                    binding.mainViewPager.visibility = View.GONE
                    recyclerAdapter.setList(games)
                    recyclerAdapter.filter.filter(newText)
                } else if (newText.isEmpty()) {
                    binding.mainViewPager.visibility = View.VISIBLE
                    recyclerAdapter.setList(games)
                }
                return true
            }
        })
    }

    private fun setViewPager(games: List<Result>) {
        val indexThreeGame = ArrayList<Result>()
        val newIndexThreeGame = addTopThreeGame(games, indexThreeGame)
        initViewPager(newIndexThreeGame)
    }

    private fun addTopThreeGame(
        data: List<Result>,
        topThreeGame: ArrayList<Result>
    ): ArrayList<Result> {
        topThreeGame.add(data[0])
        topThreeGame.add(data[1])
        topThreeGame.add(data[2])
        return topThreeGame
    }

    private fun initViewPager(newIndexThreeGame: ArrayList<Result>) {
        viewPager = binding.mainViewPager
        viewPager.offscreenPageLimit = 3
        viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        viewPagerAdapter.setList(newIndexThreeGame)
        viewPager.adapter = viewPagerAdapter
    }
}