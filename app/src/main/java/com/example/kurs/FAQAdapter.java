package com.example.kurs;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class FAQAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ANSWER = 1;

    private Context context;
    private List<Object> items; // Список для заголовков и ответов

    public FAQAdapter(Context context, List<String> questions, LinkedHashMap<String, String> faqMap) {
        this.context = context;
        this.items = new ArrayList<>();
        for (String question : questions) {
            items.add(question);
            items.add(faqMap.get(question));
        }
        Log.d("FAQAdapter", "Initialized with " + questions.size() + " questions");
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? TYPE_HEADER : TYPE_ANSWER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.faq_group, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.faq_child, parent, false);
            return new AnswerViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            String question = (String) items.get(position);
            ((HeaderViewHolder) holder).questionTextView.setText(question);
            Log.d("FAQAdapter", "Binding header " + position + ": " + question);
        } else {
            String answer = (String) items.get(position);
            ((AnswerViewHolder) holder).answerTextView.setText(answer);
            Log.d("FAQAdapter", "Binding answer " + position + ": " + answer);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView questionTextView;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.faqQuestionTextView);
        }
    }

    static class AnswerViewHolder extends RecyclerView.ViewHolder {
        TextView answerTextView;

        AnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            answerTextView = itemView.findViewById(R.id.faqAnswerTextView);
        }
    }
}