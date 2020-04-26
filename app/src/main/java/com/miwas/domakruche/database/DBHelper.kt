package com.miwas.domakruche.database

import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.miwas.domakruche.utils.Constants.TABLE_NAME

class DBHelper(
	context: Context?,
	name: String?,
	factory: SQLiteDatabase.CursorFactory?,
	version: Int,
	errorHandler: DatabaseErrorHandler?
) : SQLiteOpenHelper(context, name, factory, version, errorHandler) {

	override fun onCreate(db: SQLiteDatabase) {
		db.execSQL(CREATE_TABLE)
	}

	override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
		if (oldVersion != newVersion) {
			db.execSQL(DROP_TABLE)
			onCreate(db)
		}
	}

	companion object {
		const val CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
			"( _id INTEGER PRIMARY KEY, " +
			" location_latitude TEXT, " +
			" location_longitude TEXT);"

		const val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
	}
}