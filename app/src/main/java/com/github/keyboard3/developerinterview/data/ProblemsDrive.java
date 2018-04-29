package com.github.keyboard3.developerinterview.data;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;

import com.github.keyboard3.developerinterview.ConfigConst;
import com.github.keyboard3.developerinterview.callback.Callback;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.http.HttpClient;
import com.github.keyboard3.developerinterview.pattern.BaseProblemState;
import com.github.keyboard3.developerinterview.util.ListUtil;
import com.google.common.io.CharStreams;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author keyboard3
 * @date 2017/9/8
 */

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class ProblemsDrive {
    private static final String TAG = "DataFactory";
    /**
     * 存入了所有类型的题目集合
     */
    private static Map<Integer, SparseArray<Problem>> typeProblemsMap = new ArrayMap<>();
    private SparseArray<Problem> problemSparseArray;
    public List<Problem> problemList;
    private BaseProblemState problemState;
    private Context applicationContext;
    public String problemJsonPath;

    public ProblemsDrive(Context applicationContext, BaseProblemState problemState) {
        if (problemState == null || applicationContext == null) {
            return;
        }
        this.applicationContext = applicationContext;
        this.problemState = problemState;
        problemJsonPath = ConfigConst.STORAGE_DIRECTORY + "/" + problemState.getTypeStr() + "/" + problemState.getTypeStr() + ".json";

        File dir = new File(problemJsonPath);
        if (!dir.getParentFile().exists()) {
            dir.getParentFile().mkdirs();
        }
    }

    public void initProblemsByType() throws IOException {
        if (problemSparseArray == null)
            problemSparseArray = typeProblemsMap.get(problemState.getType());
        if (problemSparseArray == null)
            asyncFetchProblems(null);

    }

    public void asyncFetchProblems(final Callback<List<Problem>> callback) {
        problemSparseArray = new SparseArray<>();
        final Gson gson = new Gson();
        final File file = new File(problemJsonPath);

        Logger.d("asyncFetchProblems file.exists():"+file.exists());

        try {
            if (file.exists()) {
                InputStream inputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                String content = CharStreams.toString(inputStreamReader);
                problemList = new Gson().fromJson(content, new TypeToken<List<Problem>>(){}.getType());

                saveMemoryCache(problemList);
                if (callback != null) callback.success(problemList);
            } else {
                HttpClient.getInstance(applicationContext).getProblems(problemState.getTypeStr() + ".json", new Consumer<List<Problem>>() {
                    @Override
                    public void accept(List<Problem> list) throws Exception {
                        Logger.d("获取成功:" + list.size());
                        if (!ListUtil.isEmpty(list)) {
                            problemList = list;

                            FileOutputStream outputStream = new FileOutputStream(file);
                            outputStream.write(gson.toJson(list).getBytes());
                            outputStream.close();
                        }

                        saveMemoryCache(list);
                        if (callback != null) callback.success(list);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        callback.fail(throwable);
                    }
                });
            }
        } catch (IOException e) {
            callback.fail(e);
        }
    }

    private void saveMemoryCache(List<Problem> list) {
        for (Problem item : list) {
            problemSparseArray.put(item.getId(), item);
        }
        typeProblemsMap.put(problemState.getType(), problemSparseArray);
    }

    /**
     * 异步查询题目
     *
     * @param id
     * @return
     */
    public void asyncQueryProblem(String id, final Callback callback) {
        Integer Id = Integer.parseInt(id);
        io.reactivex.Observable.just(Id)
                .observeOn(Schedulers.io())
                .flatMap(new Function<Integer, ObservableSource<Problem>>() {
                    @Override
                    public ObservableSource<Problem> apply(Integer id) throws Exception {

                        return (ObservableSource<Problem>) problemSparseArray.get(id);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Problem>() {
                    @Override
                    public void accept(Problem problem) throws Exception {
                        callback.success(problem);
                    }
                });
    }

    public Problem queryProblem(String sid) throws IOException {
        Integer id = Integer.parseInt(sid);
        initProblemsByType();
        return problemSparseArray.get(id);
    }
}