package com.diarmaidlindsay.koohii.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.support.v4.widget.SimpleCursorAdapter;
import com.diarmaidlindsay.koohii.database.dao.KeywordDataSource;
import com.diarmaidlindsay.koohii.database.dao.PrimitiveDataSource;
import com.diarmaidlindsay.koohii.model.Keyword;
import com.diarmaidlindsay.koohii.model.Primitive;

import java.util.*;

/**
 * For the suggestions list of the searchview inside the main kanji list activity
 */
public class SuggestionsAdapter extends SimpleCursorAdapter {
    private List<Keyword> allKeywords;
    private List<Primitive> allPrimitives;
    private List<String> suggestionsList;
    private String previousQuery;

    public SuggestionsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        PrimitiveDataSource primitiveDataSource = new PrimitiveDataSource(context);
        KeywordDataSource keywordDataSource = new KeywordDataSource(context);
        primitiveDataSource.open();
        keywordDataSource.open();
        allKeywords = keywordDataSource.getAllKeywords();
        allPrimitives = primitiveDataSource.getAllPrimitives();
        suggestionsList = new ArrayList<>();
        keywordDataSource.close();
        primitiveDataSource.close();
    }

    public void populateSuggestions(String query)
    {
        final MatrixCursor cursor = new MatrixCursor(new String[]{ BaseColumns._ID, "keywordPrimitive" });

        if(query.length() < 2) {
            suggestionsList.clear();
            previousQuery = null;
            changeCursor(cursor);
            return;
        }
        query = query.toLowerCase();
        Set<String> suggestionsSet = new HashSet<>();

        //some text was deleted so we should fall back to suggest from all primitives and keywords
        if(previousQuery == null || query.length() < previousQuery.length()) {
            for (Primitive primitive : allPrimitives) {
                if (primitive.getPrimitiveText().toLowerCase().contains(query)) {
                    suggestionsSet.add(primitive.getPrimitiveText());
                }
            }

            for (Keyword keyword : allKeywords) {
                if (keyword.getKeywordText().toLowerCase().contains(query)) {
                    suggestionsSet.add(keyword.getKeywordText());
                }
            }
        }
        //search the subset of results from the previous query
        else
        {
            for(String text : suggestionsList) {
                if(text.toLowerCase().contains(query)) {
                    suggestionsSet.add(text);
                }
            }
        }

        suggestionsList = new ArrayList<>(suggestionsSet);
        Collections.sort(suggestionsList, new SortIgnoreCase());

        for(int i = 0; i < suggestionsList.size(); i++) {
            cursor.addRow(new Object[]{i, suggestionsList.get(i)});
        }
        previousQuery = query;
        changeCursor(cursor);
    }

    public class SortIgnoreCase implements Comparator<Object> {
        public int compare(Object o1, Object o2) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    }
}
