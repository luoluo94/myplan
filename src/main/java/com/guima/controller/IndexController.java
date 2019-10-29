package com.guima.controller;

import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;

/**
 * Created by Ran on 2019/9/23.
 */
public class IndexController extends Controller {

    private String path = PropKit.use("controller.properties").get("path");

    public void index()
    {
        render("/index.jsp");
    }
}
