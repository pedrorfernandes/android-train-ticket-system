package org.feup.cmov.userticketapp.Controllers;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.feup.cmov.userticketapp.Models.ErrorResponse;
import org.feup.cmov.userticketapp.Models.SharedDataSingleton;
import org.feup.cmov.userticketapp.Models.Station;
import org.feup.cmov.userticketapp.Models.Timetable;
import org.feup.cmov.userticketapp.R;
import org.feup.cmov.userticketapp.Services.GetTimetables;

import java.util.ArrayList;

public class TimetableActivity extends AppCompatActivity {

    private SharedDataSingleton sharedData = SharedDataSingleton.getInstance();
    private RecyclerView mRecyclerView;
    private TimetableAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        Station selectedStation = sharedData.getSelectedStation();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(String.format(getString(R.string.title_activity_timetable), selectedStation.getName()));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.timetable_recycler_view);
        mRecyclerView.setHasFixedSize(false);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new TimetableAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        final Context context = this;

        new GetTimetables(this, new GetTimetables.OnGetTimetableTaskCompleted() {
            @Override
            public void onTaskCompleted(ArrayList<Timetable> timetables) {
                mAdapter.setTimetable(timetables);
            }

            @Override
            public void onTaskError(ErrorResponse error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(selectedStation);
    }

}
