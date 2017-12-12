package com.ufu.bomfim.aluno.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ufu.bomfim.aluno.R;
import com.ufu.bomfim.aluno.helper.AlunoDbHelper;
import com.ufu.bomfim.aluno.model.Aluno;
import com.ufu.bomfim.aluno.util.CircleTransform;

import java.util.List;

public class AlunosAdapter extends BaseAdapter {

    private static final String TAG = "AlunosAdapter";

    private Holder holder;
    private Context mContext;
    private List<Aluno> mAlunosList;

    public AlunosAdapter(Context context, List<Aluno> alunoList) {
        mContext = context;
        mAlunosList = alunoList;
    }

    @Override
    public int getCount() {
        return mAlunosList != null ? mAlunosList.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return mAlunosList != null ? mAlunosList.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return  mAlunosList != null ? mAlunosList.get(i).getId() : 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.aluno_item, null);
            holder = new Holder(view);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        Aluno aluno = mAlunosList.get(i);
        loadAlunoImage(aluno.getImagePath());
        holder.name.setText(aluno.getNome());

        return view;
    }

    private void loadAlunoImage(String path) {
        if(path != null && !path.isEmpty()) {
            Uri imageUri = Uri.parse(path);
            Picasso.with(mContext)
                    .load(imageUri)
                    .placeholder(ContextCompat.getDrawable(mContext, R.drawable.minha_imagem))
                    .transform(new CircleTransform())
                    .into(holder.image);
        }
    }

    public void refreshAdapter()
    {
        mAlunosList.clear();
        mAlunosList = AlunoDbHelper.getInstance().getAllAlunos();
        notifyDataSetChanged();
    }

    private class Holder {

        private ImageView image;
        private TextView name;

        private Holder(View view) {
            image = (ImageView) view.findViewById(R.id.ImageViewAlunoID);
            name = (TextView) view.findViewById(R.id.TextViewAlunoID);
        }
    }
}
