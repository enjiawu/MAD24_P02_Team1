package sg.edu.np.mad.pocketchef.Adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.pocketchef.R;

public class PostInstructionsAdapter extends RecyclerView.Adapter<PostInstructionsViewHolder> {
    Context context;
    List<TextInputLayout> instructions = new ArrayList<>();

    public PostInstructionsAdapter(Context context, List<TextInputLayout> instructions) {
        this.context = context;
        this.instructions = instructions;
    }

    @NonNull
    @Override
    public PostInstructionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostInstructionsViewHolder(LayoutInflater.from(context).inflate(R.layout.input_box, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostInstructionsViewHolder holder, int position) {
        addInstruction();

        TextInputEditText editText = holder.inputLayout.findViewById(R.id.input);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                TextInputLayout parentLayout = (TextInputLayout) editText.getParent().getParent();
                if (s.toString().trim().isEmpty()) {
                    parentLayout.setError("Please fill this field");
                } else {
                    parentLayout.setError(null);
                    parentLayout.setErrorEnabled(false);
                }
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (editText.getText().toString().trim().isEmpty()) {
                        if (instructions.size() > 1) {
                            instructions.remove(position);
                            notifyItemRemoved(position);
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    public int getItemCount() {
        return instructions.size();
    }

    public TextInputLayout getItem(int position) {
        return instructions.get(position);
    }

    public void addInstruction() {
        TextInputLayout inputLayout = (TextInputLayout) LayoutInflater.from(context).inflate(R.layout.input_box, null);
        TextInputEditText editText = inputLayout.findViewById(R.id.input);
        instructions.add(inputLayout);
    }
}

class PostInstructionsViewHolder extends RecyclerView.ViewHolder {
    public TextInputLayout inputLayout;

    public PostInstructionsViewHolder(View itemView) {
        super(itemView);
        inputLayout = itemView.findViewById(R.id.inputBox);
    }
}