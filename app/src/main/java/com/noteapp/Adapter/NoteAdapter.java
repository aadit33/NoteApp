package com.noteapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.noteapp.Data.DatabaseHelper;
import com.noteapp.Model.NoteData;
import com.noteapp.R;
import com.noteapp.Widgets.Constant;
import com.noteapp.Widgets.GlideApp;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<NoteData> itemList;
    private Context context;
    private NoteAdapter.OnItemClickListener mItemClickListener;
    private final int VIEW_TYPE_LOADING = 0;
    private static final int TYPE_ONE = 1;
    private DatabaseHelper databaseHelper;


    public NoteAdapter(ArrayList<NoteData> itemList, Context context, DatabaseHelper databaseHelper) {
        this.itemList = itemList;
        this.context = context;
        this.databaseHelper = databaseHelper;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        NoteData pos = itemList.get(position);

        NoteAdapter.ViewHolder holder = (NoteAdapter.ViewHolder) holder1;
        holder.noteTitle.setText(pos.getTitle());
        holder.noteContent.setText(pos.getContent());

        if (pos.getSnap() != null) {
            if (!pos.getSnap().equals("")) {
                holder.noteImage.setVisibility(View.VISIBLE);
                holder.noteImage.setImageBitmap(Constant.getBitmapFromBase64(pos.getSnap()));
            } else {
                holder.noteImage.setVisibility(View.GONE);
            }
        }

        //delete notes
        holder.close.setOnClickListener(new close(position, holder));

        if (pos.getColor() != null)
            holder.rootLayout.setBackgroundColor(Color.parseColor(pos.getColor()));


    }


    @Override
    public int getItemCount() {
        return this.itemList == null ? 0 : this.itemList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView noteTitle, noteContent;
        private ImageView noteImage, close;
        private CardView rootLayout;


        ViewHolder(View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.card_view);
            noteTitle = itemView.findViewById(R.id.note_title);
            noteContent = itemView.findViewById(R.id.note_content);
            noteImage = itemView.findViewById(R.id.note_image);
            close = itemView.findViewById(R.id.close);
            rootLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final NoteAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


    private class close implements View.OnClickListener {

        int position;
        NoteAdapter.ViewHolder hld;


        public close(int position, NoteAdapter.ViewHolder hld) {
            this.position = position;
            this.hld = hld;
        }

        @Override
        public void onClick(View v) {

            databaseHelper.deleteNotifications(itemList.get(position).getNoteId());
            itemList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, itemList.size());


        }
    }

}


