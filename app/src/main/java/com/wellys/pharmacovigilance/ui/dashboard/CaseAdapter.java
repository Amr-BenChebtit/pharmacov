package com.wellys.pharmacovigilance.ui.dashboard;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.wellys.pharmacovigilance.data.local.entity.CaseEntity;
import com.wellys.pharmacovigilance.databinding.ItemCaseBinding;
import com.wellys.pharmacovigilance.util.DateFormatter;

public class CaseAdapter extends ListAdapter<CaseEntity, CaseAdapter.VH> {

    public interface OnCaseClick {
        void onClick(@NonNull CaseEntity c);
    }

    private final OnCaseClick onClick;

    public CaseAdapter(@NonNull OnCaseClick onClick) {
        super(DIFF);
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCaseBinding b = ItemCaseBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(getItem(position), onClick);
    }

    static class VH extends RecyclerView.ViewHolder {
        private final ItemCaseBinding binding;

        VH(ItemCaseBinding b) {
            super(b.getRoot());
            this.binding = b;
        }

        void bind(@NonNull CaseEntity c, @NonNull OnCaseClick onClick) {
            binding.productName.setText(c.productName);
            binding.submittedAt.setText(DateFormatter.relative(c.submittedAt));
            binding.statusPill.setStatus(c.getStatusEnum());
            binding.severityStrip.setBackgroundColor(severityColor(c));

            binding.getRoot().setOnClickListener(v -> onClick.onClick(c));
        }

        private int severityColor(@NonNull CaseEntity c) {
            int resId;
            switch (c.getSeverityEnum()) {
                case SERIOUS:
                    resId = com.wellys.pharmacovigilance.R.color.severity_serious;
                    break;
                case LIFE_THREATENING:
                    resId = com.wellys.pharmacovigilance.R.color.severity_life_threatening;
                    break;
                case FATAL:
                    resId = com.wellys.pharmacovigilance.R.color.severity_fatal;
                    break;
                case MINOR:
                default:
                    resId = com.wellys.pharmacovigilance.R.color.severity_minor;
                    break;
            }
            return binding.getRoot().getContext().getColor(resId);
        }
    }

    private static final DiffUtil.ItemCallback<CaseEntity> DIFF = new DiffUtil.ItemCallback<CaseEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull CaseEntity a, @NonNull CaseEntity b) {
            return a.id == b.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull CaseEntity a, @NonNull CaseEntity b) {
            // Important: compare all fields that affect the rendering, especially status,
            // since the fake workflow flips it after submission.
            return a.id == b.id
                && a.submittedAt == b.submittedAt
                && a.productName.equals(b.productName)
                && a.status.equals(b.status)
                && a.severity.equals(b.severity);
        }
    };
}
