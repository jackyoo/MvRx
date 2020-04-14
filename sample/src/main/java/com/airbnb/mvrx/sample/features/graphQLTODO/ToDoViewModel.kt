package com.airbnb.mvrx.sample.features.graphQLTODO

import TodosQuery
import com.airbnb.mvrx.*
import com.airbnb.mvrx.sample.core.MvRxViewModel
import com.airbnb.mvrx.sample.models.Joke
import com.airbnb.mvrx.sample.models.JokesResponse
import com.airbnb.mvrx.sample.models.ToDo
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import okhttp3.OkHttpClient
import java.util.*
import java.util.Collections.EMPTY_LIST
import java.util.Collections.copy

private const val JOKES_PER_PAGE = 5

data class ToDoState(
        /** We use this request to store the list of all jokes. */
//    val todos: List<ToDo> = emptyList(),
    val todos: List<TodosQuery.Todo?>  = emptyList(),
        /** We use this Async to keep track of the state of the current network request. */
    val request: Async<JokesResponse> = Uninitialized
) : MvRxState

/**
 * initialState *must* be implemented as a constructor parameter.
 */
class ToDoViewModel(
    initialState: ToDoState,
    private val apolloClient: ApolloClient

) : MvRxViewModel<ToDoState>(initialState) {

    init {
        fetchNextPage()
    }

    fun fetchNextPage() = withState { state ->
//        if (state.request is Loading) return@withState
        apolloClient.query(TodosQuery()).enqueue(object : ApolloCall.Callback<TodosQuery.Data?>() {
            override fun onResponse(dataResponse: Response<TodosQuery.Data?>) {
                val todos = dataResponse.data()?.todos ?: emptyList()
                setState {
                    copy(todos = todos)
                }
            }

            override fun onFailure(e: ApolloException) {
                println(e.message)
            }
        })
    }

    companion object : MvRxViewModelFactory<ToDoViewModel, ToDoState> {

        override fun create(viewModelContext: ViewModelContext, state: ToDoState): ToDoViewModel {
            val okHttpClient = OkHttpClient.Builder()
                    .build()
            val apolloClient = ApolloClient.builder()
                    .serverUrl("https://todo-mongo-graphql-server.herokuapp.com/graphql")
                    .okHttpClient(okHttpClient)
                    .build()
            return ToDoViewModel(state, apolloClient)
        }
    }
}
