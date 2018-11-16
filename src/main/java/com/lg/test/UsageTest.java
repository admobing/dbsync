package com.lg.test;

import com.lg.db.Filter;
import com.lg.db.Update;
import com.lg.dbsync.TableSync;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class UsageTest {

    public List list() {
        return Arrays.asList(new User("zhangsan"), new User("lisi"));
    }

    public void update(Object obj) {
        System.out.println(obj);
    }

    public static void main(String[] args) {
        Executors.newCachedThreadPool().execute(() -> {
            try {
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        UsageTest test = new UsageTest();
        TableSync task = new TableSync(test::list, test::update);
        task.delegate();

        task.update(Update.create().inc("intV", 1), Filter.create().eq("name", "zhangsan"));
        task.update(Update.create().inc("intv", 1), Filter.create().eq("name", "zhangsan"));
        task.update(Update.create().inc("floatV", 1.0f), Filter.create().eq("name", "zhangsan"));
        task.update(Update.create().inc("floatv", 1.1f), Filter.create().eq("name", "zhangsan"));
        task.update(Update.create().inc("doubleV", 2.0d), Filter.create().eq("name", "zhangsan"));
        task.update(Update.create().inc("doublev", 3.0d), Filter.create().eq("name", "zhangsan"));
        task.update(Update.create().inc("bigDecimal", new BigDecimal(1.0)), Filter.create().eq("name", "zhangsan"));

    }

}
