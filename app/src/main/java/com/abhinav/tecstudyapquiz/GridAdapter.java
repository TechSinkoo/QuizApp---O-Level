package com.abhinav.tecstudyapquiz;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class GridAdapter extends BaseAdapter {



    private List<String> sets;
    private String category;


    public GridAdapter(List<String> sets, String category) {
        this.sets = sets;
        this.category = category;
    }

    @Override
    public int getCount() {
        return sets.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, final ViewGroup viewGroup) {
        final View v1;
       // TextView txt = view.findViewById(R.id.textviewd);
        if(view==null)
        {
            v1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.set_item,viewGroup,false);

        }
        else
            {
                v1 = view;
            }

        v1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                        Intent questionIntent = new Intent(viewGroup.getContext(),QuestionsActivity.class);
                        questionIntent.putExtra("category",category);
                        questionIntent.putExtra("setId",sets.get(i));
                        viewGroup.getContext().startActivity(questionIntent);



//                Intent questionIntent = new Intent(viewGroup.getContext(),QuestionsActivity.class);
//                questionIntent.putExtra("category",category);
//                questionIntent.putExtra("setId",sets.get(i));
//                viewGroup.getContext().startActivity(questionIntent);


            }

        });




          ((TextView) v1.findViewById(R.id.gridtextView)).setText(String.valueOf(i+1));







        return v1;
    }



}
