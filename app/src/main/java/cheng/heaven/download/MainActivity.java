package cheng.heaven.download;

import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DownloadAdapter adapter;
    List<String> list = new ArrayList<>();
    String[] item = {"one", "two", "three", "four", "five"};
    static String url = "http://dldir1.qq.com/qqfile/QQforMac/QQ_V5.0.2.dmg";
    static String path = Environment.getExternalStorageDirectory() + File.separator + "QQ.dmg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = Arrays.asList(item);
        adapter = new DownloadAdapter(this);
        adapter.setList(list);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    public static void clickDownload(final DownloadAdapter.ViewHolder holder) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    InputStream is = response.body().byteStream();
                    long length = response.body().contentLength();
                    Log.e("length", length+"");
                    FileOutputStream fos = null;
                    fos = new FileOutputStream(new File(path));
                    byte[] buffer = new byte[2048];
                    int len = 0;
                    long sum = 0;
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                        sum = sum + len;
                        long total = sum*100/length;
//                        Log.i("total11111", total+"");
                        Message message = DownloadAdapter.mHandler.obtainMessage(DownloadAdapter.MSG_NOTIFY_PROGRESSBAR);
                        message.obj = holder;
                        message.arg1 = (int) total;
                        DownloadAdapter.mHandler.sendMessage(message);
                    }
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("file download success", "文件下载成功");
            }
        });
    }
}
