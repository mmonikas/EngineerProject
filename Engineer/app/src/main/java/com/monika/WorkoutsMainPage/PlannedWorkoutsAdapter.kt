package com.monika.WorkoutsMainPage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.monika.Model.WorkoutPlan.PlannedWorkout
import com.monika.Model.WorkoutPlan.Workout
import com.monika.R
import kotlinx.android.synthetic.main.workout_cardview.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PlannedWorkoutsAdapter(private val context: Context, private val plannedWorkouts: ArrayList<PlannedWorkout>,
                             private val listener: WorkoutsPlannerListener, private val pageDate: Date):
    RecyclerView.Adapter<PlannedWorkoutsAdapter.PlannedWorkoutsViewHolder>() {

    private var workoutsForToday = ArrayList<Workout>()
    private var isWorkoutForToday: Boolean? = null

    override fun getItemCount(): Int {
        if (isWorkoutForToday == null) {
            checkIfIsWorkoutForToday()
        }
        return if (isWorkoutForToday == true) {
            workoutsForToday.size
        } else {
            1
        }
    }

    override fun onBindViewHolder(holder: PlannedWorkoutsViewHolder, position: Int) {
        if (isWorkoutForToday == null) {
            checkIfIsWorkoutForToday()
        }
        if (workoutsForToday.size == 0) {
            holder.itemView.cardView_workout.visibility = View.GONE
            holder.itemView.cardView_addAnother.visibility = View.VISIBLE
            holder.itemView.workoutCard_addAnother.setOnClickListener {
                //przeniesc do dodawania treningu do dnia
            }
        } else {
            holder.itemView.cardView_addAnother.visibility = View.GONE
            holder.itemView.cardView_workout.visibility = View.VISIBLE
            val workout = workoutsForToday[position]
            holder.itemView.workoutcard_name.text = workout.name
            holder.itemView.workoutcard_exercisesNumber.text = workout.exercises?.size.toString()
            workout.initDate?.let {
                val workoutDate = workout.initDate?.time
                workoutDate?.let {
                    val initDate = Date(workoutDate)
                    val formatter = SimpleDateFormat("dd.MM.yyyy")
                    val formattedDate = formatter.format(initDate)
                    val localeFormat = SimpleDateFormat("EEEE", Locale.forLanguageTag("en-us"))
                    val dayOfWeek = localeFormat.format(initDate)
                    holder.itemView.workoutcard_inittime.text = "$dayOfWeek, $formattedDate"
                }

            }
            val exercisesInThisWorkout = workout.exercises
            exercisesInThisWorkout?.let {
                if (!exercisesInThisWorkout.isNullOrEmpty()) {
                    viewAdapter = WorkoutElementItemsAdapter(workoutElementItems = exercisesInThisWorkout)
                    val recyclerView = holder.itemView.workoutcard_items
                    recyclerView.apply {
                        adapter = viewAdapter
                        layoutManager = LinearLayoutManager(context)
                    }
                }
            }
            holder.itemView.workoutcard_logworkout_button.setOnClickListener {
                //                val logWorkoutDialog = LogWorkoutDialog(context, workout, listener)
//                logWorkoutDialog.show()
            }
            holder.itemView.workoutcard_logworkout_button.visibility = View.VISIBLE
            holder.itemView.workoutcard_planButton.visibility = View.GONE
        }
    }

    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    class PlannedWorkoutsViewHolder(cardView: CardView): RecyclerView.ViewHolder(cardView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlannedWorkoutsViewHolder {
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.workout_cardview, parent, false) as CardView
        checkIfIsWorkoutForToday()
        return PlannedWorkoutsViewHolder(cardView)
    }

    private fun checkIfIsWorkoutForToday() {
        if (plannedWorkouts.size > 0) {
            plannedWorkouts.forEach { plannedWorkout ->
                val plannedDate = plannedWorkout.date?.toDate()
                if (plannedDate == pageDate) {
                    plannedWorkout.workout?.let {
                        workoutsForToday.add(it)
                    }
                }
            }
        }
        else {
            isWorkoutForToday = false
        }
    }

}