package cheng.heaven.download;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenglei Created by chenglei on 16/7/19.
 */
public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder>{

    public static final int MSG_NOTIFY_PROGRESSBAR = 1;
    public static final int MSG_NOTIFY_TV = 2;
    static final String OUT = "%/100%";
    AppCompatActivity mActivity;
    List<String> list = new ArrayList<>();
    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_NOTIFY_PROGRESSBAR:
                    ViewHolder holder = (ViewHolder) msg.obj;
                    int progress = msg.arg1;
                    holder.pb.setProgress(progress);
                    holder.progress.setText(progress + OUT);
                    if (progress == 100) {
                        holder.tv.setText("下载完成");
                        holder.btn.setText("delete");
                    }
                    break;
                case MSG_NOTIFY_TV:
                    ViewHolder viewHolder = (ViewHolder) msg.obj;
                    Bundle bundle = msg.getData();
                    String value = (String) bundle.get("key");
                    viewHolder.tv.setText(value);
                    break;
            }
        }
    };

    public void setList(List<String> list) {
        this.list = list;
    }

    public DownloadAdapter(@NonNull AppCompatActivity activity) {
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tv.setText(list.get(position));
        holder.pb.setMax(100);
        holder.progress.setVisibility(View.GONE);
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.btn.getText().equals("CLICK")) {
                    holder.progress.setVisibility(View.VISIBLE);
                    MainActivity.clickDownload(holder, MainActivity.url, MainActivity.path);
                } else if (holder.btn.getText().equals("delete")){
                    File file = new File(MainActivity.path);
                    if (file.exists()) {
                        if (file.delete()) {
                            holder.tv.setText("删除成功");
                            holder.btn.setText("CLICK");
                            holder.progress.setVisibility(View.GONE);
                            holder.pb.setProgress(0);
                            Message message = mHandler.obtainMessage(MSG_NOTIFY_TV);
                            message.obj = holder;
                            Bundle bundle = new Bundle();
                            bundle.putString("key", list.get(position));
                            message.setData(bundle);
                            mHandler.sendMessageDelayed(message, 2000);
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public Button btn;
        public ProgressBar pb;
        public TextView tv;
        public TextView progress;

        public ViewHolder(View itemView) {
            super(itemView);
            btn = (Button) itemView.findViewById(R.id.btn);
            pb = (ProgressBar) itemView.findViewById(R.id.pb);
            tv = (TextView) itemView.findViewById(R.id.tv);
            progress = (TextView) itemView.findViewById(R.id.progress);
        }
    }
}
