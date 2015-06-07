package com.diarmaidlindsay.koohii.database.dao;

import android.content.Context;
import android.database.Cursor;
import com.diarmaidlindsay.koohii.database.DatabaseAssetHelper;
import com.diarmaidlindsay.koohii.model.HeisigKanji;
import com.diarmaidlindsay.koohii.model.Keyword;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Keyword table
 */
public class KeywordDataSource extends CommonDataSource {

    private final String COLUMN_ID = "heisig_id";
    private final String COLUMN_KEYWORD = "keyword_text";
    private String[] allColumns = {COLUMN_ID, COLUMN_KEYWORD};

    public KeywordDataSource(Context context) {
        super(context);
    }

    public Keyword getKeywordForHeisigKanjiId(int id)
    {
        Cursor cursor = database.query(DatabaseAssetHelper.TABLE_KEYWORD,
                allColumns, COLUMN_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        Keyword keyword = cursorToKeyword(cursor);
        cursor.close();
        return keyword;
    }

    public List<Keyword> getKeywordsForHeisigKanjiObjects(List<HeisigKanji> heisigKanjiList)
    {
        String[] ids = HeisigKanji.getIds(heisigKanjiList);
        List<Keyword> keywordList = new ArrayList<>();

        Cursor cursor = database.query(DatabaseAssetHelper.TABLE_KEYWORD,
                allColumns, COLUMN_ID+ " IN (?)", ids, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            keywordList.add(cursorToKeyword(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return keywordList;
    }

    public List<Keyword> getAllKeywords()
    {
        List<Keyword> keywordList = new ArrayList<>();
        String orderBy =  COLUMN_ID + " ASC";

        Cursor cursor = database.query(DatabaseAssetHelper.TABLE_KEYWORD,
                allColumns, null, null, null, null, orderBy);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            keywordList.add(cursorToKeyword(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return keywordList;
    }

    private Keyword cursorToKeyword(Cursor cursor) {
        return new Keyword(
                cursor.getInt(0),
                cursor.getString(1));
    }
}