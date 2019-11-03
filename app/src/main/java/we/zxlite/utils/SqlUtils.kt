package we.zxlite.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

object SqlUtils {

    private const val DB_NAME = "Zxlite"

    class Helper private constructor(ctx: Context) :
        ManagedSQLiteOpenHelper(ctx, DB_NAME, null, 1) {
        init {
            instance = this
        }

        companion object {
            const val TABLE_RMB = "TABLE_RMB"
            const val TABLE_CFG = "TABLE_CFG"
            const val ITEM_NAME = "ITEM_NAME"
            const val ITEM_VALUE = "ITEM_VALUE"
            const val SELECT_USER = "SELECT_USER"

            private var instance: Helper? = null

            @Synchronized
            fun getInstance(ctx: Context) = instance ?: Helper(ctx.applicationContext)
        }

        override fun onCreate(db: SQLiteDatabase) {
            db.createTable(TABLE_RMB, true, ITEM_NAME to TEXT + UNIQUE, ITEM_VALUE to TEXT)
            db.createTable(TABLE_CFG, true, ITEM_NAME to TEXT + UNIQUE, ITEM_VALUE to TEXT)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        }
    }

}