package com.airbnb.mvrx.sample.features.graphQLTODO

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import android.util.Log
import android.view.View
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.sample.R
import com.airbnb.mvrx.sample.core.BaseFragment
import com.airbnb.mvrx.sample.core.simpleController
import com.airbnb.mvrx.sample.views.basicRow
import com.airbnb.mvrx.sample.views.loadingRow
import com.airbnb.mvrx.sample.views.marquee

private const val TAG = "DadJokeIndexFragment"

class ToDoFragment : BaseFragment() {

    /**
     * This will get or create a new ViewModel scoped to this Fragment. It will also automatically
     * subscribe to all state changes and call [invalidate] which we have wired up to
     * call [buildModels] in [BaseFragment].
     */
    private val viewModel: ToDoViewModel by fragmentViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /**
         * Use viewModel.subscribe to listen for changes. The parameter is a shouldUpdate
         * function that is given the old state and new state and returns whether or not to
         * call the subscriber. onSuccess, onFail, and propertyWhitelist ship with MvRx.
         */
        viewModel.asyncSubscribe(ToDoState::request, onFail = { error ->
            Snackbar.make(coordinatorLayout, "todo request failed.", Snackbar.LENGTH_INDEFINITE)
                .show()
            Log.w(TAG, "todo request failed", error)
        })
    }

    override fun epoxyController() = simpleController(viewModel) { state ->
        marquee {
            id("todo")
            title("GraphQL TODOs")
        }

        state.todos.forEach { todo ->
            basicRow {
                id(todo?.id)
                todo?.title?.let { title(it) }
                clickListener { _ ->

                }
            }
        }

        loadingRow {
            // Changing the ID will force it to rebind when new data is loaded even if it is
            // still on screen which will ensure that we trigger loading again.
            id("loading${state.todos.size}")
            onBind { _, _, _ -> viewModel.fetchNextPage() }
        }
    }
}