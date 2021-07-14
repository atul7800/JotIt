package com.atulgupta.JotIt.model;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.atulgupta.JotIt.note.NoteDetails;
import com.atulgupta.JotIt.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    List<String> titles;
    List<String> content;

    public Adapter(List<String> titles, List<String> content) {
        this.titles = titles;
        this.content = content;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_view_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.noteTitle.setText(titles.get(position));
        holder.noteContent.setText(content.get(position));
        final int contentsBackgroundColorCode = getRandomColor();
        holder.mCardView.setCardBackgroundColor(holder.view.getResources().getColor(contentsBackgroundColorCode, null));


        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), NoteDetails.class);
                i.putExtra("title", titles.get(position));
                i.putExtra("content", content.get(position));
                i.putExtra("color", contentsBackgroundColorCode);
                v.getContext().startActivity(i);

            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView noteTitle, noteContent;
        View view;
        CardView mCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTitle = itemView.findViewById(R.id.titles);
            noteContent = itemView.findViewById(R.id.content);
            view = itemView;
            mCardView = itemView.findViewById(R.id.noteCard);

        }
    }

    private int getRandomColor()
    {
        List<Integer> colorscode = new ArrayList<>();
        colorscode.add(R.color.blue);
        colorscode.add(R.color.skyblue);
        colorscode.add(R.color.lightPurple);
        colorscode.add(R.color.yellow);
        colorscode.add(R.color.lightGreen);
        colorscode.add(R.color.pink);
        colorscode.add(R.color.gray);
        colorscode.add(R.color.notgreen);
        colorscode.add(R.color.red);
        colorscode.add(R.color.greenlight);

        Random randomcolor  = new Random();
        int number = randomcolor.nextInt(colorscode.size());

        return colorscode.get(number);
    }
}
