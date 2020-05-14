package com.teme.addtorv

import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.teme.addtorv.room.AppDatabase
import com.teme.addtorv.room.User
import kotlinx.android.synthetic.main.fragment_one.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import kotlin.random.Random

/**
 * A simple [Fragment] subclass.
 */
class FragmentOne : Fragment() {

    private lateinit var users: List<User>
    private var userz: ArrayList<User> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //this.userz = ArrayList()

        AppDatabase.getAppDatabase(activity!!)

        recycler_view.layoutManager = LinearLayoutManager(activity)

        insertUsersInRecyclerViewFromDB()

        btn_add_user.setOnClickListener {
            val fn = tv_first_name.text.toString()
            val ln = tv_last_name.text.toString()
            insertUser(User(Random.nextInt(10000), fn, ln))
        }

        btn_remove_user.setOnClickListener {
            val firstName = tv_first_name.text.toString()
            val lastName = tv_last_name.text.toString()
            deleteUser(firstName, lastName)
        }

        btn_delete_all.setOnClickListener {
            deleteAllUsers()
        }

        btn_sync.setOnClickListener {
            syncWithServer()
        }

    }

    private fun insertUsersInRecyclerViewFromDB() {
        AsyncTask.execute {
            users = AppDatabase.getAppDatabase(activity!!).userDao().getAll()
            userz.addAll(users)
            recycler_view.adapter = Adapter(userz)
        }
    }

    private fun insertUser(user: User) {

        for (us in userz) {
            if (us.firstName == user.firstName && us.lastName == user.lastName) {
                Toast.makeText(context, "User already exists!", Toast.LENGTH_SHORT).show()
                return
            }
        }

        userz.add(user)

        /*class InsertUser : AsyncTask<Void, Void, Void>(){
            override fun doInBackground(vararg params: Void?): Void? {
                AppDatabase.getAppDatabase(activity!!).userDao().insert(user)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                Toast.makeText(context, "User Inserted!", Toast.LENGTH_SHORT).show()
            }
        }

        InsertUser().execute()*/

        AsyncTask.execute {
            AppDatabase.getAppDatabase(activity!!).userDao().insert(user)
        }

        recycler_view.adapter?.notifyItemInserted(userz.size - 1)

    }

    private fun deleteUser(firstName: String, lastName: String) {

        if(userz.isEmpty()){
            Toast.makeText(context, "User not found!", Toast.LENGTH_SHORT).show()
            return
        }

        var userFound = false
        for (i in 0..userz.size) {
            if (userz[i].firstName == firstName && userz[i].lastName == lastName) {
                userz.removeAt(i)
                recycler_view.adapter?.notifyItemRemoved(i)
                userFound = true
                break
            }
        }

        if (!userFound) {
            Toast.makeText(context, "User not found!", Toast.LENGTH_SHORT).show()
            return
        }

        AsyncTask.execute {
            AppDatabase.getAppDatabase(activity!!).userDao().deleteUser(firstName, lastName)
        }
    }

    private fun deleteAllUsers() {

        userz.removeAll(userz)

        class DeleteAllUsers : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void?): Void? {
                AppDatabase.getAppDatabase(activity!!).userDao().deleteAll()
                return null
            }
        }
        DeleteAllUsers().execute()

        recycler_view.adapter?.notifyDataSetChanged()

    }

    private fun syncWithServer() {

        val url = "https://jsonplaceholder.typicode.com/users"

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Failure")
            }

            override fun onResponse(call: Call, response: Response) {

                val body = response.body()?.string()
                println(body)

                val jsonArray = JSONArray(body)

                var i = 0
                while (i < jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)

                    val name = jsonObject.getString("name")
                    val firstAndLastName = name.split(" ")
                    val firstName = firstAndLastName[0]
                    val lastName = firstAndLastName[1]
                    val user =
                        User(Random.nextInt(10000), firstName = firstName, lastName = lastName)
                    //usersFromJson.add(user)
                    userz.add(user)
                    AsyncTask.execute {
                        AppDatabase.getAppDatabase(activity!!).userDao().insert(user)
                    }
                    i++
                }
                activity?.runOnUiThread {
                    recycler_view.adapter?.notifyDataSetChanged()
                }
            }
        })

    }
}
