package com.noteapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.noteapp.Adapter.NoteAdapter;
import com.noteapp.Data.DatabaseHelper;
import com.noteapp.Model.NoteData;
import com.noteapp.Widgets.GridSpacingItemDecoration;

import java.util.ArrayList;

public class NoteListActivity extends AppCompatActivity {

    private RecyclerView noteRecycler;
    ArrayList<NoteData> noteList;
    ArrayList<NoteData> listViewItems;
    Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
    private DatabaseHelper databaseHelper;
    private NoteAdapter noteAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        databaseHelper = new DatabaseHelper(this);
        init();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNoteAdd(""
                        , ""
                        , ""
                        , ""
                        , -1
                        , ""
                        , getResources().getString(R.color.colorNoteDefault));
            }
        });

    }

    private void init() {
        bindResources();
    }

    private void bindResources() {
        noteRecycler = findViewById(R.id.note_recycler);
        noteRecycler.setHasFixedSize(true);
        setRecyclerview();
    }

    private void setRecyclerview() {
        // Check orientation to put the good amount of columns
        int column = 2;
        if (getResources().getConfiguration().orientation == 2)
            column = 3;

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(column, 1);
        noteRecycler.setLayoutManager(staggeredGridLayoutManager);
        // Prevent the loss of items
        noteRecycler.getRecycledViewPool().setMaxRecycledViews(0, 0);

        final float scale = getResources().getDisplayMetrics().density;
        int spacing = (int) (1 * scale + 0.5f);
        noteRecycler.addItemDecoration(new GridSpacingItemDecoration(spacing));


        // Load notes from Db
        noteList = loadNotes();

        noteAdapter = new NoteAdapter(noteList, this, databaseHelper);
        noteRecycler.setAdapter(noteAdapter);

        NoteAdapter.OnItemClickListener onItemClickListener = new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                NoteData noteData = getSavedloadNotes().get(position);
                goToNoteAdd(noteData.getNoteId()
                        , noteData.getTitle()
                        , noteData.getContent()
                        , noteData.getCreationDate()
                        , position
                        , noteData.getSnap()
                        , noteData.getColor());

            }
        };
        noteAdapter.setOnItemClickListener(onItemClickListener);


    }

    private void goToNoteAdd(String noteId, String title, String content, String creationDate, int position, String snap, String color) {
        Intent simpleNoteIntent = new Intent(getApplicationContext(), NoteAddActivity.class);
        simpleNoteIntent.putExtra("note_id", noteId);
        simpleNoteIntent.putExtra("title", title);
        simpleNoteIntent.putExtra("content", content);
        simpleNoteIntent.putExtra("creationDate", creationDate);
        simpleNoteIntent.putExtra("color", color);
        simpleNoteIntent.putExtra("position", position);
        simpleNoteIntent.putExtra("image", snap);
        // TODO
        startActivityForResult(simpleNoteIntent, 1);

        overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);

    }

    // Get the data from the note creation
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String noteJSON = data.getStringExtra("noteJSON");
            int notePosition = data.getIntExtra("notePosition", 0);
            //make this global
            String noteId = data.getStringExtra("noteId");
            NoteData noteData = gson.fromJson(noteJSON, NoteData.class);
            if (notePosition > -1) {
                //update the notes data in database if note position is > -1 and refresh the recycler adapter wrt to position

                listViewItems.remove(notePosition);
                databaseHelper.upDateNoti(noteData.getTitle(), noteData.getContent(), noteId, noteData.getSnap(),noteData.getColor());
                listViewItems.add(notePosition, noteData);
                noteAdapter.notifyItemChanged(notePosition);
            } else {
                //add the note data to database and refresh the adapter
                databaseHelper.addNote(noteData);
                listViewItems.add(noteData);
                noteAdapter.notifyDataSetChanged();
            }
        }
    }

    //get the notes from database
    private ArrayList<NoteData> loadNotes() {
        listViewItems = new ArrayList<>();
        listViewItems = databaseHelper.getAllNotes();
        return listViewItems;
    }

    private ArrayList<NoteData> getSavedloadNotes() {
        ArrayList<NoteData> newViewItems;
        newViewItems = databaseHelper.getAllNotes();
        return newViewItems;
    }


}
