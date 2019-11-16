package we.zxlite.activity

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_guess.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject
import we.zxlite.R
import we.zxlite.adapter.GuessItemAdapter
import we.zxlite.dialog.SelectFriendsDialog
import we.zxlite.utils.BaseUtils.EMPTY_STR
import we.zxlite.utils.HttpUtils.Error
import we.zxlite.utils.HttpUtils.Type.JsonObject
import we.zxlite.utils.HttpUtils.Type.JsonArray
import we.zxlite.utils.HttpUtils.api
import we.zxlite.utils.UserUtils.cfg

class GuessActivity : BaseActivity() {

    companion object {
        //获取好友
        private const val FRIENDS_URL = "https://app.zhixue.com/study/social/t/getFriends?"
        //获取分数
        private const val GUESS_URL = "https://app.zhixue.com/study/social/getGuessScore"
        //获取班级
        private const val GRADE_URL = "https://www.zhixue.com/zhixuebao/studentPK/getClazzByGradeId"
        //获取同学
        private const val CLAZZ_URL =
            "https://www.zhixue.com/zhixuebao/studentPK/getStudentByClazzId"

        private const val SUBJECT_LIST = "subjectList"
        private const val SUBJECT_CODE = "subjectCode"
        private const val STUDENT_PK_DTOS = "studentPKDTOs"
        private const val EXAM_ID = "examId"
        private const val FRIENDS = "friends"
        private const val USER_NAME = "userName"
        private const val USER_ID = "userId"
        private const val NAME_LIST = "nameList"
        private const val IS_SELF = "isSelf"
        private const val CLAZZ_ID = "clazzId"
        private const val STUDENT_ID = "studentId"
        private const val STUDENT_NAME = "studentName"
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
        guessSelect.setOnLongClickListener {
            toast(R.string.toastWait)
            launch {
                api(GRADE_URL, "token=${cfg.serviceToken}", true, JsonArray).let {
                    if (it is JSONArray)
                        for (i in 0 until it.length()) {
                            val clazz = it.optJSONObject(i)
                            if (clazz.optBoolean(IS_SELF))
                                return@let clazz.optString(CLAZZ_ID)
                        } else null
                }?.let {
                    api(CLAZZ_URL, "clazzId=$it&token=${cfg.serviceToken}", true, JsonArray).run {
                        if (this is JSONArray) {
                            friendsId.clear()
                            friendsName.clear()
                            for (i in 0 until length()) {
                                val friends = optJSONObject(i).optJSONArray(FRIENDS)
                                for (f in 0 until friends!!.length()) {
                                    val friend = friends.optJSONObject(f)
                                    friendsId.add(f, friend.optString(STUDENT_ID))
                                    friendsName.add(f, friend.optString(STUDENT_NAME))
                                }
                            }
                            withContext(Main) {
                                toast(R.string.addSuccess)
                            }
                        }
                    }
                }
            }
            return@setOnLongClickListener true
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
