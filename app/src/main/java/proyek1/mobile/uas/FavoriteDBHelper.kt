package proyek1.mobile.uas

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class FavoriteDbHelper(context: Context) :
    SQLiteOpenHelper(context, "favorites.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE favorites (
                id INTEGER PRIMARY KEY,
                user_id INTEGER,
                name TEXT,
                description TEXT,
                location TEXT,
                price TEXT,
                size TEXT,
                bedrooms TEXT,
                bathrooms TEXT,
                photo TEXT,
                status TEXT,
                type TEXT
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS favorites")
        onCreate(db)
    }

    fun insertFavorite(fav: Favorite) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", fav.id)
            put("user_id", fav.userId)
            put("name", fav.name)
            put("description", fav.description)
            put("location", fav.location)
            put("price", fav.price)
            put("size", fav.size)
            put("bedrooms", fav.bedrooms)
            put("bathrooms", fav.bathrooms)
            put("photo", fav.photo)
            put("status", fav.status)
            put("type", fav.type)
        }
        db.insertWithOnConflict("favorites", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getAllFavorites(): List<Favorite> {
        val favorites = mutableListOf<Favorite>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM favorites", null)

        if (cursor.moveToFirst()) {
            do {
                val favorite = Favorite(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    location = cursor.getString(cursor.getColumnIndexOrThrow("location")),
                    price = cursor.getString(cursor.getColumnIndexOrThrow("price")),
                    size = cursor.getString(cursor.getColumnIndexOrThrow("size")),
                    bedrooms = cursor.getString(cursor.getColumnIndexOrThrow("bedrooms")),
                    bathrooms = cursor.getString(cursor.getColumnIndexOrThrow("bathrooms")),
                    photo = cursor.getString(cursor.getColumnIndexOrThrow("photo")),
                    status = cursor.getString(cursor.getColumnIndexOrThrow("status")),
                    type = cursor.getString(cursor.getColumnIndexOrThrow("type"))
                )
                favorites.add(favorite)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return favorites
    }


    fun clearFavorites() {
        writableDatabase.execSQL("DELETE FROM favorites")
    }
}
