package com.github.vinizaan.moviesmanager.view

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.vinizaan.moviesmanager.view.adapter.OnMovieClickListener
import com.github.vinizaan.moviesmanager.R
import com.github.vinizaan.moviesmanager.controller.MovieViewModel
import com.github.vinizaan.moviesmanager.databinding.FragmentMainBinding
import com.github.vinizaan.moviesmanager.model.entity.Movie
import com.github.vinizaan.moviesmanager.view.adapter.MovieAdapter

class MainFragment : Fragment(), OnMovieClickListener {
    private lateinit var fmb: FragmentMainBinding

    private val movieList: MutableList<Movie> = mutableListOf()

    private val movieAdapter: MovieAdapter by lazy {
        MovieAdapter(movieList, this)
    }

    private val navController: NavController by lazy {
        findNavController()
    }

    companion object {
        const val EXTRA_MOVIE = "EXTRA_MOVIE"
        const val MOVIE_FRAGMENT_REQUEST_KEY = "MOVIE_FRAGMENT_REQUEST_KEY"
    }

    private val movieViewModel: MovieViewModel by viewModels {
        MovieViewModel.MovieViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(MOVIE_FRAGMENT_REQUEST_KEY) { requestKey, bundle ->
            if (requestKey == MOVIE_FRAGMENT_REQUEST_KEY) {
                val movie = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bundle.getParcelable(EXTRA_MOVIE, Movie::class.java)
                } else {
                    bundle.getParcelable(EXTRA_MOVIE)
                }
                movie?.also { receivedMovie ->
                    movieList.indexOfFirst { it.time == receivedMovie.time }.also { position ->
                        if (position != -1) {
                            movieViewModel.editMovie(receivedMovie)
                            movieList[position] = receivedMovie
                            movieAdapter.notifyItemChanged(position)
                        } else {
                            if (movieList.any { it.name == receivedMovie.name }) {
                                Toast.makeText(
                                    requireContext(),
                                    "This movie could not be added.\nReason: There is already a movie with the same name.",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                movieViewModel.insertMovie(receivedMovie)
                                movieList.add(receivedMovie)
                                movieAdapter.notifyItemInserted(movieList.lastIndex)
                            }
                        }
                    }
                }

                (context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    fmb.root.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }

        movieViewModel.moviesMld.observe(requireActivity()) { movies ->
            movieList.clear()
            movies.forEachIndexed { index, movie ->
                movieList.add(movie)
                movieAdapter.notifyItemChanged(index)
            }
        }

        movieViewModel.getMovies()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = getString(R.string.movies_list)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.sort_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if(menuItem.itemId == R.id.action_sort_name) {
                    movieList.sortBy { it.name }
                    movieAdapter.notifyItemRangeChanged(0, movieList.size)
                } else {
                    movieList.sortByDescending { it.userRating }
                    movieAdapter.notifyItemRangeChanged(0, movieList.size)
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        fmb = FragmentMainBinding.inflate(inflater, container, false).apply {
            moviesRv.layoutManager = LinearLayoutManager(context)
            moviesRv.adapter = movieAdapter

            addMovieFab.setOnClickListener {
                navController.navigate(
                    MainFragmentDirections.actionMainFragmentToMovieFragment(null, editMovie = false)
                )
            }
        }

        return fmb.root
    }

    override fun onMovieClick(position: Int) = navigateToMovieFragment(position, false)

    override fun onRemoveMovieMenuItemClick(position: Int) {
        movieViewModel.removeMovie(movieList[position])
        movieList.removeAt(position)
        movieAdapter.notifyItemRemoved(position)
    }

    override fun onEditMovieMenuItemClick(position: Int) = navigateToMovieFragment(position, true)

    override fun onWatchedCheckBoxClick(position: Int, checked: Boolean) {
        movieList[position].apply {
            movieWatched = if (checked) Movie.MOVIE_WATCHED_TRUE else Movie.MOVIE_WATCHED_FALSE
            userRating = 0f
            movieViewModel.editMovie(this)
            movieAdapter.notifyItemChanged(position)
        }
    }

    private fun navigateToMovieFragment(position: Int, editMovie: Boolean) {
        movieList[position].also {
            navController.navigate(
                MainFragmentDirections.actionMainFragmentToMovieFragment(it, editMovie)
            )
        }
    }
}