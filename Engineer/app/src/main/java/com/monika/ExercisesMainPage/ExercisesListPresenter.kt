package com.monika.ExercisesMainPage

import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.monika.Enums.FirebaseRequestResult
import com.monika.Enums.UserDataType
import com.monika.Model.WorkoutComponents.Exercise
import com.monika.Services.DatabaseService

class ExercisesListPresenter {

    fun getOptionsForUpdatesListener(): FirestoreRecyclerOptions<Exercise> {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val query : Query = DatabaseService.instance.getQueryForFetching(UserDataType.EXERCISE)
                .whereEqualTo("userId", user.uid)
                .orderBy("name", Query.Direction.ASCENDING)
            return FirestoreRecyclerOptions.Builder<Exercise>()
                .setQuery(query, Exercise::class.java)
                .build()
        }
        else {
            val query : Query = DatabaseService.instance.getQueryForFetching(UserDataType.EXERCISE)
                .whereEqualTo("userId", "")
                .orderBy("name", Query.Direction.ASCENDING)
            return FirestoreRecyclerOptions.Builder<Exercise>()
                .setQuery(query, Exercise::class.java)
                .build()
        }
    }

    fun removeItemAt(item: Exercise, completion: (result: FirebaseRequestResult) -> Unit) {
        DatabaseService.instance.removeDocument(item, UserDataType.EXERCISE) {
            result -> completion(result)
        }
    }


}
