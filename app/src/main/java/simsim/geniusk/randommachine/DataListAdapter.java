package simsim.geniusk.randommachine;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by young on 2017-05-07.
 */

public class DataListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private RecyclerView mRecyclerView;
    private ArrayList<HashMap<String, String>> mData;
    private final String dbName = "random_is";
    private final String tableName = "absolute";
    private SQLiteDatabase sampleDB = null;
    private MainActivity mainActivity;
    private EditText mRand_text;

    public DataListAdapter(Context context, ArrayList<HashMap<String, String>> data, EditText rand_text) {
        mContext = context;
        mData = data;
        mainActivity = new MainActivity();
        mRand_text = rand_text;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder1;
        view = LayoutInflater.from(mContext).inflate(R.layout.random_list_adapter, parent, false);
        viewHolder1 = new GridViewHolder(view);
        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final GridViewHolder viewholder = (GridViewHolder) holder;
        viewholder.list_texv.setText(mData.get(position).get("list"));
        viewholder.list_texv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRand_text.setText(mData.get(position).get("list"));
            }
        });
        viewholder.result_texv.setText(mData.get(position).get("result"));
        viewholder.delete_texv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sampleDB = mContext.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
                sampleDB.execSQL("DELETE FROM " + tableName + " WHERE idx = " + mData.get(position).get("idx"));
                sampleDB.close();
                mainActivity.finish();
                Intent intent = new Intent(mContext, mainActivity.getClass());
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class GridViewHolder extends RecyclerView.ViewHolder {
        public TextView list_texv, result_texv, delete_texv;

        public GridViewHolder(View v) {
            super(v);
            delete_texv = (TextView) v.findViewById(R.id.result_delete);
            list_texv = (TextView) v.findViewById(R.id.list_txt);
            result_texv = (TextView) v.findViewById(R.id.result_txt);
        }
    }
}
