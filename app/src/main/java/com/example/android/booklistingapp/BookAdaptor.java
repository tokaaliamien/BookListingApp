package com.example.android.booklistingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.resource;

/**
 * Created by Demo on 2017-08-09.
 */

public class BookAdaptor extends ArrayAdapter {
    public BookAdaptor(Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Book currentBook = (Book) getItem(position);

        TextView titleTextView = (TextView) listItem.findViewById(R.id.title);
        String title = currentBook.getTitle();
        titleTextView.setText(title);

        TextView autherTextView = (TextView) listItem.findViewById(R.id.auther);
        String auther = currentBook.getAuther();
        autherTextView.setText(auther);

        return listItem;
    }
}
