package com.barapraja.isatds.stock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.barapraja.isatds.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/** g
 * Created by Tri Sutrisno on 11/05/2017.
 */

public class ViewStock extends AppCompatActivity {
    ExpandableListAdapter listAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    // get the listview
    @BindView(R.id.list_stocksale) ExpandableListView expListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewstock);
        ButterKnife.bind(this);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(ViewStock.this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });


    }

    /*
      * Preparing the list data
      */
    private void prepareListData() {

            listDataHeader = new ArrayList<String>();
            listDataChild = new HashMap<String, List<String>>();

            // Adding child data
            listDataHeader.add("Stock Sold");
            listDataHeader.add("Stock Available");

            // Adding child data
            List<String> top250 = new ArrayList<String>();
            top250.add("0564645654656");
            top250.add("65654654656546");
            top250.add("6556457745757547");
            top250.add("757457547547");
            top250.add("7547574754");
            top250.add("745745745757");
            top250.add("745745747457457");

            List<String> nowShowing = new ArrayList<String>();
            nowShowing.add("547474574");
            nowShowing.add("D47475745");
            nowShowing.add("474575474");
            nowShowing.add("4754754754");
            nowShowing.add("754754754754");
            nowShowing.add("47547547547457");

            listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
            listDataChild.put(listDataHeader.get(1), nowShowing);
        }
}

