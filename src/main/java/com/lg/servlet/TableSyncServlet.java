package com.lg.servlet;

import com.lg.dbsync.TableSyncManage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class TableSyncServlet extends HttpServlet {

    private String url;

    public TableSyncServlet(String url) {
        this.url = url;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        try {
            out.write(TableSyncManage.status(url));
            out.flush();
        } finally {
            out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        try {
            out.write(TableSyncManage.status(url, req.getParameter("beanName"), req.getParameter("field"), req.getParameter("value")));
            out.flush();
        } finally {
            out.close();
        }
    }
}
