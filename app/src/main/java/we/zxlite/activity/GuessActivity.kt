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
import we.zxlite.dialog.SelectFriendsDialog
import we.zxlite.utils.BaseUtils.EMPTY_STR
import we.zxlite.utils.HttpUtils.Error
import we.zxlite.utils.HttpUtils.Type.JsonObject
import we.zxlite.utils.HttpUtils.api

class GuessActivity : BaseActivity() {

    companion object {
        //获取好友
        private const val FRIENDS_URL = "https://app.zhixue.com/study/social/t/getFriends?"
        //获取分数
        private const val GUESS_URL = "https://app.zhixue.com/study/social/getGuessScore"

        private const val SUBJECT_LIST = "subjectList"
        private const val SUBJECT_CODE = "subjectCode"
        private const val STUDENT_PK_DTOS = "studentPKDTOs"
        private const val EXAM_ID = "examId"
        private const val FRIENDS = "friends"
        private const val USER_NAME = "userName"
        private const val USER_ID = "userId"
        private const val NAME_LIST = "nameList"
    }

    private var friendsId = mutableListOf<String>()
    private var friendsName = mutableListOf<String>()

    private var mList = mutableMapOf<Int, JSONObject>()
    private var fList = mutableMapOf<String, JSONObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guess)
        loadFriends()
    }

    override fun initView() {
        setSupportActionBar(guessBar)
        guessBar.setNavigationOnClickListener { onBackPressed() }
        guessRecycler.adapter = GuessItemAdapter(mList, fList)
        guessSelect.setOnClickListener {
            if (friendsId.isNotEmpty() && friendsName.isNotEmpty()) SelectFriendsDialog { i ->
                mList.clear()
                fList.clear()
                guessSelectText.text = friendsName[i]
                guessRecycler.adapter!!.notifyDataSetChanged()
                loadScores(i)
            }.apply {
                arguments = Bundle().apply { putStringArray(NAME_LIST, friendsName.toTypedArray()) }
            }.show(supportFragmentManager, EMPTY_STR)
            else toast(R.string.noFriends)
        }
    }

    private fun loadScores(position: Int) {
        launch {
            val guessParams =
                "examId=${intent.getStringExtra(EXAM_ID)}&guessUserId=${friendsId[position]}"
            api(GUESS_URL, guessParams, true, JsonObject).let {
                if (it is JSONObject) {
                    val dtos = it.optJSONArray(STUDENT_PK_DTOS)
                    if (dtos != null) {
                        val mScores = dtos.optJSONObject(0).optJSONArray(SUBJECT_LIST)
                        for (i in 0 until mScores!!.length()) {
                            mList[i] = mScores.optJSONObject(i)
                        }

                        val fScores = dtos.optJSONObject(1).optJSONArray(SUBJECT_LIST)
                        for (i in 0 until fScores!!.length()) {
                            val json = fScores.optJSONObject(i)
                            val code = json.optString(SUBJECT_CODE, "0")
                            fList[code] = json
                        }

                        withContext(Main) {
                            guessRecycler.adapter!!.notifyItemRangeInserted(0, mList.size)
                        }
                    } else Unit
                } else withContext(Main) {
                    toast((it as Error).message)
                }
            }
        }
    }

    private fun loadFriends() {
        launch {
            api(FRIENDS_URL, null, true, JsonObject).let {
                if (it is JSONObject) {
                    val friendArray = it.optJSONArray(FRIENDS)
                    for (i in 0 until friendArray!!.length()) {
                        val friend = friendArray.optJSONObject(i)
                        friendsId.add(i, friend.optString(USER_ID))
                        friendsName.add(i, friend.optString(USER_NAME))
                    }
                }
            }
        }
    }
}
