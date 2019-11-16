package we.zxlite.activity

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_guess.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.toast
import org.json.JSONObject
import we.zxlite.R
import we.zxlite.adapter.GuessItemAdapter
import we.zxlite.dialog.SelectDialog
import we.zxlite.utils.BaseUtils
import we.zxlite.utils.HttpUtils.Error
import we.zxlite.utils.HttpUtils.Type.JsonObject
import we.zxlite.utils.HttpUtils.api

class GuessActivity : BaseActivity() {

    companion object {
        //获取好友
        private const val FRIENDS_URL = "https://app.zhixue.com/study/social/t/getFriends?"
        private const val GUESS_URL = "https://app.zhixue.com/study/social/getGuessScore"

        private const val EXAM_ID = "examId"
        private const val FRIENDS = "friends"
        private const val USER_NAME = "userName"
        private const val USER_ID = "userId"
        private const val NAME_LIST = "nameList"
        private const val SUBJECT_LIST = "subjectList"
        private const val SUBJECT_CODE = "subjectCode"
        private const val STUDENT_PK_DTOS = "studentPKDTOs"
    }

    private var friendsId = mutableListOf<String>()
    private var friendsName = mutableListOf<String>()

    private var myList = mutableMapOf<Int, JSONObject>()
    private var guessList = mutableMapOf<String, JSONObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guess)
        getFriends()
    }

    override fun initView() {
        setSupportActionBar(guessBar)
        guessBar.setNavigationOnClickListener { onBackPressed() }
        guessRecycler.adapter = GuessItemAdapter(myList, guessList)
        guessSelect.setOnClickListener {
            if (friendsId.isNotEmpty() && friendsName.isNotEmpty()) SelectDialog { which ->
                guessSelectText.text = friendsName[which]
                myList.clear()
                guessList.clear()
                guessRecycler.adapter!!.notifyDataSetChanged()
                val guessParams =
                    "examId=${intent.getStringExtra(EXAM_ID)}&guessUserId=${friendsId[which]}"
                launch {
                    api(GUESS_URL, guessParams, true, JsonObject).let {
                        if (it is JSONObject) {
                            val array = it.optJSONArray(STUDENT_PK_DTOS)
                            if (array != null) {
                                val myScores = array.optJSONObject(0).optJSONArray(SUBJECT_LIST)
                                for (i in 0 until myScores!!.length()) {
                                    myList[i] = myScores.optJSONObject(i)
                                }
                                val friendScores =
                                    array.optJSONObject(1).optJSONArray(SUBJECT_LIST)
                                for (i in 0 until friendScores!!.length()) {
                                    val json = friendScores.optJSONObject(i)
                                    var code = json.optString(SUBJECT_CODE)
                                    if (code.isEmpty()) code = "0"
                                    guessList[code] = json
                                }
                                withContext(Main) {
                                    guessRecycler.adapter!!.notifyDataSetChanged()
                                }
                            } else Unit
                        } else withContext(Main) {
                            toast((it as Error).message)
                        }
                    }
                }
            }.apply {
                arguments = Bundle().apply {
                    putStringArray(NAME_LIST, friendsName.toTypedArray())
                }
            }.show(supportFragmentManager, BaseUtils.EMPTY_STR)
            else toast(R.string.noFriends)
        }
    }

    private fun getFriends() {
        launch {
            api(FRIENDS_URL, null, true, JsonObject).let {
                if (it is JSONObject) {
                    val friendArray = it.optJSONArray(FRIENDS)
                    for (i in 0 until friendArray!!.length()) {
                        val json = friendArray.optJSONObject(i)
                        friendsName.add(i, json.optString(USER_NAME))
                        friendsId.add(i, json.optString(USER_ID))
                    }
                }
            }
        }
    }
}
