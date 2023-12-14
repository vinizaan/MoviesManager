package com.github.vinizaan.moviesmanager.view

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.vinizaan.moviesmanager.R
import com.github.vinizaan.moviesmanager.databinding.FragmentMovieBinding
import com.github.vinizaan.moviesmanager.model.entity.Movie
import com.github.vinizaan.moviesmanager.model.entity.Movie.Companion.MOVIE_WATCHED_FALSE
import com.github.vinizaan.moviesmanager.model.entity.Movie.Companion.MOVIE_WATCHED_TRUE
import com.github.vinizaan.moviesmanager.view.MainFragment.Companion.EXTRA_MOVIE
import com.github.vinizaan.moviesmanager.view.MainFragment.Companion.MOVIE_FRAGMENT_REQUEST_KEY


class MovieFragment : Fragment() {
    private lateinit var fmb: FragmentMovieBinding
    private val navigationArgs: MovieFragmentArgs by navArgs()

    var genresList: List<String> =
        mutableListOf("Romance", "Adventure", "Horror", "Drama", "Action")
    var selectedGenre = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle =
            getString(R.string.movie_details)

        fmb = FragmentMovieBinding.inflate(inflater, container, false)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genresList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            val input = dest.subSequence(0, dstart).toString() + source.subSequence(start, end) +
                    dest.subSequence(dend, dest.length)

            if (input.isEmpty() || input == ".") {
                return@InputFilter null
            }

            try {
                val newValue = input.toFloat()
                if (newValue in 0.0..10.0) {
                    null
                } else {
                    ""
                }
            } catch (e: NumberFormatException) {
                ""
            }
        }

        val receivedMovie = navigationArgs.movie
        receivedMovie?.also { movie ->
            with(fmb) {
                nameEt.setText(movie.name)
                releaseYearEt.setText(movie.releaseYear.toString())
                studioEt.setText(movie.studio)
                durationMinutesEt.setText(movie.durationMinutes.toString())
                userRatingEt.setText(movie.userRating.toString())
                genreSp.setSelection(genresList.indexOf(movie.genre))
                movieWatchedCb.isChecked = movie.movieWatched == MOVIE_WATCHED_TRUE
                navigationArgs.editMovie.also { editMovie ->
                    nameEt.isEnabled = false
                    movieWatchedCb.isEnabled = editMovie
                    releaseYearEt.isEnabled = editMovie
                    studioEt.isEnabled = editMovie
                    durationMinutesEt.isEnabled = editMovie
                    userRatingEt.isEnabled = editMovie && movie.movieWatched == MOVIE_WATCHED_TRUE
                    genreSp.isEnabled = editMovie
                    saveBt.visibility = if (editMovie) VISIBLE else GONE
                }
            }
        }

        fmb.run {

            fmb.genreSp.adapter = adapter
            fmb.genreSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    selectedGenre = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

            fmb.userRatingEt.filters = arrayOf(filter)
            fmb.userRatingEt.isEnabled = false
            fmb.movieWatchedCb.setOnCheckedChangeListener { _, isChecked ->
                fmb.userRatingEt.isEnabled = isChecked
                if (!isChecked) {
                    fmb.userRatingEt.text.clear()
                }
            }

            saveBt.setOnClickListener {
//                if()
                setFragmentResult(MOVIE_FRAGMENT_REQUEST_KEY, Bundle().apply {
                    putParcelable(
                        EXTRA_MOVIE, Movie(
                            receivedMovie?.time ?: System.currentTimeMillis(),
                            nameEt.text.toString(),
                            releaseYearEt.text.toString().toInt(),
                            studioEt.text.toString(),
                            durationMinutesEt.text.toString().toInt(),
                            if (movieWatchedCb.isChecked) MOVIE_WATCHED_TRUE else MOVIE_WATCHED_FALSE,
                            if (movieWatchedCb.isChecked) userRatingEt.text.toString().toFloat() else 0f,
                            genresList[selectedGenre]
                        )
                    )
                })
                findNavController().navigateUp()
            }
        }

        return fmb.root
    }
}