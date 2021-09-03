package tech.brainco.bstardemo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import tech.brainco.bstarblesdk.core.BstarDevice;
import tech.brainco.bstarblesdk.core.BstarSDK;
import tech.brainco.bstarblesdk.core.Callback;
import tech.brainco.bstarblesdk.core.Result;
import tech.brainco.bstardemo.databinding.ActivityConnectedDevicesBinding;
import tech.brainco.bstardemo.databinding.ItemDevicesBinding;
import timber.log.Timber;

public class DevicesActivity extends AppCompatActivity {

    private RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;
    @SuppressWarnings("NotNullFieldNotInitialized")
    private @NonNull
    List<BstarDevice> connectedDevices;

    private final Map<String, Integer> maps = new HashMap<>();

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BstarSDK.scanDevices(new Result<List<String>>() {
            @Override
            public void onResult(List<String> strings) {

            }

            @Override
            public void onError(@NonNull Throwable throwable) {

            }
        });
        ActivityConnectedDevicesBinding binding = ActivityConnectedDevicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Timber.i("onCreate");
        binding.rv.setLayoutManager(new LinearLayoutManager(this));
        connectedDevices = BstarSDK.getDevices();
        BstarSDK.setBstarDevicesListener(result -> {
            connectedDevices = result;
            mAdapter.notifyDataSetChanged();
        });
        mAdapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ItemDevicesBinding itemBinding = ItemDevicesBinding.inflate(getLayoutInflater(), parent, false);
                itemBinding.getRoot().setTag(itemBinding);
                return new RecyclerView.ViewHolder(itemBinding.getRoot()) {
                };
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ItemDevicesBinding binding = (ItemDevicesBinding) holder.itemView.getTag();
                binding.tv.setText(connectedDevices.get(position).getId());
                BstarDevice device = connectedDevices.get(position);
                setText(binding, device);
                device.setAttentionListener(result -> {
                    if (holder.getAbsoluteAdapterPosition() == position) {
                        Timber.d("on attention %s", result);
                        Integer count = maps.get(device.getId());
                        if (count == null) {
                            count = 0;
                        }
                        count++;
                        maps.put(device.getId(), count);
                        binding.tv2.setText("attention " + String.format(Locale.getDefault(), "%.2f", result));
                        setText(binding, device);
                    }
                });
                device.setContactStateChangeListener(result -> {
                    if (holder.getAbsoluteAdapterPosition() == position) {
                        setText(binding, device);
                    }
                });
                device.setConnectStateChangeListener(result -> {
                    if (holder.getAbsoluteAdapterPosition() == position) {
                        setText(binding, device);
                    }
                });
            }
            @SuppressLint("SetTextI18n")
            private void setText(ItemDevicesBinding binding, BstarDevice device) {
                binding.tvConnect.setText("connected " + device.getConnected());
                binding.tvContact.setText("contacted " + device.getContacted());
                binding.tvVersion.setText("version " + device.getFirmwareVersion());
            }

            @Override
            public int getItemCount() {
                return connectedDevices.size();
            }
        };
        binding.rv.setAdapter(mAdapter);
        binding.btn1.setOnClickListener(v -> {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.show();
            BstarSDK.init(this, dialog::dismiss);
        });
        binding.btn2.setOnClickListener(v -> BstarSDK.release());
    }

}
